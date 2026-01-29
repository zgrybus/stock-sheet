package com.example.stocksheet.operations.controller

import com.example.stocksheet.BaseIntegrationTest
import com.example.stocksheet.operations.dto.OperationImportResponseDTO
import com.example.stocksheet.operations.dto.OperationRequestDTO
import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.dto.PortfolioSummaryDTO
import com.example.stocksheet.operations.dto.StockPositionDTO
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.repository.OperationRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

class OperationControllerTest : BaseIntegrationTest() {
    @Autowired lateinit var operationRepository: OperationRepository

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
                currency = "USD",
            )

        beforeEach {
            operationRepository.saveAll(
                listOf(
                    getOperationEntity(),
                    getOperationEntity().apply {
                        externalId = "external-id-2"
                        volume = 15.toBigDecimal()
                        pricePerVolume = 95.toBigDecimal()
                        totalPrice = 1425.toBigDecimal()
                        currency = "EUR"
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
            operationRepository.deleteAll()
        }

        val currency = "USD"

        describe("GET /api/operations/portfolio/{currency}") {
            it("gets portfolio for provided currency") {
                val response = mockMvc.get("/api/operations/portfolio/$currency").andReturn().response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, PortfolioSummaryDTO::class.java)

                returnedPortfolio.currency.shouldBe(currency)
                returnedPortfolio.positions.shouldContainExactly(
                    listOf(
                        StockPositionDTO(stockSymbol = "GOOG.US", totalVolume = 10.toBigDecimal(), totalCost = 1500.toBigDecimal()),
                        StockPositionDTO(stockSymbol = "TSLA.US", totalVolume = 2.toBigDecimal(), totalCost = 1000.toBigDecimal()),
                    ),
                )
            }

            it("gets empty positions, when operations does not exist for provided currency") {
                val response = mockMvc.get("/api/operations/portfolio/PLN").andReturn().response

                val returnedPortfolio = objectMapper.readValue(response.contentAsString, PortfolioSummaryDTO::class.java)

                returnedPortfolio.currency.shouldBe("PLN")
                returnedPortfolio.positions.shouldBeEmpty()
            }
        }

        describe("POST /api/operations/import/{currency}") {
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
                        .post("/api/operations/import/$currency") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedResponse = objectMapper.readValue(response.contentAsString, OperationImportResponseDTO::class.java)

                returnedResponse.shouldBe(
                    OperationImportResponseDTO(
                        added = listOf(OperationImportResponseDTO.OperationSummaryDTO(returnedResponse.added[0].id, "external-id-101")),
                        duplicated =
                            listOf(
                                OperationImportResponseDTO.OperationSummaryDTO(returnedResponse.duplicated[0].id, "external-id-1"),
                            ),
                    ),
                )
            }

            it("does not import any operations when all are duplicates") {
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
                        .post("/api/operations/import/$currency") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedResponse = objectMapper.readValue(response.contentAsString, OperationImportResponseDTO::class.java)

                returnedResponse.shouldBe(
                    OperationImportResponseDTO(
                        added =
                            listOf(),
                        duplicated =
                            listOf(
                                OperationImportResponseDTO.OperationSummaryDTO(returnedResponse.duplicated[0].id, "external-id-1"),
                                OperationImportResponseDTO.OperationSummaryDTO(returnedResponse.duplicated[1].id, "external-id-2"),
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
                        .post("/api/operations/import/$currency") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(body)
                        }.andReturn()
                        .response

                val returnedResponse = objectMapper.readValue(response.contentAsString, OperationImportResponseDTO::class.java)

                returnedResponse.shouldBe(
                    OperationImportResponseDTO(
                        added =
                            listOf(
                                OperationImportResponseDTO.OperationSummaryDTO(returnedResponse.added[0].id, "external-id-100"),
                                OperationImportResponseDTO.OperationSummaryDTO(returnedResponse.added[1].id, "external-id-101"),
                            ),
                        duplicated =
                            listOf(),
                    ),
                )
            }
        }
    }
}
