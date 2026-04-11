package com.example.stocksheet.stocks.dto

import com.example.stocksheet.stocks.entity.DividendFrequency
import java.math.BigDecimal

data class StockDTO(
    val name: String,
    val symbol: String,
    val exchange: String,
    val industry: String,
    val price: BigDecimal,
    val dividendFrequency: DividendFrequency,
    val dividend: BigDecimal,
)
