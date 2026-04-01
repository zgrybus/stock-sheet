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
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

class OperationsImportRequestDTOTest : DescribeSpec() {
    init {
        val validator: Validator = Validation.buildDefaultValidatorFactory().validator

        fun getOperationRequestDTO(): OperationsImportRequestDTO.OperationRequestDTO =
            OperationsImportRequestDTO.OperationRequestDTO(
                externalId = "external-id-1",
                stockSymbol = "APL.US",
                type = OperationType.BUY,
                volume = 10.toBigDecimal(),
                openDate = LocalDateTime.of(2019, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
                pricePerVolume = 100.toBigDecimal(),
                totalPrice = 1000.toBigDecimal(),
            )

        fun getOperationsImportRequestDTO(): OperationsImportRequestDTO =
            OperationsImportRequestDTO(
                operations =
                    listOf(
                        getOperationRequestDTO(),
                    ),
            )

        describe("OperationsImportRequestDTO") {
            it("does not return any error") {
                val dto = getOperationsImportRequestDTO()
                val violation = validator.validate(dto)

                violation.isEmpty().shouldBeTrue()
            }

            describe("operations validation") {
                it("returns an error, when operations are empty") {
                    val dto = getOperationsImportRequestDTO().apply { operations = listOf() }
                    val violations = validator.validate(dto)

                    violations.shouldHaveSize(1)
                    violations.first().should {
                        it.message.shouldBe("Operations list cannot be empty")
                        it.propertyPath.toString().shouldBe("operations")
                    }
                }
            }

            describe("OperationRequestDTO") {
                it("does not return any error") {
                    val dto = getOperationRequestDTO()
                    val violation = validator.validate(dto)

                    violation.isEmpty().shouldBeTrue()
                }

                describe("externalId validation") {
                    it("returns an error, when external id is not set") {
                        val dto = getOperationRequestDTO().apply { externalId = "" }
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
                        val dto = getOperationRequestDTO().apply { stockSymbol = "" }
                        val violations = validator.validate(dto)

                        violations.shouldHaveSize(1)
                        violations.first().should {
                            it.message.shouldBe("Stock Symbol is required")
                            it.propertyPath.toString().shouldBe("stockSymbol")
                        }
                    }
                }

                describe("type validation") {
//                    it("returns an error, when type is not set") {
//                        val dto = getOperationRequestDTO().apply { type = OperationType.UNKNOWN }
//                        val violations = validator.validate(dto)
//
//                        violations.shouldHaveSize(1)
//                        violations.first().should {
//                            it.message.shouldBe("Type is required")
//                            it.propertyPath.toString().shouldBe("type")
//                        }
//                    }
                }

                describe("volume validation") {
                    it("returns an error, when volume is not positive") {
                        val dto = getOperationRequestDTO().apply { volume = BigDecimal.ZERO }
                        val violations = validator.validate(dto)

                        violations.forOne {
                            it.message.shouldBe("Volume needs to be positive")
                            it.propertyPath.toString().shouldBe("volume")
                        }
                    }
                }

                describe("openDate validation") {
                    it("returns an error, when open date is from the future") {
                        val dto = getOperationRequestDTO().apply { openDate = LocalDateTime.now().plusYears(1).toInstant(ZoneOffset.UTC) }
                        val violations = validator.validate(dto)

                        violations.shouldHaveSize(1)
                        violations.first().should {
                            it.message.shouldBe("Open Date needs to be date from the past")
                            it.propertyPath.toString().shouldBe("openDate")
                        }
                    }
                }

                describe("pricePerVolume validation") {
                    it("returns an error, when price per volume is not positive") {
                        val dto = getOperationRequestDTO().apply { pricePerVolume = BigDecimal.ZERO }
                        val violations = validator.validate(dto)

                        violations.forOne {
                            it.message.shouldBe("Price Per Volume needs to be positive")
                            it.propertyPath.toString().shouldBe("pricePerVolume")
                        }
                    }
                }

                describe("totalPrice validation") {
                    it("returns an error, when total price is not positive") {
                        val dto = getOperationRequestDTO().apply { totalPrice = BigDecimal.ZERO }
                        val violations = validator.validate(dto)

                        violations.forOne {
                            it.message.shouldBe("Total Price needs to be positive")
                            it.propertyPath.toString().shouldBe("totalPrice")
                        }
                    }

                    it("returns an error, when total price is does not equal Volume * Price Per Volume") {
                        val dto =
                            getOperationRequestDTO().apply {
                                pricePerVolume = 100.toBigDecimal()
                                volume = 10.toBigDecimal()
                                totalPrice = 900.toBigDecimal()
                            }
                        val violations = validator.validate(dto)

                        violations.forOne {
                            it.message.shouldBe("Total Price must be equal to Volume * Price Per Volume")
                            it.propertyPath.toString().shouldBe("totalPrice")
                        }
                    }
                }
            }
        }
    }
}
