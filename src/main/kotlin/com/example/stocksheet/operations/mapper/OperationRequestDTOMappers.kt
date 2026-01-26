package com.example.stocksheet.operations.mapper

import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.entity.OperationEntity

fun OperationsBatchRequestDTO.toEntity(): List<OperationEntity> =
    requireNotNull(this.operations).map {
        OperationEntity(
            externalId = requireNotNull(it.externalId),
            stockSymbol = requireNotNull(it.stockSymbol),
            type = requireNotNull(it.type),
            volume = requireNotNull(it.volume),
            openDate = requireNotNull(it.openDate),
            pricePerVolume = requireNotNull(it.pricePerVolume),
            totalPrice = requireNotNull(it.totalPrice),
            currency = requireNotNull(this.currency),
        )
    }
