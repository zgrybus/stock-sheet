package com.example.stocksheet.analytics.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.analytics.dto.PortfolioSummaryResponseDTO
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.portfolio.exceptions.PortfolioErrorType
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Transactional
class AnalyticsControllerTest : BaseIntegrationTest() {
    init {
        fun setup(): Pair<PortfolioEntity, PortfolioEntity> {
            val portfolio1 = testDb.createPortfolioEntity()
            val portfolio2 = testDb.createPortfolioEntity()

            val stock1 =
                testDb.createStockEntity {
                    symbol = "GOOG"
                    price = BigDecimal("332.62")
                }
            val stock2 =
                testDb.createStockEntity {
                    symbol = "MSFT"
                    price = BigDecimal("418.62")
                }

            val referenceDate = LocalDate.now().minusDays(1)
            testDb.createStockQuoteEntity {
                date = referenceDate
                stock = stock1
                closedPrice = BigDecimal("330.25")
            }
            testDb.createStockQuoteEntity {
                date = referenceDate
                stock = stock2
                closedPrice = BigDecimal("400.12")
            }

            testDb.createOperationEntity {
                portfolio = portfolio2
                stock = stock1
                volume = BigDecimal("5.32")
                pricePerVolume = BigDecimal("100.00")
            }
            testDb.createOperationEntity {
                portfolio = portfolio2
                stock = stock1
                volume = BigDecimal("2")
                pricePerVolume = BigDecimal("54.32")
            }
            testDb.createOperationEntity {
                portfolio = portfolio2
                stock = stock2
                volume = BigDecimal("10.5423")
                pricePerVolume = BigDecimal("5.23")
            }

            return Pair(portfolio1, portfolio2)
        }

        describe("GET /api/analytics/{portfolioId}/summary") {
            it("gets summary with calculated values for existing portfolio") {
                val (_, portfolio2) = setup()

                val response = mockMvc.get("/api/analytics/${portfolio2.id}/summary").andReturn().response

                val returnedResponse = objectMapper.readValue(response.contentAsString, PortfolioSummaryResponseDTO::class.java)

                returnedResponse.shouldBe(
                    PortfolioSummaryResponseDTO(
                        BigDecimal("6848.00"),
                        BigDecimal("6152.22"),
                        BigDecimal("695.78"),
                        BigDecimal("212.38"),
                    ),
                )
            }

            it("gets zeroed summary when portfolio has no operations") {
                val (portfolio1) = setup()

                val response = mockMvc.get("/api/analytics/${portfolio1.id}/summary").andReturn().response

                val returnedResponse = objectMapper.readValue(response.contentAsString, PortfolioSummaryResponseDTO::class.java)

                returnedResponse.shouldBe(
                    PortfolioSummaryResponseDTO(
                        BigDecimal("0.00"),
                        BigDecimal("0.00"),
                        BigDecimal("0.00"),
                        BigDecimal("0.00"),
                    ),
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
