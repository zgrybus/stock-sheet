package com.example.stocksheet.stocks.mappers

import com.example.stocksheet.stocks.dto.StockDTO
import com.example.stocksheet.stocks.entity.StockEntity
import org.springframework.stereotype.Component

@Component
class StockMapper {
    fun toEntity(dto: StockDTO): StockEntity =
        StockEntity(
            name = dto.name,
            symbol = dto.symbol,
            exchange = dto.exchange,
            industry = dto.industry,
            dividend = dto.dividend,
            dividendFrequency = dto.dividendFrequency,
            price = dto.price,
        )
}
