package com.example.stocksheet.mocks

import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.stocks.entity.StockEntity
import java.math.BigDecimal
import java.time.Instant

fun createMockOperationEntity(
    externalId: String = "external-id-1",
    stock: StockEntity = createMockStockEntity(),
    type: OperationType = OperationType.BUY,
    volume: BigDecimal = 10.toBigDecimal(),
    pricePerVolume: BigDecimal = 150.toBigDecimal(),
    openDate: Instant = Instant.now(),
    portfolio: PortfolioEntity = createMockPortfolioEntity(),
) = OperationEntity(
    externalId = externalId,
    stock = stock,
    type = type,
    volume = volume,
    openDate = openDate,
    pricePerVolume = pricePerVolume,
    totalPrice = volume * pricePerVolume,
    portfolio = portfolio,
)
