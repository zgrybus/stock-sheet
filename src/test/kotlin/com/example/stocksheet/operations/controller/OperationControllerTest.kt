package com.example.stocksheet.operations.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.operations.dto.HoldingPositionDTO
import com.example.stocksheet.operations.dto.OperationRequestDTO
import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.dto.OperationsImportResponseDTO
import com.example.stocksheet.operations.dto.PortfolioHoldingsDTO
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.exceptions.OperationsErrorType
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.portfolio.exceptions.PortfolioErrorType
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

class OperationControllerTest : BaseIntegrationTest() {
    @Autowired lateinit var operationRepository: OperationRepository

    @Autowired lateinit var portfolioRepository: PortfolioRepository

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
            portfolioPLN = PortfolioEntity(name = "portfolio_name_3", currency = "PLN")
            portfolioEURO = PortfolioEntity(name = "portfolio_name_2", currency = "EUR")

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
                        portfolio = portfolioEURO
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
            operationRepository.deleteAllInBatch()
            portfolioRepository.deleteAllInBatch()
        }

        describe("GET /api/operations/{portfolioId}/holdings") {
            it("gets holdings for provided portfolio id") {
                val response = mockMvc.get("/api/operations/${portfolioUSD.id}/holdings").andReturn().response

                val returnedHoldings = objectMapper.readValue(response.contentAsString, PortfolioHoldingsDTO::class.java)

                returnedHoldings.portfolioId.shouldBe(portfolioUSD.id)
                returnedHoldings.positions.shouldContainExactly(
                    listOf(
                        HoldingPositionDTO(stockSymbol = "GOOG.US", totalVolume = 10.toBigDecimal(), totalCost = 1500.toBigDecimal()),
                        HoldingPositionDTO(stockSymbol = "TSLA.US", totalVolume = 2.toBigDecimal(), totalCost = 1000.toBigDecimal()),
                    ),
                )
            }

            it("gets empty holdings, when operations does not exist for provided portfolio id") {
                val response = mockMvc.get("/api/operations/${portfolioPLN.id}/holdings").andReturn().response

                val returnedHoldings = objectMapper.readValue(response.contentAsString, PortfolioHoldingsDTO::class.java)

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
            fun getOperationRequestDTO(): OperationRequestDTO =
                OperationRequestDTO(
                    externalId = "external-id-1",
                    stockSymbol = "APL.US",
                    type = OperationType.BUY,
                    volume = 10.toBigDecimal(),
                    openDate = LocalDateTime.of(2019, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
                    pricePerVolume = 100.toBigDecimal(),
                    totalPrice = 1000.toBigDecimal(),
                )

            fun getOperationsBatchRequestDTO(): OperationsBatchRequestDTO =
                OperationsBatchRequestDTO(
                    operations =
                        listOf(
                            getOperationRequestDTO(),
                            getOperationRequestDTO().apply {
                                externalId = "external-id-101"
                                stockSymbol = "NVDA.US"
                            },
                        ),
                )

            it("saves new operation and do not save duplicated one") {
                val body = getOperationsBatchRequestDTO()

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
                val batch = getOperationsBatchRequestDTO()
                val body =
                    batch.copy(
                        operations =
                            batch.operations!!.toMutableList().apply {
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
                val batch = getOperationsBatchRequestDTO()
                val body =
                    batch.copy(
                        operations =
                            batch.operations!!.toMutableList().apply {
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
                val body = getOperationsBatchRequestDTO()

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
