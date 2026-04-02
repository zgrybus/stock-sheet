package com.example.stocksheet.operations.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
import com.example.stocksheet.operations.dto.OperationsImportResponseDTO
import com.example.stocksheet.operations.dto.PortfolioHoldingsResponseDTO
import com.example.stocksheet.operations.exceptions.OperationsBatchEmptyException
import com.example.stocksheet.operations.mappers.OperationsMapper
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.exceptions.PortfolioNotFoundException
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import com.example.stocksheet.stocks.service.StockService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OperationService(
    private val operationRepository: OperationRepository,
    private val portfolioRepository: PortfolioRepository,
    private val operationsMapper: OperationsMapper,
    private val stockService: StockService,
) : Loggable {
    @Transactional(readOnly = true)
    fun getHoldings(portfolioId: Long): PortfolioHoldingsResponseDTO {
        logger.info { "Generating portfolio holdings for portfolio: $portfolioId" }

        if (!portfolioRepository.existsById(portfolioId)) {
            throw PortfolioNotFoundException("Could not find portfolio with id $portfolioId")
        }

        val items =
            operationRepository.getHoldingsSummaryByPortfolioId(portfolioId).map {
                PortfolioHoldingsResponseDTO.PositionDTO(
                    stockSymbol = it.stockSymbol,
                    totalVolume = it.totalVolume,
                    totalCost = it.totalCost,
                )
            }
        val response = PortfolioHoldingsResponseDTO(portfolioId, items)

        logger.info { "Portfolio holdings generated for $portfolioId" }

        return response
    }

    @Transactional()
    fun importOperations(
        batch: OperationsImportRequestDTO,
        portfolioId: Long,
    ): OperationsImportResponseDTO {
        logger.info { "Processing batch import for portfolioId - $portfolioId" }

        val operations = batch.operations

        val portfolio =
            portfolioRepository.findByIdOrNull(portfolioId)
                ?: throw PortfolioNotFoundException("Could not find portfolio with id $portfolioId")

        // TODO: move logic to sql - if possible
        val requestedExternalIds = operations.map { it.externalId }
        val existingEntities = operationRepository.findAllByExternalIdIn(requestedExternalIds)
        val existingIdsMap = existingEntities.associateBy { it.externalId }

        val (duplicatedOperations, newOperations) = operations.partition { existingIdsMap.containsKey(it.externalId) }

        if (newOperations.isEmpty()) {
            throw OperationsBatchEmptyException("Could not find new operations")
        }

        val savedOperations =
            operationRepository.saveAll(
                newOperations.map { operation ->
                    // TODO: optimize it
                    val stock = stockService.getStock(operation.stockSymbol)
                    operationsMapper.toEntity(operation, portfolio, stock)
                },
            )

        logger.info { "Import finished. Added: ${savedOperations.size}, Duplicated: ${duplicatedOperations.size}" }

        return OperationsImportResponseDTO(
            added =
                savedOperations.map {
                    OperationsImportResponseDTO.OperationSummaryDTO(
                        id = it.id!!,
                        externalId = it.externalId,
                    )
                },
            duplicated =
                duplicatedOperations.map { operation ->
                    val idFromDb = existingIdsMap[operation.externalId]?.id
                    OperationsImportResponseDTO.OperationSummaryDTO(id = idFromDb!!, externalId = operation.externalId)
                },
        )
    }
}
