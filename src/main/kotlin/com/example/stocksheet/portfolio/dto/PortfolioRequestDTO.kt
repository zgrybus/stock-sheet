package com.example.stocksheet.portfolio.dto

import com.example.stocksheet.portfolio.validation.ValidCurrency
import jakarta.validation.constraints.NotBlank

data class PortfolioRequestDTO(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String = "",
    @field:NotBlank(message = "Currency cannot be empty")
    @field:ValidCurrency(message = "Currency is not valid")
    val currency: String = "",
)
