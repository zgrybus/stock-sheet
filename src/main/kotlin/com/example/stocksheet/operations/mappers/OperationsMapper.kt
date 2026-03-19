package com.example.stocksheet.operations.mappers

import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import org.springframework.stereotype.Component

@Component
class OperationsMapper {
    fun toEntity(
        dto: OperationsImportRequestDTO.OperationRequestDTO,
        portfolio: PortfolioEntity,
    ): OperationEntity =
        OperationEntity(
            externalId = dto.externalId,
            stockSymbol = dto.stockSymbol,
            type = dto.type,
            volume = dto.volume,
            openDate = dto.openDate,
            pricePerVolume = dto.pricePerVolume,
            totalPrice = dto.totalPrice,
            portfolio = portfolio,
        )
}
