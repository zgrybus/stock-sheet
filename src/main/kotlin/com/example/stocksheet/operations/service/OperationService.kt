package com.example.stocksheet.operations.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.dto.PortfolioSummaryDTO
import com.example.stocksheet.operations.dto.StockPositionDTO
import com.example.stocksheet.operations.repository.OperationRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class OperationService(
    private val operationRepository: OperationRepository,
) : Loggable {
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

    fun addOperations(operations: OperationsBatchRequestDTO) {}
}
