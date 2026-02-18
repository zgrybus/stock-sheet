package com.example.stocksheet.portfolio.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.portfolio.dto.PortfolioListResponseDTO
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class PortfolioControllerTest : BaseIntegrationTest() {
    @Autowired lateinit var portfolioRepository: PortfolioRepository

    private lateinit var portfolioUSD: PortfolioEntity
    private lateinit var portfolioPLN: PortfolioEntity

    init {
        beforeEach {
            portfolioUSD = PortfolioEntity(name = "portfolio_name_1", currency = "USD")
            portfolioPLN = PortfolioEntity(name = "portfolio_name_2", currency = "PLN")

            portfolioRepository.saveAll(
                listOf(
                    portfolioUSD,
                    portfolioPLN,
                ),
            )
        }

        afterEach {
            portfolioRepository.deleteAllInBatch()
        }

        describe("POST /api/portfolio") {
            it("adds new portfolio") {
                val body = PortfolioEntity(name = "portfolio_name_3", currency = "EUR")

                val response =
                    mockMvc
                        .post("/api/portfolio") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, PortfolioListResponseDTO::class.java)

                returnedPortfolio.shouldBe(PortfolioListResponseDTO(id = returnedPortfolio.id, name = body.name, currency = body.currency))
            }
        }

        describe("GET /api/portfolio/list") {
            it("returns list of the portfolio") {
                val response = mockMvc.get("/api/portfolio/list").andReturn().response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, Array<PortfolioListResponseDTO>::class.java)

                returnedPortfolio.shouldHaveSize(2)
                returnedPortfolio.shouldContainExactly(
                    PortfolioListResponseDTO(id = portfolioUSD.id!!, name = portfolioUSD.name, currency = portfolioUSD.currency),
                    PortfolioListResponseDTO(id = portfolioPLN.id!!, name = portfolioPLN.name, currency = portfolioPLN.currency),
                )
            }

            it("returns empty list, when there are no portfolios") {
                portfolioRepository.deleteAllInBatch()

                val response = mockMvc.get("/api/portfolio/list").andReturn().response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, Array<PortfolioListResponseDTO>::class.java)

                returnedPortfolio.shouldHaveSize(0)
            }
        }
    }
}
