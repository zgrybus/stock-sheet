package com.example.stocksheet.operations.service

import com.example.stocksheet.operations.dto.StockPositionDTO
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.repository.OperationRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.Instant

class OperationServiceTest : DescribeSpec() {
    private var operationRepositoryMock: OperationRepository = mockk()
    private val operationService = OperationService(operationRepositoryMock)

    init {
        fun getOperationEntity(): OperationEntity =
            OperationEntity(
                id = 100,
                externalId = "external-id-1",
                stockSymbol = "GOOG.US",
                type = OperationType.BUY,
                volume = 10.toBigDecimal(),
                openDate = Instant.now(),
                pricePerVolume = 150.toBigDecimal(),
                totalPrice = 1500.toBigDecimal(),
                currency = "USD",
            )

        val operations =
            listOf(
                getOperationEntity(),
                getOperationEntity().apply {
                    id = 101
                    externalId = "external-id-2"
                    volume = 15.toBigDecimal()
                    pricePerVolume = 95.toBigDecimal()
                    totalPrice = 1425.toBigDecimal()
                },
                getOperationEntity().apply {
                    id = 102
                    externalId = "external-id-3"
                    volume = 2.toBigDecimal()
                    pricePerVolume = 500.toBigDecimal()
                    totalPrice = 1000.toBigDecimal()
                    stockSymbol = "TSLA.US"
                },
                getOperationEntity().apply {
                    id = 103
                    externalId = "external-id-4"
                    volume = 95.toBigDecimal()
                    pricePerVolume = 2.toBigDecimal()
                    totalPrice = 190.toBigDecimal()
                    stockSymbol = "ALPH.US"
                },
                getOperationEntity().apply {
                    id = 103
                    externalId = "external-id-4"
                    volume = 10.toBigDecimal()
                    pricePerVolume = 5.toBigDecimal()
                    totalPrice = 50.toBigDecimal()
                    stockSymbol = "TSLA.US"
                },
            )

        describe("OperationServiceTest") {
            beforeEach {
                every { operationRepositoryMock.findAllByCurrency("USD") } returns operations
            }

            it("groups and sum operations correctly") {
                val result = operationService.getPortfolioSummary("USD")

                result.currency.shouldBe("USD")
                result.positions.shouldContainExactly(
                    listOf(
                        StockPositionDTO(stockSymbol = "GOOG.US", totalCost = 2925.toBigDecimal(), totalVolume = 25.toBigDecimal()),
                        StockPositionDTO(stockSymbol = "TSLA.US", totalCost = 1050.toBigDecimal(), totalVolume = 12.toBigDecimal()),
                        StockPositionDTO(stockSymbol = "ALPH.US", totalCost = 190.toBigDecimal(), totalVolume = 95.toBigDecimal()),
                    ),
                )
            }
        }
    }
}
