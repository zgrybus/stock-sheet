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
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@Transactional
class PortfolioControllerTest : BaseIntegrationTest() {
    @Autowired lateinit var portfolioRepository: PortfolioRepository

    init {
        fun setup(): Pair<PortfolioEntity, PortfolioEntity> {
            val portfolio1 =
                testDb.createPortfolioEntity {
                    name = "portfolio_name_1"
                    currency = "USD"
                }
            val portfolio2 =
                testDb.createPortfolioEntity {
                    name = "portfolio_name_2"
                    currency = "PLN"
                }

            return Pair(portfolio1, portfolio2)
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

                portfolioRepository.existsById(returnedPortfolio.id).shouldBeTrue()

                returnedPortfolio.shouldBe(PortfolioResponseDTO(id = returnedPortfolio.id, name = body.name, currency = body.currency))
            }

            it("returns error, when portfolio name already exists") {
                val (portfolio1) = setup()
                val body = PortfolioEntity(name = portfolio1.name, currency = "EUR")

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
                val (portfolio1, portfolio2) = setup()

                val response = mockMvc.get("/api/portfolio").andReturn().response

                val returnedPortfolios = objectMapper.readValue(response.contentAsString, Array<PortfolioResponseDTO>::class.java)

                returnedPortfolios.shouldHaveSize(2)
                returnedPortfolios.shouldContainExactly(
                    PortfolioResponseDTO(id = portfolio1.id!!, name = portfolio1.name, currency = portfolio1.currency),
                    PortfolioResponseDTO(id = portfolio2.id!!, name = portfolio2.name, currency = portfolio2.currency),
                )
            }

            it("returns empty list, when there are no portfolios") {
                val response = mockMvc.get("/api/portfolio").andReturn().response

                val returnedPortfolios = objectMapper.readValue(response.contentAsString, Array<PortfolioResponseDTO>::class.java)

                returnedPortfolios.shouldHaveSize(0)
            }
        }

        describe("GET /api/portfolio/{id}") {
            it("returns portfolio by id") {
                val (portfolio1) = setup()

                val response = mockMvc.get("/api/portfolio/${portfolio1.id}").andReturn().response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, PortfolioResponseDTO::class.java)

                returnedPortfolio.shouldBeEqual(
                    PortfolioResponseDTO(id = portfolio1.id!!, name = portfolio1.name, currency = portfolio1.currency),
                )
            }

            it("returns error, when id does not exist") {
                setup()

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
                val (portfolio1) = setup()

                portfolioRepository.existsById(portfolio1.id!!).shouldBeTrue()

                mockMvc.delete("/api/portfolio/${portfolio1.id}").andReturn().response

                portfolioRepository.existsById(portfolio1.id!!).shouldBeFalse()
            }

            it("returns error, when id does not exist") {
                setup()

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
