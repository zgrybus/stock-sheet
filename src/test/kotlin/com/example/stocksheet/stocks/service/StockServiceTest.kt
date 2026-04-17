package com.example.stocksheet.stocks.service

import com.example.stocksheet.integration.fmp.dto.FmpCompanyProfileResponseDTO
import com.example.stocksheet.integration.fmp.service.FmpService
import com.example.stocksheet.mocks.createMockStockEntity
import com.example.stocksheet.stocks.entity.DividendFrequency
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
import java.math.BigDecimal

class StockServiceTest : DescribeSpec() {
    private val stockRepositoryMock = mockk<StockRepository>()
    private val fmpServiceMock = mockk<FmpService>()

    private val stockService = StockService(stockRepositoryMock, StockMapper(), fmpServiceMock)

    init {
        beforeEach {
            every { stockRepositoryMock.findAllBySymbolIn(any<List<String>>()) } answers {
                val symbols = firstArg<List<String>>()
                symbols.take(2).mapIndexed { index, symbol ->
                    createMockStockEntity(
                        id = (1000 + index).toLong(),
                        symbol = symbol,
                        dividend = BigDecimal.ZERO,
                        price = BigDecimal.ZERO,
                    )
                }
            }

            every { stockRepositoryMock.saveAll(any<List<StockEntity>>()) } answers {
                val stocksToSave = firstArg<List<StockEntity>>()
                stocksToSave.mapIndexed { index, symbol ->
                    symbol.id = (2000 + index).toLong()
                    symbol
                }
            }

            every { fmpServiceMock.getCompanyProfile(any<String>()) } answers {
                val symbol = firstArg<String>()

                FmpCompanyProfileResponseDTO(
                    name = "name".plus(symbol),
                    symbol = symbol,
                    exchange = "exchange".plus(symbol),
                    industry = "industry".plus(symbol),
                    lastDividend = BigDecimal("1.0025"),
                    price = BigDecimal("520.5222"),
                )
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

                capturedSymbols.captured.shouldBe(listOf<String>())
            }

            it("calls repository with all provided symbols") {
                stockService.getOrCreateStocks(
                    listOf(
                        StockService.StockIdentifier(symbol = "NVDA"),
                        StockService.StockIdentifier(symbol = "AAPL"),
                        StockService.StockIdentifier(symbol = "GOOG"),
                        StockService.StockIdentifier(symbol = "TSL"),
                    ),
                )

                val capturedSymbols = slot<List<String>>()
                verify {
                    stockRepositoryMock.findAllBySymbolIn(capture(capturedSymbols))
                }

                capturedSymbols.captured.shouldBe(listOf("NVDA", "AAPL", "GOOG", "TSL"))
            }

            it("calls saveAll only for missing symbols") {
                stockService.getOrCreateStocks(
                    listOf(
                        StockService.StockIdentifier(symbol = "NVDA"),
                        StockService.StockIdentifier(symbol = "AAPL"),
                        StockService.StockIdentifier(symbol = "GOOG"),
                        StockService.StockIdentifier(symbol = "TSL"),
                    ),
                )

                val capturedCreatedSymbols = slot<List<StockEntity>>()
                verify {
                    stockRepositoryMock.saveAll(capture(capturedCreatedSymbols))
                }
                capturedCreatedSymbols.captured.shouldHaveSize(2)
                capturedCreatedSymbols.captured[0].shouldBeEqualToComparingFields(
                    createMockStockEntity(
                        symbol = "GOOG",
                        id = 2000L,
                        industry = "industryGOOG",
                        exchange = "exchangeGOOG",
                        name = "nameGOOG",
                        dividend = BigDecimal("1.0025"),
                        price = BigDecimal("520.5222"),
                        dividendFrequency = DividendFrequency.NONE,
                    ),
                )
                capturedCreatedSymbols.captured[1].shouldBeEqualToComparingFields(
                    createMockStockEntity(
                        symbol = "TSL",
                        id = 2001L,
                        industry = "industryTSL",
                        exchange = "exchangeTSL",
                        name = "nameTSL",
                        dividend = BigDecimal("1.0025"),
                        price = BigDecimal("520.5222"),
                        dividendFrequency = DividendFrequency.NONE,
                    ),
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
                            StockService.StockIdentifier(symbol = "NVDA"),
                            StockService.StockIdentifier(symbol = "AAPL"),
                            StockService.StockIdentifier(symbol = "GOOG"),
                        ),
                    )

                result.shouldHaveSize(3)
                result[0].shouldBeEqualToComparingFields(
                    createMockStockEntity(
                        symbol = "NVDA",
                        id = 1000L,
                        price = BigDecimal.ZERO,
                        dividend = BigDecimal.ZERO,
                    ),
                )
                result[1].shouldBeEqualToComparingFields(
                    createMockStockEntity(
                        symbol = "AAPL",
                        id = 1001L,
                        price = BigDecimal.ZERO,
                        dividend = BigDecimal.ZERO,
                    ),
                )
                result[2].shouldBeEqualToComparingFields(
                    createMockStockEntity(
                        symbol = "GOOG",
                        id = 2000L,
                        industry = "industryGOOG",
                        exchange = "exchangeGOOG",
                        name = "nameGOOG",
                        dividend = BigDecimal("1.0025"),
                        price = BigDecimal("520.5222"),
                        dividendFrequency = DividendFrequency.NONE,
                    ),
                )
            }

            it("ignores failing symbols and successfully saves the rest") {
                every { stockRepositoryMock.findAllBySymbolIn(any<List<String>>()) } returns emptyList()
                every { fmpServiceMock.getCompanyProfile("FAIL") } throws RuntimeException("Error")
                every { fmpServiceMock.getCompanyProfile("SUCCESS") } answers {
                    val symbol = firstArg<String>()
                    FmpCompanyProfileResponseDTO(
                        name = "name".plus(symbol),
                        symbol = symbol,
                        exchange = "exchange".plus(symbol),
                        industry = "industry".plus(symbol),
                        lastDividend = BigDecimal("1.0025"),
                        price = BigDecimal("520.5222"),
                    )
                }

                val result =
                    stockService.getOrCreateStocks(
                        listOf(
                            StockService.StockIdentifier("FAIL"),
                            StockService.StockIdentifier("SUCCESS"),
                        ),
                    )

                result.shouldHaveSize(2)
                result[0].shouldBeEqualToComparingFields(
                    createMockStockEntity(
                        symbol = "FAIL",
                        id = 2000L,
                        industry = "",
                        exchange = "",
                        name = "FAIL",
                        price = BigDecimal.ZERO,
                        dividend = BigDecimal.ZERO,
                    ),
                )
                result[1].shouldBeEqualToComparingFields(
                    createMockStockEntity(
                        symbol = "SUCCESS",
                        id = 2001L,
                        industry = "industrySUCCESS",
                        exchange = "exchangeSUCCESS",
                        name = "nameSUCCESS",
                        dividend = BigDecimal("1.0025"),
                        price = BigDecimal("520.5222"),
                        dividendFrequency = DividendFrequency.NONE,
                    ),
                )
            }

            it("uses fallback values when company profile is null") {
                every { stockRepositoryMock.findAllBySymbolIn(any<List<String>>()) } returns emptyList()
                every { fmpServiceMock.getCompanyProfile(any<String>()) } returns null

                val result =
                    stockService.getOrCreateStocks(
                        listOf(
                            StockService.StockIdentifier("VWRA"),
                        ),
                    )

                result.shouldHaveSize(1)
                result[0].shouldBeEqualToComparingFields(
                    createMockStockEntity(
                        symbol = "VWRA",
                        id = 2000L,
                        industry = "",
                        exchange = "",
                        name = "VWRA",
                        price = BigDecimal.ZERO,
                        dividend = BigDecimal.ZERO,
                    ),
                )
            }
        }
    }
}
