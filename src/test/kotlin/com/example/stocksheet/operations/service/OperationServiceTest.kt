package com.example.stocksheet.operations.service

import com.example.stocksheet.mocks.createMockOperationsList
import com.example.stocksheet.mocks.createMockPortfolioEntity
import com.example.stocksheet.mocks.createMockStockEntity
import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
import com.example.stocksheet.operations.dto.PortfolioHoldingsResponseDTO
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.entity.OperationType
import com.example.stocksheet.operations.mappers.OperationsMapper
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import com.example.stocksheet.stocks.service.StockService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.util.Optional

class OperationServiceTest : DescribeSpec() {
    private val operationRepositoryMock: OperationRepository = mockk()
    private val portfolioRepositoryMock: PortfolioRepository = mockk()
    private val stockServiceMock: StockService = mockk()
    private val operationsMapper = OperationsMapper()
    private val operationService = OperationService(operationRepositoryMock, portfolioRepositoryMock, operationsMapper, stockServiceMock)

    init {
        val operations =
            createMockOperationsList().apply {
                this[0].id = 100
                this[0].stock = createMockStockEntity(symbol = "GOOG", name = "Alphabet")

                this[1].id = 101
                this[1].stock = createMockStockEntity(symbol = "TSL", name = "Tesla")

                this[2].id = 102

                this[3].id = 103
            }
        val portfolio = createMockPortfolioEntity(id = 1003)

        beforeEach {
            every { portfolioRepositoryMock.existsById(portfolio.id!!) } returns true
        }

        afterEach {
            clearAllMocks()
        }

        describe("OperationServiceTest") {
            describe("getHoldings") {
                beforeEach {
                    every { operationRepositoryMock.findAllByPortfolioId(portfolio.id!!) } returns operations
                }

                it("groups and sum operations correctly") {
                    val result = operationService.getHoldings(portfolio.id!!)

                    result.portfolioId.shouldBe(portfolio.id)
                    result.positions.shouldContainExactly(
                        listOf(
                            PortfolioHoldingsResponseDTO.PositionDTO(
                                stockSymbol = "GOOG",
                                totalCost = 1500.toBigDecimal(),
                                totalVolume = 10.toBigDecimal(),
                            ),
                            PortfolioHoldingsResponseDTO.PositionDTO(
                                stockSymbol = "TSL",
                                totalCost = 400.toBigDecimal(),
                                totalVolume = 5.toBigDecimal(),
                            ),
                            PortfolioHoldingsResponseDTO.PositionDTO(
                                stockSymbol = "AAPL",
                                totalCost = 1500.toBigDecimal(),
                                totalVolume = 150.toBigDecimal(),
                            ),
                        ),
                    )
                }
            }

            describe("importOperations") {
                fun getOperationRequestDTO(): OperationsImportRequestDTO.OperationRequestDTO =
                    OperationsImportRequestDTO.OperationRequestDTO(
                        externalId = "external-id-1",
                        stockSymbol = "AAPL",
                        type = OperationType.BUY,
                        volume = 10.toBigDecimal(),
                        openDate = LocalDateTime.of(2019, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
                        pricePerVolume = 100.toBigDecimal(),
                        totalPrice = 1000.toBigDecimal(),
                    )

                fun getOperationsImportRequestDTO(): OperationsImportRequestDTO =
                    OperationsImportRequestDTO(
                        operations =
                            listOf(
                                getOperationRequestDTO(),
                                getOperationRequestDTO().apply {
                                    externalId = "external-id-101"
                                    stockSymbol = "NVDA"
                                },
                            ),
                    )

                beforeEach {
                    every { operationRepositoryMock.findAllByExternalIdIn(any()) } returns operations
                    every { operationRepositoryMock.saveAll<OperationEntity>(any()) } returns listOf<OperationEntity>()
                    every { portfolioRepositoryMock.findById(portfolio.id!!) } returns Optional.of(portfolio)

                    every { stockServiceMock.getStock(any()) } answers {
                        val requestedSymbol = firstArg<String>()
                        createMockStockEntity(symbol = requestedSymbol)
                    }
                }

                it("fetches entities for requested external ids") {
                    operationService.importOperations(getOperationsImportRequestDTO(), portfolio.id!!)

                    val externalIds = slot<List<String>>()
                    verify {
                        operationRepositoryMock.findAllByExternalIdIn(capture(externalIds))
                    }

                    externalIds.captured.shouldContainExactly(listOf("external-id-1", "external-id-101"))
                }

                it("saves only new operations to the database") {
                    val batch = getOperationsImportRequestDTO()
                    operationService.importOperations(batch, portfolio.id!!)

                    val newOperations = slot<List<OperationEntity>>()
                    verify {
                        operationRepositoryMock.saveAll(capture(newOperations))
                    }

                    val nvdaOperation = batch.operations[1]
                    val expectedStock = createMockStockEntity(symbol = nvdaOperation.stockSymbol)

                    newOperations.captured.shouldHaveSize(1)
                    newOperations.captured[0].shouldBeEqualToComparingFields(
                        operationsMapper.toEntity(nvdaOperation, portfolio, expectedStock),
                    )
                }
            }
        }
    }
}
