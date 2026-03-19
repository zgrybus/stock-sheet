package com.example.stocksheet.portfolio.mappers

import com.example.stocksheet.portfolio.dto.PortfolioRequestDTO
import com.example.stocksheet.portfolio.dto.PortfolioResponseDTO
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import org.springframework.stereotype.Component

@Component
class PortfolioMapper {
    fun toEntity(dto: PortfolioRequestDTO): PortfolioEntity = PortfolioEntity(name = dto.name, currency = dto.currency)

    fun toResponseDTO(entity: PortfolioEntity): PortfolioResponseDTO =
        PortfolioResponseDTO(name = entity.name, currency = entity.currency, id = entity.id!!)
}
