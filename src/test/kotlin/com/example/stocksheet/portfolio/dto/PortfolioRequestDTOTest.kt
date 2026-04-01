package com.example.stocksheet.portfolio.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import jakarta.validation.Validation
import jakarta.validation.Validator

class PortfolioRequestDTOTest : DescribeSpec() {
    init {
        val validator: Validator = Validation.buildDefaultValidatorFactory().validator

        fun getPortfolioRequestDTO(): PortfolioRequestDTO = PortfolioRequestDTO(name = "portfolio_name_1", currency = "USD")

        describe("PortfolioRequestDTO") {
            it("does not return any error") {
                val dto = getPortfolioRequestDTO()
                val violation = validator.validate(dto)

                violation.isEmpty().shouldBeTrue()
            }

            describe("name validation") {
                it("returns an error, when name is empty") {
                    val dto = getPortfolioRequestDTO().copy(name = "")
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("Name cannot be empty")
                        it.propertyPath.toString().shouldBe("name")
                    }
                }
            }

            describe("currency validation") {
                data class ExpectedError(
                    val propertyPath: String,
                    val message: String,
                )

                it("returns an error, when currency is empty") {
                    val dto = getPortfolioRequestDTO().copy(currency = "")
                    val violations = validator.validate(dto)

                    val errors =
                        violations.map {
                            ExpectedError(propertyPath = it.propertyPath.toString(), message = it.message)
                        }

                    errors.shouldHaveSize(2)
                    errors.shouldContainExactlyInAnyOrder(
                        ExpectedError(propertyPath = "currency", message = "Currency cannot be empty"),
                        ExpectedError(propertyPath = "currency", message = "Currency is not valid"),
                    )
                }

                it("returns an error, when currency is not valid") {
                    val dto = getPortfolioRequestDTO().copy(currency = "USDD")
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("Currency is not valid")
                        it.propertyPath.toString().shouldBe("currency")
                    }
                }
            }
        }
    }
}
