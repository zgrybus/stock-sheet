package com.example.stocksheet.stocks.service

import com.example.stocksheet.integration.finnhub.service.FinnhubService
import com.example.stocksheet.mocks.createMockStockEntity
import com.example.stocksheet.stocks.entity.StockEntity
import com.example.stocksheet.stocks.mappers.StockMapper
import com.example.stocksheet.stocks.repository.StockRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class StockServiceTest : DescribeSpec() {
    private val stockRepositoryMock = mockk<StockRepository>()
    private val finnhubServiceMock = mockk<FinnhubService>()

    private val stockService = StockService(stockRepositoryMock, StockMapper(), finnhubServiceMock)

    init {
        beforeEach {
            every { stockRepositoryMock.findAllBySymbolIn(any<List<String>>()) } answers {
                val symbols = firstArg<Set<String>>()
                symbols.take(2).mapIndexed { index, symbol ->
                    createMockStockEntity(id = (1000 + index).toLong(), symbol = symbol)
                }
            }

            every { stockRepositoryMock.saveAll(any<List<StockEntity>>()) } answers {
                val stocksToSave = firstArg<List<StockEntity>>()
                stocksToSave.mapIndexed { index, symbol ->
                    symbol.id = (2000 + index).toLong()
                    symbol
                }
            }
        }

        afterEach {
            clearAllMocks()
        }

        describe("getOrCreateStocks") {
            it("calls repository with an empty set when input is empty") {
                stockService.getOrCreateStocks(listOf<StockService.StockIdentifier>())

                val capturedSymbols = slot<List<String>>()
                verify {
                    stockRepositoryMock.findAllBySymbolIn(capture(capturedSymbols))
                }

                capturedSymbols.captured.shouldBe(setOf<String>())
            }

            it("calls repository with all provided symbols") {
                stockService.getOrCreateStocks(
                    listOf(
                        StockService.StockIdentifier(symbol = "NVDA", exchange = "US"),
                        StockService.StockIdentifier(symbol = "AAPL", exchange = "L"),
                        StockService.StockIdentifier(symbol = "GOOG", exchange = "US"),
                        StockService.StockIdentifier(symbol = "TSL", exchange = "US"),
                    ),
                )

                val capturedSymbols = slot<List<String>>()
                verify {
                    stockRepositoryMock.findAllBySymbolIn(capture(capturedSymbols))
                }

                capturedSymbols.captured.shouldBe(setOf("NVDA", "AAPL", "GOOG", "TSL"))
            }

            it("calls saveAll only for missing symbols") {
                stockService.getOrCreateStocks(
                    listOf(
                        StockService.StockIdentifier(symbol = "NVDA", exchange = "US"),
                        StockService.StockIdentifier(symbol = "AAPL", exchange = "L"),
                        StockService.StockIdentifier(symbol = "GOOG", exchange = "US"),
                        StockService.StockIdentifier(symbol = "TSL", exchange = "US"),
                    ),
                )

                val capturedCreatedSymbols = slot<List<StockEntity>>()
                verify {
                    stockRepositoryMock.saveAll(capture(capturedCreatedSymbols))
                }
                capturedCreatedSymbols.captured.shouldHaveSize(2)
                capturedCreatedSymbols.captured[0].shouldBeEqualToComparingFields(
                    createMockStockEntity(symbol = "GOOG", id = 2000L, industry = "GOOG", exchange = "GOOG", name = "GOOG"),
                )
                capturedCreatedSymbols.captured[1].shouldBeEqualToComparingFields(
                    createMockStockEntity(symbol = "TSL", id = 2001L, industry = "TSL", exchange = "TSL", name = "TSL"),
                )
            }

            it("returns an empty list when input is empty") {
                val result = stockService.getOrCreateStocks(listOf())

                result.shouldBe(setOf<String>())
            }

            it("returns a combined list of existing and newly created stocks") {
                val result =
                    stockService.getOrCreateStocks(
                        listOf(
                            StockService.StockIdentifier(symbol = "NVDA", exchange = "US"),
                            StockService.StockIdentifier(symbol = "AAPL", exchange = "L"),
                            StockService.StockIdentifier(symbol = "GOOG", exchange = "US"),
                        ),
                    )

                result.shouldHaveSize(3)
                result[0].shouldBeEqualToComparingFields(
                    createMockStockEntity(symbol = "NVDA", id = 1000L),
                )
                result[1].shouldBeEqualToComparingFields(
                    createMockStockEntity(symbol = "AAPL", id = 1001L),
                )
                result[2].shouldBeEqualToComparingFields(
                    createMockStockEntity(symbol = "GOOG", id = 2000L, industry = "GOOG", exchange = "GOOG", name = "GOOG"),
                )
            }
        }
    }
}
