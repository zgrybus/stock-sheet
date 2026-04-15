package com.example.stocksheet.analytics.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.analytics.dto.PortfolioSummaryResponseDTO
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.exceptions.PortfolioNotFoundException
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.RoundingMode
import java.time.LocalDate

@Service
class AnalyticsService(
    private val operationRepository: OperationRepository,
    private val portfolioRepository: PortfolioRepository,
) : Loggable {
    @Transactional(readOnly = true)
    fun generatePortfolioSummary(portfolioId: Long): PortfolioSummaryResponseDTO {
        logger.info { "Attempt to generate portfolio summary for id: $portfolioId" }

        portfolioRepository.findByIdOrNull(portfolioId)
            ?: throw PortfolioNotFoundException("Could not find portfolio with id $portfolioId")

        val summary = operationRepository.calculatePortfolioSummaryByPortfolioId(portfolioId)
        val snapshot = operationRepository.calculateValuationSnapshotByPortfolioId(portfolioId, LocalDate.now().minusDays(1))

        val totalValue = summary.totalValue.setScale(2, RoundingMode.HALF_EVEN)
        val investedCapital = summary.investedCapital.setScale(2, RoundingMode.HALF_EVEN)

        val todayIncome = (snapshot.currentValue - snapshot.historicalValue).setScale(2, RoundingMode.HALF_EVEN)
        val totalIncome = (totalValue - investedCapital).setScale(2, RoundingMode.HALF_EVEN)

        logger.info { "Portfolio summary successfully generated for $portfolioId" }

        return PortfolioSummaryResponseDTO(
            todayIncome = todayIncome,
            totalIncome = totalIncome,
            totalValue = totalValue,
            investedCapital = investedCapital,
        )
    }
}
