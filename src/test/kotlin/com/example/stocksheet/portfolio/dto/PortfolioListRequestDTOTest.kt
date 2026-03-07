package com.example.stocksheet.portfolio.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import jakarta.validation.Validation
import jakarta.validation.Validator

class PortfolioListRequestDTOTest : DescribeSpec() {
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
                it("returns an error, when currency is empty") {
                    val dto = getPortfolioRequestDTO().copy(currency = null)
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("Currency cannot be empty")
                        it.propertyPath.toString().shouldBe("currency")
                    }
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

            describe("toEntity") {
                it("should correctly map all fields from DTO to Entity") {
                    val dto = getPortfolioRequestDTO()

                    dto.toEntity().should {
                        it.name.shouldBe(dto.name)
                        it.currency.shouldBe(dto.currency)
                    }
                }
            }
        }
    }
}
