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
    fun addOperations(operations: OperationsBatchRequestDTO): OperationImportResponseDTO {
        logger.info { "Adding operations: $operations" }

        val notDuplicatedOperations = operations
        val duplicated = operations.operations

        val added =
            operationRepository
                .saveAll(notDuplicatedOperations.toEntity())
                .also {
                    logger.info { "Added new operations: $notDuplicatedOperations" }
                }.toList()

        return OperationImportResponseDTO(
            added =
                added.map {
                    OperationImportResponseDTO.OperationSummaryDTO(
                        id = it.id,
                        externalId = it.externalId,
                    )
                },
            duplicated = listOf(),
        )
    }
}
