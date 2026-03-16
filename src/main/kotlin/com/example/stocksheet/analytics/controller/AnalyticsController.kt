package com.example.stocksheet.analytics.controller

import com.example.stocksheet.analytics.dto.PortfolioSummaryDTO
import com.example.stocksheet.analytics.service.AnalyticsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/analytics/{portfolioId}")
class AnalyticsController(
    private val analyticsService: AnalyticsService,
) {
    @GetMapping(value = ["/summary"], produces = ["application/json"])
    fun generatePortfolioSummary(
        @PathVariable("portfolioId") portfolioId: Long,
    ): PortfolioSummaryDTO = analyticsService.generatePortfolioSummary(portfolioId)
}
