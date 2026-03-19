package com.example.stocksheet.operations.dto

import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.validation.ValidTotalPrice
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.stocks.entity.StockEntity
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.Instant

data class OperationsImportRequestDTO(
    @field:NotEmpty(message = "Operations list cannot be empty")
    @field:Valid
    var operations: List<OperationImportRequestDTO>? = null,
)

@ValidTotalPrice(message = "Total Price must be equal to Volume * Price Per Volume")
data class OperationImportRequestDTO(
    @field:NotBlank(message = "External ID is required")
    var externalId: String? = null,
    @field:NotBlank(message = "Symbol is required")
    var symbol: String? = null,
    @field:NotBlank(message = "Exchange is required")
    var exchange: String? = null,
    @field:NotNull(message = "Type is required")
    var type: OperationType? = null,
    @field:NotNull(message = "Volume is required")
    @field:Positive(message = "Volume needs to be positive")
    var volume: BigDecimal? = null,
    @field:NotNull(message = "Open Date is required")
    @field:Past(message = "Open Date needs to be date from the past")
    var openDate: Instant? = null,
    @field:NotNull(message = "Price Per Volume is required")
    @field:Positive(message = "Price Per Volume needs to be positive")
    var pricePerVolume: BigDecimal? = null,
    @field:NotNull(message = "Total Price is required")
    @field:Positive(message = "Total Price needs to be positive")
    var totalPrice: BigDecimal? = null,
) {
    fun toEntity(
        portfolio: PortfolioEntity,
        stock: StockEntity,
    ): OperationEntity =
        OperationEntity(
            externalId = requireNotNull(this.externalId),
            stock = stock,
            type = requireNotNull(this.type),
            volume = requireNotNull(this.volume),
            openDate = requireNotNull(this.openDate),
            pricePerVolume = requireNotNull(this.pricePerVolume),
            totalPrice = requireNotNull(this.totalPrice),
            portfolio = portfolio,
        )
}
