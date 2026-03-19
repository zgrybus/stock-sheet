package com.example.stocksheet.mocks

import com.example.stocksheet.operations.entity.OperationEntity

fun createMockOperationsList(): List<OperationEntity> =
    listOf(
        createMockOperationEntity(
            externalId = "external-id-1",
            volume = 10.toBigDecimal(),
        ),
        createMockOperationEntity(
            externalId = "external-id-2",
            volume = 5.toBigDecimal(),
            pricePerVolume = 80.toBigDecimal(),
        ),
        createMockOperationEntity(
            externalId = "external-id-3",
            volume = 100.toBigDecimal(),
            pricePerVolume = 10.toBigDecimal(),
        ),
        createMockOperationEntity(
            externalId = "external-id-4",
            volume = 50.toBigDecimal(),
            pricePerVolume = 10.toBigDecimal(),
        ),
    )
