package com.example.stocksheet.analytics.dto

import java.math.BigDecimal

data class PortfolioSummaryDTO(
    val totalValue: BigDecimal,
    val totalIncome: BigDecimal,
    val investedCapital: BigDecimal,
    val todayIncome: Long,
)
