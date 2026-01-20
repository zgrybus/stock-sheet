package com.example.stocksheet.operations.dto

import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.validation.ValidTotalPrice
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.Instant

@ValidTotalPrice(message = "Total Price must be equal to Volume * Price Per Volume")
data class OperationRequestDTO(
    @field:NotBlank(message = "External ID is required")
    var externalId: String,
    @field:NotBlank(message = "Stock Symbol is required")
    var stockSymbol: String,
    @field:NotNull(message = "Type is required")
    var type: OperationType,
    @field:NotNull(message = "Volume is required")
    @field:Positive(message = "Volume needs to be positive")
    var volume: BigDecimal,
    @field:NotBlank(message = "Open Date is required")
    @field:Past(message = "Open Date needs to be date from the past")
    var openDate: Instant,
    @field:NotNull(message = "Price Per Volume is required")
    @field:Positive(message = "Price Per Volume needs to be positive")
    var pricePerVolume: BigDecimal,
    @field:NotNull(message = "Total Price is required")
    @field:Positive(message = "Total Price needs to be positive")
    var totalPrice: BigDecimal,
    @field:NotBlank(message = "Currency is required")
    var currency: String,
)
