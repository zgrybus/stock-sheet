package com.example.stocksheet.operations.repository

import com.example.stocksheet.BaseRepositoryTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.LocalDate

class OperationRepositoryTest : BaseRepositoryTest() {
    @Autowired lateinit var operationRepository: OperationRepository

    init {
        fun setup() =
            withFlushedTransaction {
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
                }

                Triple(portfolio1, portfolio2, referenceDate)
            }

        describe("calculatePortfolioSummaryByPortfolioId") {
            it("returns zero when portfolio has no assigned operations") {
                val (portfolio1) = setup()

                val portfolioSummary = operationRepository.calculatePortfolioSummaryByPortfolioId(portfolio1.id!!)

                portfolioSummary.shouldBe(
                    OperationRepository.PortfolioSummaryProjection(totalValue = BigDecimal.ZERO, investedCapital = BigDecimal.ZERO),
                )
            }

            it("returns sum of total price for all operations within specified portfolio") {
                val (_, portfolio2) = setup()

                val portfolioSummary = operationRepository.calculatePortfolioSummaryByPortfolioId(portfolio2.id!!)

                portfolioSummary.totalValue.shouldBeEqualComparingTo(BigDecimal("6847.99602600"))
                portfolioSummary.investedCapital.shouldBeEqualComparingTo(BigDecimal("695.78"))
            }
        }

        describe("getHoldingsSummaryByPortfolioId") {
            it("aggregates multiple operations into correct portfolio positions and orders them") {
                val (_, portfolio2) = setup()

                val holdings = operationRepository.getHoldingsSummaryByPortfolioId(portfolio2.id!!)

                holdings.shouldHaveSize(2)
                holdings.shouldContainExactly(
                    listOf(
                        OperationRepository.PortfolioHoldingProjection(
                            stockName = "Apple Inc.",
                            stockSymbol = "GOOG",
                            totalVolume = BigDecimal("7.3200"),
                            totalCost = BigDecimal("640.64"),
                            stockPrice = BigDecimal("332.6200"),
                        ),
                        OperationRepository.PortfolioHoldingProjection(
                            stockName = "Apple Inc.",
                            stockSymbol = "MSFT",
                            totalVolume = BigDecimal("10.5423"),
                            totalCost = BigDecimal("55.14"),
                            stockPrice = BigDecimal("418.6200"),
                        ),
                    ),
                )
            }
        }

        describe("calculateValuationSnapshotByPortfolioId") {
            it("calculates valuation using available historical quotes for all stocks") {
                val (_, portfolio2, referenceDate) = setup()

                val valuationSnapshot = operationRepository.calculateValuationSnapshotByPortfolioId(portfolio2.id!!, referenceDate)

                valuationSnapshot.currentValue.shouldBeEqualComparingTo(BigDecimal("6847.99602600"))
                valuationSnapshot.historicalValue.shouldBeEqualComparingTo(BigDecimal("6635.61507600"))
            }

            it("handles zero-price quotes without affecting other stock calculations") {
                val (_, portfolio2, referenceDate) = setup()

                withFlushedTransaction {
                    val stock3 =
                        testDb.createStockEntity {
                            symbol = "AMD"
                            price = BigDecimal("274.00")
                        }

                    testDb.createStockQuoteEntity {
                        date = referenceDate
                        stock = stock3
                        closedPrice = BigDecimal.ZERO
                    }

                    testDb.createOperationEntity {
                        portfolio = portfolio2
                        stock = stock3
                        volume = BigDecimal("2")
                        pricePerVolume = BigDecimal("200.00")
                    }
                }

                val valuationSnapshot = operationRepository.calculateValuationSnapshotByPortfolioId(portfolio2.id!!, referenceDate)

                valuationSnapshot.currentValue.shouldBeEqualComparingTo(BigDecimal("7395.99602600"))
                valuationSnapshot.historicalValue.shouldBeEqualComparingTo(BigDecimal("6635.61507600"))
            }

            it("fallbacks to current stock price when historical quote is missing") {
                val (_, portfolio2, referenceDate) = setup()

                withFlushedTransaction {
                    val stock3 =
                        testDb.createStockEntity {
                            symbol = "AMD"
                            price = BigDecimal("274.00")
                        }

                    testDb.createOperationEntity {
                        portfolio = portfolio2
                        stock = stock3
                        volume = BigDecimal("2")
                        pricePerVolume = BigDecimal("200.00")
                    }
                }

                val valuationSnapshot = operationRepository.calculateValuationSnapshotByPortfolioId(portfolio2.id!!, referenceDate)

                valuationSnapshot.currentValue.shouldBeEqualComparingTo(BigDecimal("7395.99602600"))
                valuationSnapshot.historicalValue.shouldBeEqualComparingTo(BigDecimal("7183.61507600"))
            }
        }
    }
}
