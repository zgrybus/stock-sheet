package com.example.stocksheet.stocks.repository

import com.example.stocksheet.BaseRepositoryTest
import com.example.stocksheet.scenarios.StandardMarketScenario
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import org.springframework.beans.factory.annotation.Autowired

class StockRepositoryTest : BaseRepositoryTest() {
    @Autowired lateinit var stockRepository: StockRepository

    @Autowired lateinit var standardMarketScenario: StandardMarketScenario

    init {
        describe("findAllBySymbolIn") {
            it("returns empty list for empty input") {
                standardMarketScenario.setup()
                entityManager.flush()
                entityManager.clear()

                val returnedStocks = stockRepository.findAllBySymbolIn(setOf())

                returnedStocks.shouldHaveSize(0)
            }

            it("returns only existing symbols") {
                val data = standardMarketScenario.setup()
                entityManager.flush()
                entityManager.clear()

                val returnedStocks = stockRepository.findAllBySymbolIn(setOf("AAPL.US", "MSC", "TSL"))

                returnedStocks.shouldHaveSize(1)
                returnedStocks[0].shouldBeEqualToComparingFields(
                    data.stocks[1],
                )
            }

            it("requires exact symbol match") {
                val data = standardMarketScenario.setup()
                entityManager.flush()
                entityManager.clear()

                val returnedStocks = stockRepository.findAllBySymbolIn(setOf("AAPL", "GOOGL.US"))

                returnedStocks.shouldHaveSize(1)
                returnedStocks[0].shouldBeEqualToComparingFields(
                    data.stocks[0],
                )
            }

            it("returns multiple existing symbols") {
                val data = standardMarketScenario.setup()
                entityManager.flush()
                entityManager.clear()

                val returnedStocks = stockRepository.findAllBySymbolIn(setOf("GOOGL.US", "AAPL.US"))

                returnedStocks.shouldHaveSize(2)
                returnedStocks[0].shouldBeEqualToComparingFields(
                    data.stocks[0],
                )
                returnedStocks[1].shouldBeEqualToComparingFields(
                    data.stocks[1],
                )
            }
        }
    }
}
