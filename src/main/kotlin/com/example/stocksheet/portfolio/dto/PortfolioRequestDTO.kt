package com.example.stocksheet.portfolio.dto

import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.portfolio.validation.ValidCurrency
import jakarta.validation.constraints.NotBlank

data class PortfolioRequestDTO(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String? = null,
    @field:ValidCurrency(message = "Currency is not valid")
    val currency: String? = null,
) {
    fun toEntity(): PortfolioEntity = PortfolioEntity(name = requireNotNull(this.name), currency = requireNotNull(this.currency))
}
