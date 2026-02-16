package com.example.stocksheet.operations.dto

import java.math.BigDecimal

data class PortfolioHoldingsDTO(
    val portfolioId: Long,
    val positions: List<HoldingPositionDTO>,
)

data class HoldingPositionDTO(
    val stockSymbol: String,
    val totalVolume: BigDecimal,
    val totalCost: BigDecimal,
)
