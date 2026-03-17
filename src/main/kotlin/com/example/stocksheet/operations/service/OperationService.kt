package com.example.stocksheet.operations.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.integration.finnhub.service.FinnhubService
import com.example.stocksheet.operations.dto.HoldingPositionDTO
import com.example.stocksheet.operations.dto.OperationImportResponseDTO
import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.dto.PortfolioHoldingsDTO
import com.example.stocksheet.operations.exceptions.OperationsBatchEmptyException
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.exceptions.PortfolioNotFoundException
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import com.example.stocksheet.stocks.repository.StockRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OperationService(
    private val operationRepository: OperationRepository,
    private val portfolioRepository: PortfolioRepository,
    private val stockRepository: StockRepository,
    private val finnhubService: FinnhubService,
) : Loggable {
    @Transactional(readOnly = true)
    fun getHoldings(portfolioId: Long): PortfolioHoldingsDTO {
        logger.info { "Generating portfolio holdings for portfolio: $portfolioId" }

        if (!portfolioRepository.existsById(portfolioId)) {
            throw PortfolioNotFoundException("Could not find portfolio with id $portfolioId")
        }

        val operations = operationRepository.findAllByPortfolioId(portfolioId)

        val items =
            operations
                // TODO: fix and move it to the repository
                .groupingBy { it.stock.symbol }
                .fold(
                    {
                        key,
                        _,
                        ->
                        HoldingPositionDTO(key, BigDecimal.ZERO, BigDecimal.ZERO)
                    },
                    { _, acc, element ->
                        acc.copy(
                            totalVolume = acc.totalVolume.add(element.volume),
                            totalCost = acc.totalCost.add(element.totalPrice),
                        )
                    },
                ).values
                .toList()

        val response = PortfolioHoldingsDTO(portfolioId, items)

        logger.info { "Portfolio holdings generated for $portfolioId" }

        return response
    }

    @Transactional()
    fun importOperations(
        batch: OperationsBatchRequestDTO,
        portfolioId: Long,
    ): OperationImportResponseDTO {
        logger.info { "Processing batch import for portfolioId - $portfolioId" }

        val operations = requireNotNull(batch.operations)

        val portfolio =
            portfolioRepository.findByIdOrNull(portfolioId)
                ?: throw PortfolioNotFoundException("Could not find portfolio with id $portfolioId")

        // TODO: move logic to sql - if possible
        val requestedExternalIds = operations.mapNotNull { it.externalId }
        val existingEntities = operationRepository.findAllByExternalIdIn(requestedExternalIds)
        val existingIdsMap = existingEntities.associateBy { it.externalId }

        val (duplicatedOperations, newOperations) = operations.partition { existingIdsMap.containsKey(it.externalId) }

        if (newOperations.isEmpty()) {
            throw OperationsBatchEmptyException("Could not find new operations")
        }

        val savedOperations =
            operationRepository.saveAll(
                newOperations.map { operation ->
                    val stockSymbol = requireNotNull(operation.stockSymbol)
                    val stock =
                        stockRepository.findBySymbol(stockSymbol) ?: finnhubService.getCompanyProfile2(stockSymbol).let {
                            if (it == null) {
                                throw IllegalArgumentException("No stock found for symbol $stockSymbol")
                            }
                            stockRepository.save(it.toStockEntity(stockSymbol))
                        }
                    operation.toEntity(portfolio, stock)
                },
            )

        logger.info { "Import finished. Added: ${savedOperations.size}, Duplicated: ${duplicatedOperations.size}" }

        return OperationImportResponseDTO(
            added =
                savedOperations.map {
                    OperationImportResponseDTO.OperationSummaryDTO(
                        id = it.id,
                        externalId = it.externalId,
                    )
                },
            duplicated =
                duplicatedOperations.map { operation ->
                    val idFromDb = existingIdsMap[operation.externalId]?.id
                    OperationImportResponseDTO.OperationSummaryDTO(id = idFromDb, externalId = operation.externalId)
                },
        )
    }
}
