package com.example.stocksheet.analytics.dto

data class PortfolioSummaryDTO(
    val totalValue: Long,
    val totalIncome: Long,
    val investedCapital: Long,
    val todayIncome: Long,
)
