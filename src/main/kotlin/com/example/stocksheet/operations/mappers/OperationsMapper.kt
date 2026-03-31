package com.example.stocksheet.operations.mappers

import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.stocks.entity.StockEntity
import org.springframework.stereotype.Component

@Component
class OperationsMapper {
    fun toEntity(
        dto: OperationsImportRequestDTO.OperationRequestDTO,
        portfolio: PortfolioEntity,
        stock: StockEntity,
    ): OperationEntity =
        OperationEntity(
            externalId = dto.externalId,
            type = dto.type,
            volume = dto.volume,
            openDate = dto.openDate,
            pricePerVolume = dto.pricePerVolume,
            totalPrice = dto.totalPrice,
            portfolio = portfolio,
            stock = stock,
        )
}
