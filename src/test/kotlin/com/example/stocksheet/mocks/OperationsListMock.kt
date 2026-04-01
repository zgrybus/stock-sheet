package com.example.stocksheet.mocks

import com.example.stocksheet.operations.entity.OperationEntity
import java.math.BigDecimal

fun createMockOperationsList(): List<OperationEntity> =
    listOf(
        createMockOperationEntity(
            externalId = "external-id-1",
            volume = BigDecimal("10.00"),
        ),
        createMockOperationEntity(
            externalId = "external-id-2",
            volume = BigDecimal("5.00"),
            pricePerVolume = BigDecimal("80.00"),
        ),
        createMockOperationEntity(
            externalId = "external-id-3",
            volume = BigDecimal("100.00"),
            pricePerVolume = BigDecimal("10.00"),
        ),
        createMockOperationEntity(
            externalId = "external-id-4",
            volume = BigDecimal("50.00"),
            pricePerVolume = BigDecimal("10.00"),
        ),
    )
