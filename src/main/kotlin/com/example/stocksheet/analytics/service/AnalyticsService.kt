package com.example.stocksheet.analytics.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.analytics.dto.PortfolioSummaryResponseDTO
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.exceptions.PortfolioNotFoundException
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyticsService(
    private val operationRepository: OperationRepository,
    private val portfolioRepository: PortfolioRepository,
) : Loggable {
    @Transactional(readOnly = true)
    fun generatePortfolioSummary(portfolioId: Long): PortfolioSummaryResponseDTO {
        logger.info { "Attempt to generate portfolio summary" }

        portfolioRepository.findByIdOrNull(portfolioId) ?: throw PortfolioNotFoundException("Could not find portfolio with id $portfolioId")

        // TODO: move logic to sql
        val response =
            operationRepository
                .findAllByPortfolioId(portfolioId)
                .fold(
                    initial =
                        PortfolioSummaryResponseDTO(
                            totalValue = 0.toBigDecimal(),
                            totalIncome = 0.toBigDecimal(),
                            investedCapital = 0.toBigDecimal(),
                            todayIncome = 0,
                        ),
                    operation = { acc, element ->
                        // TODO: calculate proper totalValue, totalIncome nad todayIncome
                        acc.copy(
                            totalValue = acc.totalValue.add(element.totalPrice),
                            investedCapital = acc.investedCapital.add(element.totalPrice),
                        )
                    },
                )

        logger.info { "Portfolio summary generated for $portfolioId" }

        return response
    }
}
