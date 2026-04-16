package com.example.stocksheet.mocks

import com.example.stocksheet.stocks.entity.StockEntity
import com.example.stocksheet.stocks.quotes.entity.StockQuoteEntity
import java.math.BigDecimal
import java.time.LocalDate

fun createMockStockQuoteEntityMock(
    id: Long? = null,
    closedPrice: BigDecimal = BigDecimal("50.25"),
    stock: StockEntity = createMockStockEntity(),
    date: LocalDate = LocalDate.now().minusDays(1),
): StockQuoteEntity =
    StockQuoteEntity(
        id = id,
        closedPrice = closedPrice,
        stock = stock,
        date = date,
    )
