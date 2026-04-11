package com.example.stocksheet.operations.validation

import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
import com.example.stocksheet.operations.entity.OperationType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import jakarta.validation.ConstraintValidatorContext
import java.math.BigDecimal
import java.time.Instant

class TotalPriceValidatorTest : DescribeSpec() {
    init {
        val validator = TotalPriceValidator()
        val context = mockk<ConstraintValidatorContext>(relaxed = true)

        fun createDTO(
            volume: String,
            price: String,
            total: String,
        ) = OperationsImportRequestDTO.OperationRequestDTO(
            externalId = "ext-1",
            stockSymbol = "AAPL",
            type = OperationType.BUY,
            volume = BigDecimal(volume),
            pricePerVolume = BigDecimal(price),
            totalPrice = BigDecimal(total),
            openDate = Instant.now(),
        )

        describe("TotalPriceValidator") {

            it("returns true when totalPrice matches volume * pricePerVolume perfectly") {
                val dto = createDTO("10.00", "150.00", "1500.00")
                validator.isValid(dto, context).shouldBeTrue()
            }

            it("returns true for fractional volume with broker rounding (case from logs: IB01)") {
                val dto = createDTO("0.3207", "118.52", "38.01")
                validator.isValid(dto, context).shouldBeTrue()
            }

            it("returns true for fractional volume with broker rounding down (case from logs: O)") {
                val dto = createDTO("0.8905", "57.44", "51.15")
                validator.isValid(dto, context).shouldBeTrue()
            }

            it("returns false when totalPrice is clearly wrong (even after rounding)") {
                val dto = createDTO("10.00", "150.00", "1500.10")
                validator.isValid(dto, context).shouldBeFalse()
            }

            it("returns false when totalPrice difference is exactly 1 cent/grosz too much") {
                val dto = createDTO("0.3207", "118.52", "38.03")
                validator.isValid(dto, context).shouldBeFalse()
            }
        }
    }
}
