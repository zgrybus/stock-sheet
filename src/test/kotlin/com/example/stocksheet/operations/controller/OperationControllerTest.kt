package com.example.stocksheet.operations.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.operations.dto.PortfolioSummaryDTO
import com.example.stocksheet.operations.dto.StockPositionDTO
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.repository.OperationRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get
import java.time.Instant

class OperationControllerTest : BaseIntegrationTest() {
    @Autowired lateinit var operationRepository: OperationRepository

    init {
        fun getOperationEntity(): OperationEntity =
            OperationEntity(
                externalId = "external-id-1",
                stockSymbol = "GOOG.US",
                type = OperationType.BUY,
                volume = 10.toBigDecimal(),
                openDate = Instant.now(),
                pricePerVolume = 150.toBigDecimal(),
                totalPrice = 1500.toBigDecimal(),
                currency = "USD",
            )

        beforeEach {
            operationRepository.saveAll(
                listOf(
                    getOperationEntity(),
                    getOperationEntity().apply {
                        externalId = "external-id-2"
                        volume = 15.toBigDecimal()
                        pricePerVolume = 95.toBigDecimal()
                        totalPrice = 1425.toBigDecimal()
                        currency = "EUR"
                    },
                    getOperationEntity().apply {
                        externalId = "external-id-3"
                        volume = 2.toBigDecimal()
                        pricePerVolume = 500.toBigDecimal()
                        totalPrice = 1000.toBigDecimal()
                        stockSymbol = "TSLA.US"
                    },
                ),
            )
        }

        afterEach {
            operationRepository.deleteAll()
        }

        describe("GET /api/operations/portfolio/{currency}") {
            it("gets portfolio for provided currency") {
                val response = mockMvc.get("/api/operations/portfolio/USD").andReturn().response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, PortfolioSummaryDTO::class.java)

                returnedPortfolio.currency.shouldBe("USD")
                returnedPortfolio.positions.shouldContainExactly(
                    listOf(
                        StockPositionDTO(stockSymbol = "GOOG.US", totalVolume = 10.toBigDecimal(), totalCost = 1500.toBigDecimal()),
                        StockPositionDTO(stockSymbol = "TSLA.US", totalVolume = 2.toBigDecimal(), totalCost = 1000.toBigDecimal()),
                    ),
                )
            }

            it("gets empty positions, when operations does not exist for provided currency") {
                val response = mockMvc.get("/api/operations/portfolio/PLN").andReturn().response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, PortfolioSummaryDTO::class.java)

                returnedPortfolio.currency.shouldBe("PLN")
                returnedPortfolio.positions.shouldBeEmpty()
            }
        }
    }
}
