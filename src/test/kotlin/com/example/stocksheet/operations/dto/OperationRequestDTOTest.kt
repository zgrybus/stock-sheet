package com.example.stocksheet.operations.dto

import com.example.stocksheet.operations.entity.OperationType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import jakarta.validation.Validation
import jakarta.validation.Validator
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

class OperationRequestDTOTest : DescribeSpec() {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    private fun getOperationRequestDTO(
        externalId: String? = "external-id-1",
        stockSymbol: String? = "APL.US",
        type: OperationType? = OperationType.BUY,
        volume: BigDecimal? = 10.toBigDecimal(),
        openDate: Instant? = LocalDateTime.of(2019, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
        pricePerVolume: BigDecimal? = 100.toBigDecimal(),
        totalPrice: BigDecimal? = 1000.toBigDecimal(),
        currency: String? = "USD",
    ): OperationRequestDTO =
        OperationRequestDTO(
            externalId,
            stockSymbol,
            type,
            volume,
            openDate,
            pricePerVolume,
            totalPrice,
            currency,
        )

    init {
        describe("OperationRequestDTO") {
            it("does not return any error") {
                val dto = getOperationRequestDTO()
                val violation = validator.validate(dto)

                violation.isEmpty().shouldBeTrue()
            }

            describe("externalId validation") {
                it("returns an error, when external id is not set") {
                    val dto = getOperationRequestDTO(externalId = null)
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("External ID is required")
                        it.propertyPath.toString().shouldBe("externalId")
                    }
                }
            }

            describe("stockSymbol validation") {
                it("returns an error, when stock symbol is not set") {
                    val dto = getOperationRequestDTO(stockSymbol = null)
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("Stock Symbol is required")
                        it.propertyPath.toString().shouldBe("stockSymbol")
                    }
                }
            }

            describe("type validation") {
                it("returns an error, when type is not set") {
                    val dto = getOperationRequestDTO(type = null)
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("Type is required")
                        it.propertyPath.toString().shouldBe("type")
                    }
                }
            }

            describe("volume validation") {
                it("returns an error, when volume is not set") {
                    val dto = getOperationRequestDTO(volume = null)
                    val violations = validator.validate(dto)

                    violations.forOne {
                        it.message.shouldBe("Volume is required")
                        it.propertyPath.toString().shouldBe("volume")
                    }
                }

                it("returns an error, when volume is not positive") {
                    val dto = getOperationRequestDTO(volume = 0.toBigDecimal())
                    val violations = validator.validate(dto)

                    violations.forOne {
                        it.message.shouldBe("Volume needs to be positive")
                        it.propertyPath.toString().shouldBe("volume")
                    }
                }
            }

            describe("openDate validation") {
                it("returns an error, when open date is not set") {
                    val dto = getOperationRequestDTO(openDate = null)
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("Open Date is required")
                        it.propertyPath.toString().shouldBe("openDate")
                    }
                }

                it("returns an error, when open date is not set") {
                    val dto = getOperationRequestDTO(openDate = LocalDateTime.now().plusYears(1).toInstant(ZoneOffset.UTC))
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("Open Date needs to be date from the past")
                        it.propertyPath.toString().shouldBe("openDate")
                    }
                }
            }

            describe("pricePerVolume validation") {
                it("returns an error, when price per volume is not set") {
                    val dto = getOperationRequestDTO(pricePerVolume = null)
                    val violations = validator.validate(dto)

                    violations.forOne {
                        it.message.shouldBe("Price Per Volume is required")
                        it.propertyPath.toString().shouldBe("pricePerVolume")
                    }
                }

                it("returns an error, when price per volume is not positive") {
                    val dto = getOperationRequestDTO(pricePerVolume = 0.toBigDecimal())
                    val violations = validator.validate(dto)

                    violations.forOne {
                        it.message.shouldBe("Price Per Volume needs to be positive")
                        it.propertyPath.toString().shouldBe("pricePerVolume")
                    }
                }
            }

            describe("totalPrice validation") {
                it("returns an error, when total price is not set") {
                    val dto = getOperationRequestDTO(totalPrice = null)
                    val violations = validator.validate(dto)

                    violations.forOne {
                        it.message.shouldBe("Total Price is required")
                        it.propertyPath.toString().shouldBe("totalPrice")
                    }
                }

                it("returns an error, when total price is not positive") {
                    val dto = getOperationRequestDTO(totalPrice = 0.toBigDecimal())
                    val violations = validator.validate(dto)

                    violations.forOne {
                        it.message.shouldBe("Total Price needs to be positive")
                        it.propertyPath.toString().shouldBe("totalPrice")
                    }
                }

                it("returns an error, when total price is does not equal Volume * Price Per Volume") {
                    val dto =
                        getOperationRequestDTO(
                            pricePerVolume = 100.toBigDecimal(),
                            volume = 10.toBigDecimal(),
                            totalPrice = 900.toBigDecimal(),
                        )
                    val violations = validator.validate(dto)

                    violations.forOne {
                        it.message.shouldBe("Total Price must be equal to Volume * Price Per Volume")
                        it.propertyPath.toString().shouldBe("totalPrice")
                    }
                }
            }

            describe("currency validation") {
                it("returns an error, when currency is not set") {
                    val dto = getOperationRequestDTO(currency = null)
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("Currency is required")
                        it.propertyPath.toString().shouldBe("currency")
                    }
                }
            }
        }
    }
}
