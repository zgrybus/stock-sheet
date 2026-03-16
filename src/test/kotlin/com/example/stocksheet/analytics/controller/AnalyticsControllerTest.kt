package com.example.stocksheet.analytics.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.analytics.dto.PortfolioSummaryDTO
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.portfolio.exceptions.PortfolioErrorType
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.get
import java.time.Instant

class AnalyticsControllerTest : BaseIntegrationTest() {
    @Autowired lateinit var portfolioRepository: PortfolioRepository

    @Autowired lateinit var operationRepository: OperationRepository

    private lateinit var portfolioUSD: PortfolioEntity
    private lateinit var portfolioEURO: PortfolioEntity
    private lateinit var portfolioPLN: PortfolioEntity

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
                portfolio = portfolioUSD,
            )

        beforeEach {
            portfolioUSD = PortfolioEntity(name = "portfolio_name_1", currency = "USD")
            portfolioEURO = PortfolioEntity(name = "portfolio_name_2", currency = "EUR")
            portfolioPLN = PortfolioEntity(name = "portfolio_name_3", currency = "PLN")

            portfolioRepository.saveAll(
                listOf(
                    portfolioUSD,
                    portfolioEURO,
                    portfolioPLN,
                ),
            )

            operationRepository.saveAll(
                listOf(
                    getOperationEntity(),
                    getOperationEntity().apply {
                        externalId = "external-id-2"
                        volume = 15.toBigDecimal()
                        pricePerVolume = 95.toBigDecimal()
                        totalPrice = 1425.toBigDecimal()
                    },
                    getOperationEntity().apply {
                        externalId = "external-id-3"
                        volume = 2.toBigDecimal()
                        pricePerVolume = 500.toBigDecimal()
                        totalPrice = 1000.toBigDecimal()
                        stockSymbol = "TSLA.US"
                    },
                    getOperationEntity().apply {
                        externalId = "external-id-4"
                        volume = 1.toBigDecimal()
                        pricePerVolume = 520.toBigDecimal()
                        totalPrice = 520.toBigDecimal()
                        stockSymbol = "O.US"
                        portfolio = portfolioEURO
                    },
                ),
            )
        }

        afterEach {
            operationRepository.deleteAllInBatch()
            portfolioRepository.deleteAllInBatch()
        }

        describe("GET /api/analytics/{portfolioId}/summary") {
            it("gets summary with calculated values for existing portfolio") {
                val response = mockMvc.get("/api/analytics/${portfolioUSD.id}/summary").andReturn().response

                val returnedResponse = objectMapper.readValue(response.contentAsString, PortfolioSummaryDTO::class.java)

                returnedResponse.shouldBe(
                    PortfolioSummaryDTO(3925.toBigDecimal(), 0.toBigDecimal(), 3925.toBigDecimal(), 0),
                )
            }

            it("gets zeroed summary when portfolio has no operations") {
                val response = mockMvc.get("/api/analytics/${portfolioPLN.id}/summary").andReturn().response

                val returnedResponse = objectMapper.readValue(response.contentAsString, PortfolioSummaryDTO::class.java)

                returnedResponse.shouldBe(
                    PortfolioSummaryDTO(0.toBigDecimal(), 0.toBigDecimal(), 0.toBigDecimal(), 0),
                )
            }

            it("gets an error, when provided portfolio id does not exist") {
                val response = mockMvc.get("/api/analytics/0/summary").andReturn().response

                val returnedErrorResponse = objectMapper.readValue(response.contentAsString, ErrorResponse::class.java)

                returnedErrorResponse.shouldBe(
                    ErrorResponse(
                        path = "uri=/api/analytics/0/summary",
                        status = HttpStatus.NOT_FOUND.value(),
                        errors =
                            listOf(
                                ErrorDTO(
                                    type = PortfolioErrorType.PORTFOLIO_NOT_FOUND.name,
                                    message = "Could not find portfolio with id 0",
                                ),
                            ),
                    ),
                )
            }
        }
    }
}
