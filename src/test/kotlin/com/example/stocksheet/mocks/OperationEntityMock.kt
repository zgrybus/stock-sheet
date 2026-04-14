package com.example.stocksheet.mocks

import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.stocks.entity.StockEntity
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

fun createMockOperationEntity(
    id: Long? = null,
    externalId: String = "external-id-1",
    stock: StockEntity = createMockStockEntity(),
    type: OperationType = OperationType.BUY,
    volume: BigDecimal = BigDecimal("10.00"),
    pricePerVolume: BigDecimal = BigDecimal("150.00"),
    totalPrice: BigDecimal? = null,
    openDate: Instant = LocalDateTime.of(2024, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
    portfolio: PortfolioEntity = createMockPortfolioEntity(),
) = OperationEntity(
    id = id,
    externalId = externalId,
    stock = stock,
    type = type,
    volume = volume,
    openDate = openDate,
    pricePerVolume = pricePerVolume,
    totalPrice = totalPrice ?: volume.multiply(pricePerVolume),
    portfolio = portfolio,
)
