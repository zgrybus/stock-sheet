package com.example.stocksheet.mocks

import com.example.stocksheet.stocks.entity.DividendFrequency
import com.example.stocksheet.stocks.entity.StockEntity
import java.math.BigDecimal

fun createMockStockEntity(
    id: Long? = null,
    symbol: String = "AAPL",
    name: String = "Apple Inc.",
    exchange: String = "NASDAQ",
    industry: String = "Technology",
    price: BigDecimal = BigDecimal.ZERO.setScale(4),
    dividend: BigDecimal = BigDecimal.ZERO.setScale(4),
    dividendFrequency: DividendFrequency = DividendFrequency.NONE,
): StockEntity =
    StockEntity(
        id = id,
        name = name,
        symbol = symbol,
        exchange = exchange,
        industry = industry,
        price = price,
        dividend = dividend,
        dividendFrequency = dividendFrequency,
    )
