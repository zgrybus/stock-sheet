package com.example.stocksheet.operations.mapper

import com.example.stocksheet.operations.dto.OperationRequestDTO
import com.example.stocksheet.operations.entity.OperationEntity

fun OperationRequestDTO.toEntity(currency: String): OperationEntity =
    OperationEntity(
        externalId = requireNotNull(this.externalId),
        stockSymbol = requireNotNull(this.stockSymbol),
        type = requireNotNull(this.type),
        volume = requireNotNull(this.volume),
        openDate = requireNotNull(this.openDate),
        pricePerVolume = requireNotNull(this.pricePerVolume),
        totalPrice = requireNotNull(this.totalPrice),
        currency = currency,
    )
