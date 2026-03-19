package com.example.stocksheet.operations.dto

import java.math.BigDecimal

data class PortfolioHoldingsResponseDTO(
    val portfolioId: Long,
    val positions: List<PositionDTO>,
) {
    data class PositionDTO(
        val stockSymbol: String,
        val totalVolume: BigDecimal,
        val totalCost: BigDecimal,
    )
}
