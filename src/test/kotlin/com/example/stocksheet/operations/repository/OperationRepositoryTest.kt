package com.example.stocksheet.operations.repository

import com.example.stocksheet.BaseRepositoryTest
import com.example.stocksheet.mocks.createMockOperationEntity
import com.example.stocksheet.mocks.createMockStockQuoteEntityMock
import com.example.stocksheet.scenarios.StandardMarketScenario
import com.example.stocksheet.stocks.quotes.repository.StockQuoteRepository
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.LocalDate

class OperationRepositoryTest : BaseRepositoryTest() {
    @Autowired lateinit var operationRepository: OperationRepository

    @Autowired lateinit var stockQuoteRepository: StockQuoteRepository

    @Autowired lateinit var standardMarketScenario: StandardMarketScenario

    init {
        describe("calculatePortfolioSummaryByPortfolioId") {
            it("returns zero when portfolio has no assigned operations") {
                val data = standardMarketScenario.setup()
                entityManager.flush()
                entityManager.clear()

                val portfolioPLN = data.portfolios[1]

                val portfolioSummary = operationRepository.calculatePortfolioSummaryByPortfolioId(portfolioPLN.id!!)

                portfolioSummary.shouldBe(
                    OperationRepository.PortfolioSummaryProjection(totalValue = BigDecimal.ZERO, investedCapital = BigDecimal.ZERO),
                )
            }

            it("returns sum of total price for all operations within specified portfolio") {
                val data = standardMarketScenario.setup()
                entityManager.flush()
                entityManager.clear()

                val portfolioUSD = data.portfolios[0]

                val portfolioSummary = operationRepository.calculatePortfolioSummaryByPortfolioId(portfolioUSD.id!!)

                portfolioSummary.totalValue.shouldBeEqualComparingTo(BigDecimal("6128.75"))
                portfolioSummary.investedCapital.shouldBeEqualComparingTo(BigDecimal("2400.00"))
            }
        }

        describe("getHoldingsSummaryByPortfolioId") {
            it("aggregates multiple operations into correct portfolio positions and orders them") {
                val data = standardMarketScenario.setup()
                val portfolioUSD = data.portfolios[0]

                val newOperation1 =
                    createMockOperationEntity(
                        externalId = "new_operation_id_1",
                        volume = BigDecimal("92.3423"),
                        pricePerVolume = BigDecimal("2.18"),
                        stock = data.stocks[1],
                        portfolio = portfolioUSD,
                    )
                operationRepository.save(newOperation1)

                entityManager.flush()
                entityManager.clear()

                val holdings = operationRepository.getHoldingsSummaryByPortfolioId(portfolioUSD.id!!)

                holdings.shouldHaveSize(2)
                holdings.shouldContainExactly(
                    listOf(
                        OperationRepository.PortfolioHoldingProjection(
                            stockName = "Apple",
                            stockSymbol = "AAPL.US",
                            totalVolume = BigDecimal("107.3423"),
                            totalCost = BigDecimal("2101.31"),
                            stockPrice = BigDecimal("75.2500"),
                        ),
                        OperationRepository.PortfolioHoldingProjection(
                            stockName = "Alphabet",
                            stockSymbol = "GOOGL.US",
                            totalVolume = BigDecimal("50.0000"),
                            totalCost = BigDecimal("500.00"),
                            stockPrice = BigDecimal("100.0000"),
                        ),
                    ),
                )
            }
        }

        describe("calculateValuationSnapshotByPortfolioId") {
            it("calculates valuation using available historical quotes for all stocks") {
                val data = standardMarketScenario.setup()

                val referenceDate = LocalDate.now().minusDays(1)

                val stock1Quote =
                    createMockStockQuoteEntityMock(
                        stock = data.stocks[0],
                        closedPrice = BigDecimal("70.5"),
                        date = referenceDate,
                    )
                val stock2Quote =
                    createMockStockQuoteEntityMock(
                        stock = data.stocks[1],
                        closedPrice = BigDecimal("60.45"),
                        date = referenceDate,
                    )
                stockQuoteRepository.saveAll(listOf(stock1Quote, stock2Quote))

                entityManager.flush()
                entityManager.clear()

                val portfolioUSD = data.portfolios[0]

                val valuationSnapshot = operationRepository.calculateValuationSnapshotByPortfolioId(portfolioUSD.id!!, referenceDate)

                valuationSnapshot.currentValue.shouldBeEqualComparingTo(BigDecimal("6128.75"))
                valuationSnapshot.historicalValue.shouldBeEqualComparingTo(BigDecimal("4431.75"))
            }

            it("handles zero-price quotes without affecting other stock calculations") {
                val data = standardMarketScenario.setup()

                val referenceDate = LocalDate.now().minusDays(1)

                val stock1Quote =
                    createMockStockQuoteEntityMock(
                        stock = data.stocks[0],
                        closedPrice = BigDecimal.ZERO,
                        date = referenceDate,
                    )
                val stock2Quote =
                    createMockStockQuoteEntityMock(
                        stock = data.stocks[1],
                        closedPrice = BigDecimal("60.45"),
                        date = referenceDate,
                    )
                stockQuoteRepository.saveAll(listOf(stock1Quote, stock2Quote))

                entityManager.flush()
                entityManager.clear()

                val portfolioUSD = data.portfolios[0]

                val valuationSnapshot = operationRepository.calculateValuationSnapshotByPortfolioId(portfolioUSD.id!!, referenceDate)

                valuationSnapshot.currentValue.shouldBeEqualComparingTo(BigDecimal("6128.75"))
                valuationSnapshot.historicalValue.shouldBeEqualComparingTo(BigDecimal("906.75"))
            }

            it("fallbacks to current stock price when historical quote is missing") {
                val data = standardMarketScenario.setup()

                val referenceDate = LocalDate.now().minusDays(1)

                val stock2Quote =
                    createMockStockQuoteEntityMock(
                        stock = data.stocks[1],
                        closedPrice = BigDecimal("60.45"),
                        date = referenceDate,
                    )
                stockQuoteRepository.saveAll(listOf(stock2Quote))

                entityManager.flush()
                entityManager.clear()

                val portfolioUSD = data.portfolios[0]

                val valuationSnapshot = operationRepository.calculateValuationSnapshotByPortfolioId(portfolioUSD.id!!, referenceDate)

                valuationSnapshot.currentValue.shouldBeEqualComparingTo(BigDecimal("6128.75"))
                valuationSnapshot.historicalValue.shouldBeEqualComparingTo(BigDecimal("5906.75"))
            }
        }
    }
}
