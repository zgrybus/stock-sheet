package com.example.stocksheet.mocks

import com.example.stocksheet.stocks.entity.StockEntity

fun createMockStockEntity(
    id: Long? = null,
    symbol: String = "AAPL",
    name: String = "Apple Inc.",
    exchange: String = "NASDAQ",
    industry: String = "Technology",
): StockEntity =
    StockEntity(
        id = id,
        name = name,
        symbol = symbol,
        exchange = exchange,
        industry = industry,
    )
