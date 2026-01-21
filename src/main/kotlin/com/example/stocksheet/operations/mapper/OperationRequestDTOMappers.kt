package com.example.stocksheet.operations.mapper

import com.example.stocksheet.operations.dto.OperationRequestDTO
import com.example.stocksheet.operations.entity.OperationEntity

fun OperationRequestDTO.toEntity(): OperationEntity =
    OperationEntity(
        externalId = this.externalId!!,
        stockSymbol = this.stockSymbol!!,
        type = this.type!!,
        volume = this.volume!!,
        openDate = this.openDate!!,
        pricePerVolume = this.pricePerVolume!!,
        totalPrice = this.totalPrice!!,
        currency = this.currency!!,
    )
