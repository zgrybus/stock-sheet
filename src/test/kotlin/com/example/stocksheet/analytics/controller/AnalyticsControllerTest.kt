package com.example.stocksheet.analytics.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.analytics.dto.PortfolioSummaryResponseDTO
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.portfolio.exceptions.PortfolioErrorType
import com.example.stocksheet.scenarios.StandardMarketScenario
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Transactional
class AnalyticsControllerTest : BaseIntegrationTest() {
    @Autowired lateinit var standardMarketScenario: StandardMarketScenario

    init {
        describe("GET /api/analytics/{portfolioId}/summary") {
            it("gets summary with calculated values for existing portfolio") {
                val data = standardMarketScenario.setup()
                val portfolioUSD = data.portfolios[0]
                val response = mockMvc.get("/api/analytics/${portfolioUSD.id}/summary").andReturn().response

                val returnedResponse = objectMapper.readValue(response.contentAsString, PortfolioSummaryResponseDTO::class.java)

                returnedResponse.shouldBe(
                    PortfolioSummaryResponseDTO(BigDecimal("6128.75"), BigDecimal("3728.75"), BigDecimal("2400.00"), BigDecimal.ZERO),
                )
            }

            it("gets zeroed summary when portfolio has no operations") {
                val data = standardMarketScenario.setup()
                val portfolioPLN = data.portfolios[1]
                val response = mockMvc.get("/api/analytics/${portfolioPLN.id}/summary").andReturn().response

                val returnedResponse = objectMapper.readValue(response.contentAsString, PortfolioSummaryResponseDTO::class.java)

                returnedResponse.shouldBe(
                    PortfolioSummaryResponseDTO(BigDecimal("0.00"), BigDecimal("0.00"), BigDecimal("0.00"), BigDecimal.ZERO),
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
