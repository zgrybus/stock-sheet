package com.example.stocksheet.operations.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
import com.example.stocksheet.operations.dto.OperationsImportResponseDTO
import com.example.stocksheet.operations.dto.PortfolioHoldingsResponseDTO
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.exceptions.OperationsErrorType
import com.example.stocksheet.portfolio.exceptions.PortfolioErrorType
import com.example.stocksheet.scenarios.StandardMarketScenario
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

@Transactional
class OperationControllerTest : BaseIntegrationTest() {
    @Autowired lateinit var standardMarketScenario: StandardMarketScenario

    init {
        describe("GET /api/operations/{portfolioId}/holdings") {
            it("gets holdings for provided portfolio id") {
                val data = standardMarketScenario.setup()
                val portfolioUSD = data.portfolios[0]
                val response = mockMvc.get("/api/operations/${portfolioUSD.id}/holdings").andReturn().response

                val returnedHoldings = objectMapper.readValue(response.contentAsString, PortfolioHoldingsResponseDTO::class.java)

                returnedHoldings.portfolioId.shouldBe(portfolioUSD.id)
                returnedHoldings.positions.shouldContainExactly(
                    listOf(
                        PortfolioHoldingsResponseDTO.PositionDTO(
                            stockSymbol = "AAPL.US",
                            totalVolume = BigDecimal("15.0000"),
                            totalCost = BigDecimal("1900.00"),
                            stockName = "Apple",
                        ),
                        PortfolioHoldingsResponseDTO.PositionDTO(
                            stockSymbol = "GOOGL.US",
                            totalVolume = BigDecimal("50.0000"),
                            totalCost = BigDecimal("500.00"),
                            stockName = "Alphabet",
                        ),
                    ),
                )
            }

            it("gets empty holdings, when operations does not exist for provided portfolio id") {
                val data = standardMarketScenario.setup()
                val portfolioPLN = data.portfolios[1]
                val response = mockMvc.get("/api/operations/${portfolioPLN.id}/holdings").andReturn().response

                val returnedHoldings = objectMapper.readValue(response.contentAsString, PortfolioHoldingsResponseDTO::class.java)

                returnedHoldings.portfolioId.shouldBe(portfolioPLN.id)
                returnedHoldings.positions.shouldBeEmpty()
            }

            it("gets an error, when provided portfolio id does not exist") {
                val response = mockMvc.get("/api/operations/0/holdings").andReturn().response

                val returnedErrorResponse = objectMapper.readValue(response.contentAsString, ErrorResponse::class.java)

                returnedErrorResponse.shouldBe(
                    ErrorResponse(
                        path = "uri=/api/operations/0/holdings",
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

        describe("POST /api/operations/:portfolioId/operations/import") {
            fun getOperationRequestDTO(): OperationsImportRequestDTO.OperationRequestDTO =
                OperationsImportRequestDTO.OperationRequestDTO(
                    externalId = "external-id-1",
                    stockSymbol = "AAPL",
                    stockExchange = "US",
                    type = OperationType.BUY,
                    volume = BigDecimal("10.00"),
                    openDate = LocalDateTime.of(2019, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
                    pricePerVolume = BigDecimal("100.00"),
                    totalPrice = BigDecimal("1000.00"),
                )

            fun getOperationsImportRequestDTO(): OperationsImportRequestDTO =
                OperationsImportRequestDTO(
                    operations =
                        listOf(
                            getOperationRequestDTO(),
                            getOperationRequestDTO().apply {
                                externalId = "external-id-101"
                                stockSymbol = "NVDA"
                                stockExchange = "L"
                            },
                        ),
                )

            it("saves new operation and do not save duplicated one") {
                val body = getOperationsImportRequestDTO()
                val data = standardMarketScenario.setup()
                val portfolioUSD = data.portfolios[0]

                val response =
                    mockMvc
                        .post("/api/operations/${portfolioUSD.id}/operations/import") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedResponse = objectMapper.readValue(response.contentAsString, OperationsImportResponseDTO::class.java)

                returnedResponse.shouldBe(
                    OperationsImportResponseDTO(
                        added = listOf(OperationsImportResponseDTO.OperationSummaryDTO(returnedResponse.added[0].id, "external-id-101")),
                        duplicated =
                            listOf(
                                OperationsImportResponseDTO.OperationSummaryDTO(returnedResponse.duplicated[0].id, "external-id-1"),
                            ),
                    ),
                )
            }

            it("imports all operations when no duplicates are present") {
                val batch = getOperationsImportRequestDTO()
                val data = standardMarketScenario.setup()
                val portfolioUSD = data.portfolios[0]

                val body =
                    batch.copy(
                        operations =
                            batch.operations.toMutableList().apply {
                                this[0] = this[0].copy(externalId = "external-id-100")
                            },
                    )

                val response =
                    mockMvc
                        .post("/api/operations/${portfolioUSD.id}/operations/import") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedResponse = objectMapper.readValue(response.contentAsString, OperationsImportResponseDTO::class.java)

                returnedResponse.shouldBe(
                    OperationsImportResponseDTO(
                        added =
                            listOf(
                                OperationsImportResponseDTO.OperationSummaryDTO(returnedResponse.added[0].id, "external-id-100"),
                                OperationsImportResponseDTO.OperationSummaryDTO(returnedResponse.added[1].id, "external-id-101"),
                            ),
                        duplicated =
                            listOf(),
                    ),
                )
            }

            it("successfully imports operations using fallback stock data when Finnhub API fails") {
                every { finnhubService.getSymbolLookup("NVDA", any<String>()) } throws RuntimeException("Error")

                val batch = getOperationsImportRequestDTO()
                val data = standardMarketScenario.setup()
                val portfolioUSD = data.portfolios[0]

                val body =
                    batch.copy(
                        operations =
                            batch.operations.toMutableList().apply {
                                this[0] = this[0].copy(externalId = "external-id-100")
                            },
                    )

                val response =
                    mockMvc
                        .post("/api/operations/${portfolioUSD.id}/operations/import") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedResponse = objectMapper.readValue(response.contentAsString, OperationsImportResponseDTO::class.java)

                returnedResponse.shouldBe(
                    OperationsImportResponseDTO(
                        added =
                            listOf(
                                OperationsImportResponseDTO.OperationSummaryDTO(returnedResponse.added[0].id, "external-id-100"),
                                OperationsImportResponseDTO.OperationSummaryDTO(returnedResponse.added[1].id, "external-id-101"),
                            ),
                        duplicated =
                            listOf(),
                    ),
                )
            }

            it("gets an error, when all of the operations are duplicates") {
                val batch = getOperationsImportRequestDTO()
                val data = standardMarketScenario.setup()
                val portfolioUSD = data.portfolios[0]
                val body =
                    batch.copy(
                        operations =
                            batch.operations.toMutableList().apply {
                                this[1] = this[1].copy(externalId = "external-id-2")
                            },
                    )

                val response =
                    mockMvc
                        .post("/api/operations/${portfolioUSD.id}/operations/import") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedErrorResponse = objectMapper.readValue(response.contentAsString, ErrorResponse::class.java)

                returnedErrorResponse.shouldBe(
                    ErrorResponse(
                        path = "uri=/api/operations/${portfolioUSD.id}/operations/import",
                        status = HttpStatus.BAD_REQUEST.value(),
                        errors =
                            listOf(
                                ErrorDTO(message = "Could not find new operations", type = OperationsErrorType.BATCH_EMPTY_OPERATIONS.name),
                            ),
                    ),
                )
            }

            it("gets an error, when provided portfolio id does not exist") {
                val body = getOperationsImportRequestDTO()

                val response =
                    mockMvc
                        .post("/api/operations/0/operations/import") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedErrorResponse = objectMapper.readValue(response.contentAsString, ErrorResponse::class.java)

                returnedErrorResponse.shouldBe(
                    ErrorResponse(
                        path = "uri=/api/operations/0/operations/import",
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
