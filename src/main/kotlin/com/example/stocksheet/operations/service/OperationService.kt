package com.example.stocksheet.operations.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.operations.dto.HoldingPositionDTO
import com.example.stocksheet.operations.dto.OperationImportResponseDTO
import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.dto.PortfolioHoldingsDTO
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@Service
class OperationService(
    private val operationRepository: OperationRepository,
    private val portfolioRepository: PortfolioRepository,
) : Loggable {
    @Transactional(readOnly = true)
    fun getHoldings(portfolioId: Long): PortfolioHoldingsDTO {
        logger.info { "Generating portfolio summary for portfolio: $portfolioId" }
        // TODO
        // VALIDATE PORTFOLIO ID and add test
        val operations = operationRepository.findAllByPortfolioId(portfolioId)

        logger.info { "Portfolio summary generated for $portfolioId" }

        val items =
            operations
                .groupingBy { it.stockSymbol }
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

        logger.info { "Portfolio summary generated for $portfolioId" }

        return response
    }

    @Transactional()
    fun importOperations(
        batch: OperationsBatchRequestDTO,
        portfolioId: Long,
    ): OperationImportResponseDTO {
        logger.info { "Processing batch import for portfolioId - $portfolioId" }

        // TODO
        // VALIDATE PORTFOLIO ID and add test
        val portfolio =
            portfolioRepository.findByIdOrNull(portfolioId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio not found with id: $portfolioId")

        val requestedExternalIds = batch.operations?.mapNotNull { it.externalId } ?: emptyList()
        val existingEntities = operationRepository.findAllByExternalIdIn(requestedExternalIds)
        val existingIdsMap = existingEntities.associateBy { it.externalId }

        val (duplicatedOperations, newOperations) =
            (batch.operations ?: emptyList()).partition {
                existingIdsMap.containsKey(
                    it.externalId,
                )
            }

        val savedOperations = operationRepository.saveAll(newOperations.map { it.toEntity(portfolio) })

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
