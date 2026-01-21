package com.example.stocksheet.operations.dto

import com.example.stocksheet.operations.entity.OperationType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
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
        externalId: String = "external-id-1",
        stockSymbol: String = "APL.US",
        type: OperationType = OperationType.BUY,
        volume: BigDecimal = 10.toBigDecimal(),
        openDate: Instant = LocalDateTime.of(2019, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
        pricePerVolume: BigDecimal = 100.toBigDecimal(),
        totalPrice: BigDecimal = 1000.toBigDecimal(),
        currency: String = "USD",
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
        }
    }
}
