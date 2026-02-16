package com.example.stocksheet.operations.mapper

import com.example.stocksheet.operations.dto.OperationRequestDTO
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

class OperationRequestDTOMappers : DescribeSpec() {
    init {
        fun getOperationRequestDTO(): OperationRequestDTO =
            OperationRequestDTO(
                externalId = "external-id-1",
                stockSymbol = "APL.US",
                type = OperationType.BUY,
                volume = 10.toBigDecimal(),
                openDate = LocalDateTime.of(2019, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
                pricePerVolume = 100.toBigDecimal(),
                totalPrice = 1000.toBigDecimal(),
            )

        describe("toEntity") {

            it("should correctly map all fields from DTO to Entity") {
                val dto = getOperationRequestDTO()
                val portfolio = PortfolioEntity(id = 10003, name = "portfolio_name_1", currency = "USD")

                dto.toEntity(portfolio).should {
                    it.externalId.shouldBe(dto.externalId)
                    it.stockSymbol.shouldBe(dto.stockSymbol)
                    it.type.shouldBe(dto.type)
                    it.volume.shouldBe(dto.volume)
                    it.openDate.shouldBe(dto.openDate)
                    it.pricePerVolume.shouldBe(dto.pricePerVolume)
                    it.totalPrice.shouldBe(dto.totalPrice)
                    it.portfolio.shouldBe(portfolio)
                }
            }
        }
    }
}
