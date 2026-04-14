package com.example.stocksheet.operations.dto

import java.math.BigDecimal

data class PortfolioHoldingsResponseDTO(
    val portfolioId: Long,
    val positions: List<PositionDTO>,
) {
    data class PositionDTO(
        val stockSymbol: String,
        val stockName: String,
        val stockPrice: BigDecimal,
        val averagePrice: BigDecimal,
        val totalVolume: BigDecimal,
        val totalCost: BigDecimal,
        val totalProfit: BigDecimal,
        val profitPercentage: BigDecimal,
    )
}
