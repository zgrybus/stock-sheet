package com.example.stocksheet.operations.service

import com.example.stocksheet.mocks.createMockOperationEntity
import com.example.stocksheet.mocks.createMockPortfolioEntity
import com.example.stocksheet.mocks.createMockStockEntity
import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
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
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.util.Optional

class OperationServiceTest : DescribeSpec() {
    private val operationRepositoryMock: OperationRepository = mockk()
    private val portfolioRepositoryMock: PortfolioRepository = mockk()
    private val stockServiceMock: StockService = mockk()
    private val operationService =
        OperationService(
            operationRepositoryMock,
            portfolioRepositoryMock,
            OperationsMapper(),
            stockServiceMock,
        )

    init {
        val portfolio = createMockPortfolioEntity(id = 1003)

        beforeEach {
            every { portfolioRepositoryMock.existsById(portfolio.id!!) } returns true
        }

        afterEach {
            clearAllMocks()
        }

        describe("OperationServiceTest") {
            describe("importOperations") {
                fun getOperationRequestDTO(): OperationsImportRequestDTO.OperationRequestDTO =
                    OperationsImportRequestDTO.OperationRequestDTO(
                        externalId = "external-id-1",
                        stockSymbol = "AAPL",
                        type = OperationType.BUY,
                        volume = BigDecimal("10.00"),
                        openDate = LocalDateTime.of(2024, Month.APRIL, 10, 10, 15).toInstant(ZoneOffset.UTC),
                        pricePerVolume = BigDecimal("150.00"),
                        totalPrice = BigDecimal("1500.00"),
                    )

                fun getOperationsImportRequestDTO(): OperationsImportRequestDTO =
                    OperationsImportRequestDTO(
                        operations =
                            listOf(
                                getOperationRequestDTO(),
                                getOperationRequestDTO().apply {
                                    externalId = "external-id-2"
                                    stockSymbol = "NVDA"
                                },
                                getOperationRequestDTO().apply {
                                    externalId = "external-id-3"
                                    stockSymbol = "NVDA"
                                },
                            ),
                    )

                beforeEach {
                    val operationExternalIdProjection =
                        listOf(
                            object : OperationRepository.OperationExternalIdProjection {
                                override val id: Long = 100L
                                override val externalId: String = "external-id-1"
                            },
                            object : OperationRepository.OperationExternalIdProjection {
                                override val id: Long = 101L
                                override val externalId: String = "external-id-3"
                            },
                        )

                    every { operationRepositoryMock.findAllByExternalIdIn(any()) } returns operationExternalIdProjection
                    every { portfolioRepositoryMock.findById(portfolio.id!!) } returns Optional.of(portfolio)

                    every { stockServiceMock.getOrCreateStocks(any<Set<String>>()) } answers {
                        val requestedSymbols = firstArg<Set<String>>()
                        requestedSymbols.map { createMockStockEntity(symbol = it) }
                    }

                    every { operationRepositoryMock.saveAll(any<List<OperationEntity>>()) } answers {
                        val entitiesToSave = firstArg<List<OperationEntity>>()
                        entitiesToSave.mapIndexed { index, entity ->
                            entity.id = (1000 + index).toLong()
                            entity
                        }
                    }
                }

                it("fetches entities for requested external ids") {
                    operationService.importOperations(getOperationsImportRequestDTO(), portfolio.id!!)

                    val externalIds = slot<List<String>>()
                    verify {
                        operationRepositoryMock.findAllByExternalIdIn(capture(externalIds))
                    }

                    externalIds.captured.shouldContainExactly(listOf("external-id-1", "external-id-2", "external-id-3"))
                }

                it("extracts and pass unique stock symbols to stock service") {
                    every { operationRepositoryMock.findAllByExternalIdIn(any()) } returns listOf()

                    operationService.importOperations(getOperationsImportRequestDTO(), portfolio.id!!)

                    val uniqueStockSymbols = slot<Set<String>>()
                    verify {
                        stockServiceMock.getOrCreateStocks(capture(uniqueStockSymbols))
                    }

                    uniqueStockSymbols.captured.shouldContainExactly(setOf("AAPL", "NVDA"))
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
                        createMockOperationEntity(
                            id = 1000L,
                            stock = expectedStock,
                            portfolio = portfolio,
                            externalId = "external-id-2",
                            totalPrice = BigDecimal("1500.00"),
                        ),
                    )
                }
            }
        }
    }
}
