package com.example.stocksheet.mocks

import com.example.stocksheet.stocks.entity.StockEntity

fun createMockStockEntity(
    symbol: String = "AAPL",
    name: String = "Apple Inc.",
    exchange: String = "NASDAQ",
    industry: String = "Technology",
): StockEntity =
    StockEntity(
        name = name,
        symbol = symbol,
        exchange = exchange,
        industry = industry,
    )
