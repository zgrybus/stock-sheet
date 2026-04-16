package com.example.stocksheet.analytics.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.analytics.dto.PortfolioSummaryResponseDTO
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.mocks.createMockStockQuoteEntityMock
import com.example.stocksheet.portfolio.exceptions.PortfolioErrorType
import com.example.stocksheet.scenarios.StandardMarketScenario
import com.example.stocksheet.stocks.quotes.repository.StockQuoteRepository
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Transactional
class AnalyticsControllerTest : BaseIntegrationTest() {
    @Autowired lateinit var standardMarketScenario: StandardMarketScenario

    @Autowired lateinit var stockQuoteRepository: StockQuoteRepository

    init {
        describe("GET /api/analytics/{portfolioId}/summary") {
            it("gets summary with calculated values for existing portfolio") {
                val data = standardMarketScenario.setup()

                val referenceDate = LocalDate.now().minusDays(1)
                val stock1Quote =
                    createMockStockQuoteEntityMock(
                        stock = data.stocks[0],
                        closedPrice = BigDecimal("70.5"),
                        date = referenceDate,
                    )
                val stock2Quote =
                    createMockStockQuoteEntityMock(
                        stock = data.stocks[1],
                        closedPrice = BigDecimal("60.45"),
                        date = referenceDate,
                    )
                stockQuoteRepository.saveAll(listOf(stock1Quote, stock2Quote))

                val portfolioUSD = data.portfolios[0]

                val response = mockMvc.get("/api/analytics/${portfolioUSD.id}/summary").andReturn().response

                val returnedResponse = objectMapper.readValue(response.contentAsString, PortfolioSummaryResponseDTO::class.java)

                returnedResponse.shouldBe(
                    PortfolioSummaryResponseDTO(BigDecimal("6128.75"), BigDecimal("3728.75"), BigDecimal("2400.00"), BigDecimal("1697.00")),
                )
            }

            it("gets zeroed summary when portfolio has no operations") {
                val data = standardMarketScenario.setup()
                val portfolioPLN = data.portfolios[1]
                val response = mockMvc.get("/api/analytics/${portfolioPLN.id}/summary").andReturn().response

                val returnedResponse = objectMapper.readValue(response.contentAsString, PortfolioSummaryResponseDTO::class.java)

                returnedResponse.shouldBe(
                    PortfolioSummaryResponseDTO(BigDecimal("0.00"), BigDecimal("0.00"), BigDecimal("0.00"), BigDecimal("0.00")),
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
