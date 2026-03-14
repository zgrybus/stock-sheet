package com.example.stocksheet.analytics.service

import com.example.stocksheet.analytics.dto.PortfolioSummaryDTO
import com.example.stocksheet.operations.repository.OperationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyticsService(
    private val operationRepository: OperationRepository,
) {
    @Transactional(readOnly = true)
    fun getPortfolioSummary(portfolioId: Long): PortfolioSummaryDTO {
    }
}
