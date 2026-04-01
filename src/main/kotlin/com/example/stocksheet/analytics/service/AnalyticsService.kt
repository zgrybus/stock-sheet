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

        val todayIncome = 0.toLong()
        val totalIncome = 0.toBigDecimal()

        val investedCapital = operationRepository.calculateInvestedCapitalByPortfolioId(portfolioId)

        val totalValue = investedCapital.add(totalIncome)

        logger.info { "Portfolio summary generated for $portfolioId" }

        return PortfolioSummaryResponseDTO(
            todayIncome = todayIncome,
            totalIncome = totalIncome,
            totalValue = totalValue,
            investedCapital = investedCapital,
        )
    }
}
