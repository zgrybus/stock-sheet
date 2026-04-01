package com.example.stocksheet.portfolio.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.portfolio.dto.PortfolioRequestDTO
import com.example.stocksheet.portfolio.dto.PortfolioResponseDTO
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.portfolio.exceptions.PortfolioErrorType
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Transactional
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

        describe("POST /api/portfolio") {
            it("adds new portfolio") {
                val body = PortfolioRequestDTO(name = "portfolio_name_3", currency = "EUR")

                val response =
                    mockMvc
                        .post("/api/portfolio") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, PortfolioResponseDTO::class.java)

                returnedPortfolio.shouldBe(PortfolioResponseDTO(id = returnedPortfolio.id, name = body.name!!, currency = body.currency!!))
            }

            it("returns error, when portfolio name already exists") {
                val body = PortfolioEntity(name = portfolioUSD.name, currency = "EUR")

                val response =
                    mockMvc
                        .post("/api/portfolio") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedError = objectMapper.readValue(response.contentAsString, ErrorResponse::class.java)

                returnedError.shouldBe(
                    ErrorResponse(
                        path = "uri=/api/portfolio",
                        status = HttpStatus.BAD_REQUEST.value(),
                        errors =
                            listOf(
                                ErrorDTO(
                                    type = PortfolioErrorType.PORTFOLIO_NAME_DUPLICATED.name,
                                    message = "Portfolio with ${body.name} already exists",
                                ),
                            ),
                    ),
                )
            }
        }

        describe("GET /api/portfolio") {
            it("returns list of the portfolio") {
                val response = mockMvc.get("/api/portfolio").andReturn().response

                val returnedPortfolios = objectMapper.readValue(response.contentAsString, Array<PortfolioResponseDTO>::class.java)

                returnedPortfolios.shouldHaveSize(2)
                returnedPortfolios.shouldContainExactly(
                    PortfolioResponseDTO(id = portfolioUSD.id!!, name = portfolioUSD.name, currency = portfolioUSD.currency),
                    PortfolioResponseDTO(id = portfolioPLN.id!!, name = portfolioPLN.name, currency = portfolioPLN.currency),
                )
            }

            it("returns empty list, when there are no portfolios") {
                portfolioRepository.deleteAllInBatch()

                val response = mockMvc.get("/api/portfolio").andReturn().response

                val returnedPortfolios = objectMapper.readValue(response.contentAsString, Array<PortfolioResponseDTO>::class.java)

                returnedPortfolios.shouldHaveSize(0)
            }
        }

        describe("GET /api/portfolio/{id}") {
            it("returns portfolio by id") {
                val response = mockMvc.get("/api/portfolio/${portfolioUSD.id}").andReturn().response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, PortfolioResponseDTO::class.java)

                returnedPortfolio.shouldBeEqual(
                    PortfolioResponseDTO(id = portfolioUSD.id!!, name = portfolioUSD.name, currency = portfolioUSD.currency),
                )
            }

            it("returns error, when id does not exist") {
                val response = mockMvc.get("/api/portfolio/0").andReturn().response

                val returnedErrorDTO = objectMapper.readValue(response.contentAsString, ErrorResponse::class.java)

                returnedErrorDTO.shouldBeEqual(
                    ErrorResponse(
                        path = "uri=/api/portfolio/0",
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

        describe("DELETE /api/portfolio/{id}") {
            it("removes portfolio from the database") {
                portfolioRepository.existsById(portfolioUSD.id!!).shouldBeTrue()

                mockMvc.delete("/api/portfolio/${portfolioUSD.id}").andReturn().response

                portfolioRepository.existsById(portfolioUSD.id!!).shouldBeFalse()
            }

            it("returns error, when id does not exist") {
                val response = mockMvc.delete("/api/portfolio/0").andReturn().response

                val returnedErrorDTO = objectMapper.readValue(response.contentAsString, ErrorResponse::class.java)

                returnedErrorDTO.shouldBeEqual(
                    ErrorResponse(
                        path = "uri=/api/portfolio/0",
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
