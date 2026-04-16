package com.example.stocksheet.operations.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
import com.example.stocksheet.operations.dto.OperationsImportResponseDTO
import com.example.stocksheet.operations.dto.PortfolioHoldingsResponseDTO
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.exceptions.OperationsErrorType
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.portfolio.exceptions.PortfolioErrorType
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

@Transactional
class OperationControllerTest : BaseIntegrationTest() {
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
                externalId = "external-id-1"
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
                externalId = "external-id-2"
            }

            return Pair(portfolio1, portfolio2)
        }

        describe("GET /api/operations/{portfolioId}/holdings") {
            it("gets holdings for provided portfolio id") {
                val (_, portfolio2) = setup()

                val response = mockMvc.get("/api/operations/${portfolio2.id}/holdings").andReturn().response

                val returnedHoldings = objectMapper.readValue(response.contentAsString, PortfolioHoldingsResponseDTO::class.java)

                returnedHoldings.portfolioId.shouldBe(portfolio2.id)
                returnedHoldings.positions.shouldContainExactly(
                    listOf(
                        PortfolioHoldingsResponseDTO.PositionDTO(
                            stockSymbol = "GOOG",
                            stockName = "Apple Inc.",
                            stockPrice = BigDecimal("332.6200"),
                            totalVolume = BigDecimal("7.3200"),
                            totalCost = BigDecimal("640.64"),
                            averagePrice = BigDecimal("87.5191"),
                            totalProfit = BigDecimal("1794.14"),
                            profitPercentage = BigDecimal("2.8005"),
                        ),
                        PortfolioHoldingsResponseDTO.PositionDTO(
                            stockSymbol = "MSFT",
                            stockName = "Apple Inc.",
                            stockPrice = BigDecimal("418.6200"),
                            totalVolume = BigDecimal("10.5423"),
                            totalCost = BigDecimal("55.14"),
                            averagePrice = BigDecimal("5.2304"),
                            totalProfit = BigDecimal("4358.08"),
                            profitPercentage = BigDecimal("79.0359"),
                        ),
                    ),
                )
            }

            it("gets empty holdings, when operations does not exist for provided portfolio id") {
                val (portfolio1) = setup()

                val response = mockMvc.get("/api/operations/${portfolio1.id}/holdings").andReturn().response

                val returnedHoldings = objectMapper.readValue(response.contentAsString, PortfolioHoldingsResponseDTO::class.java)

                returnedHoldings.portfolioId.shouldBe(portfolio1.id)
                returnedHoldings.positions.shouldBeEmpty()
            }

            it("gets an error, when provided portfolio id does not exist") {
                setup()

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
                    stockSymbol = "GOOG",
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
                val (_, portfolio2) = setup()

                val body = getOperationsImportRequestDTO()

                val response =
                    mockMvc
                        .post("/api/operations/${portfolio2.id}/operations/import") {
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
                val (_, portfolio2) = setup()

                val batch = getOperationsImportRequestDTO()

                val body =
                    batch.copy(
                        operations =
                            batch.operations.toMutableList().apply {
                                this[0] = this[0].copy(externalId = "external-id-100")
                            },
                    )

                val response =
                    mockMvc
                        .post("/api/operations/${portfolio2.id}/operations/import") {
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
                val (_, portfolio2) = setup()

                every { finnhubService.getSymbolLookup("NVDA", any<String>()) } throws RuntimeException("Error")

                val batch = getOperationsImportRequestDTO()

                val body =
                    batch.copy(
                        operations =
                            batch.operations.toMutableList().apply {
                                this[0] = this[0].copy(externalId = "external-id-100")
                            },
                    )

                val response =
                    mockMvc
                        .post("/api/operations/${portfolio2.id}/operations/import") {
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
                val (_, portfolio2) = setup()

                val batch = getOperationsImportRequestDTO()
                val body =
                    batch.copy(
                        operations =
                            batch.operations.toMutableList().apply {
                                this[1] = this[1].copy(externalId = "external-id-2")
                            },
                    )

                val response =
                    mockMvc
                        .post("/api/operations/${portfolio2.id}/operations/import") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedErrorResponse = objectMapper.readValue(response.contentAsString, ErrorResponse::class.java)

                returnedErrorResponse.shouldBe(
                    ErrorResponse(
                        path = "uri=/api/operations/${portfolio2.id}/operations/import",
                        status = HttpStatus.BAD_REQUEST.value(),
                        errors =
                            listOf(
                                ErrorDTO(message = "Could not find new operations", type = OperationsErrorType.BATCH_EMPTY_OPERATIONS.name),
                            ),
                    ),
                )
            }

            it("gets an error, when provided portfolio id does not exist") {
                setup()
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
