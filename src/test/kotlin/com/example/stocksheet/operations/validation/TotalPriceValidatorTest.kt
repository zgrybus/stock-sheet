package com.example.stocksheet.operations.validation

import com.example.stocksheet.operations.dto.OperationRequestDTO
import com.example.stocksheet.operations.entity.OperationType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import jakarta.validation.ConstraintValidatorContext
import java.time.Instant

class TotalPriceValidatorTest : DescribeSpec() {
    init {
        val validator = TotalPriceValidator()

        val context = mockk<ConstraintValidatorContext>(relaxed = true)

        fun getValidDTO() =
            OperationRequestDTO(
                externalId = "ext-1",
                stockSymbol = "AAPL",
                type = OperationType.BUY,
                volume = 10.00.toBigDecimal(),
                pricePerVolume = 150.00.toBigDecimal(),
                totalPrice = 1500.00.toBigDecimal(),
                openDate = Instant.now(),
            )

        describe("TotalPriceValidator") {

            it("returns true when totalPrice matches volume * pricePerVolume") {
                val dto = getValidDTO()

                validator.isValid(dto, context).shouldBeTrue()
            }

            it("returns false when totalPrice is higher than volume * pricePerVolume") {
                val dto = getValidDTO().apply { totalPrice = 2000.00.toBigDecimal() }

                validator.isValid(dto, context).shouldBeFalse()
            }

            it("returns false if totalPrice is null") {
                val dtoWithNull = getValidDTO().apply { totalPrice = null }

                validator.isValid(dtoWithNull, context).shouldBeFalse()
            }

            it("returns false if pricePerVolume is null") {
                val dtoWithNull = getValidDTO().apply { pricePerVolume = null }

                validator.isValid(dtoWithNull, context).shouldBeFalse()
            }

            it("returns false if volume is null") {
                val dtoWithNull = getValidDTO().apply { volume = null }

                validator.isValid(dtoWithNull, context).shouldBeFalse()
            }
        }
    }
}
