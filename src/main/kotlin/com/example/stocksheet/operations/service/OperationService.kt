package com.example.stocksheet.operations.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.operations.dto.OperationImportResponseDTO
import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.dto.PortfolioSummaryDTO
import com.example.stocksheet.operations.dto.StockPositionDTO
import com.example.stocksheet.operations.mapper.toEntity
import com.example.stocksheet.operations.repository.OperationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OperationService(
    private val operationRepository: OperationRepository,
) : Loggable {
    @Transactional(readOnly = true)
    fun getPortfolioSummary(currency: String): PortfolioSummaryDTO {
        logger.info { "Generating portfolio summary for currency: $currency" }

        return operationRepository
            .findAllByCurrency(currency)
            .let { operations ->
                val items =
                    operations
                        .groupingBy {
                            it.stockSymbol
                        }.fold(
                            {
                                key,
                                _,
                                ->
                                StockPositionDTO(key, BigDecimal.ZERO, BigDecimal.ZERO)
                            },
                            { _, acc, element ->
                                acc.copy(
                                    totalVolume = acc.totalVolume.add(element.volume),
                                    totalCost = acc.totalCost.add(element.totalPrice),
                                )
                            },
                        ).values
                        .toList()

                PortfolioSummaryDTO(currency, items)
            }.also { logger.info { "Portfolio summary generated for $currency" } }
    }

    @Transactional()
    fun addOperations(batch: OperationsBatchRequestDTO): OperationImportResponseDTO {
        logger.info { "Processing batch import for currency: ${batch.currency}" }

        val requestedExternalIds = batch.operations?.mapNotNull { it.externalId } ?: emptyList()
        val existingEntities = operationRepository.findAllByExternalIdIn(requestedExternalIds)
        val existingIdsMap = existingEntities.associateBy { it.externalId }

        val (duplicatedOperations, newOperations) =
            (batch.operations ?: emptyList()).partition {
                existingIdsMap.containsKey(
                    it.externalId,
                )
            }

        val savedOperations = operationRepository.saveAll(newOperations.map { it.toEntity(batch.currency!!) })

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
