package com.example.stocksheet.portfolio.validation

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import jakarta.validation.ConstraintValidatorContext

class CurrencyValidatorTest : DescribeSpec() {
    init {
        val validator = CurrencyValidator()
        val context = mockk<ConstraintValidatorContext>(relaxed = true)

        describe("CurrencyValidator") {
            it("returns true for valid ISO currency codes") {
                val validCodes = listOf("PLN", "USD", "EUR", "JPY", "GBP")

                validCodes.forEach { code ->
                    validator.isValid(code, context).shouldBeTrue()
                }
            }

            it("returns false for invalid currency codes") {
                val invalidCodes = listOf("XYZ", "PLNN", "123", "POLAND")

                invalidCodes.forEach { code ->
                    validator.isValid(code, context).shouldBeFalse()
                }
            }

            it("returns false for lowercase codes") {
                validator.isValid("pln", context).shouldBeFalse()
            }

            it("returns false for null values") {
                validator.isValid(null, context).shouldBeFalse()
            }

            it("returns false for empty string") {
                validator.isValid("", context).shouldBeFalse()
            }
        }
    }
}
