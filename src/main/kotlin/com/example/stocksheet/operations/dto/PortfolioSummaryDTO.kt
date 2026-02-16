package com.example.stocksheet.operations.dto

import java.math.BigDecimal

data class PortfolioSummaryDTO(
    val portfolioId: Long,
    val positions: List<StockPositionDTO>,
)

data class StockPositionDTO(
    val stockSymbol: String,
    val totalVolume: BigDecimal,
    val totalCost: BigDecimal,
)
