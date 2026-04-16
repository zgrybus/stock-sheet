package com.example.stocksheet.stocks.repository

import com.example.stocksheet.BaseRepositoryTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import org.springframework.beans.factory.annotation.Autowired

class StockRepositoryTest : BaseRepositoryTest() {
    @Autowired lateinit var stockRepository: StockRepository

    init {
        fun setup() =
            withFlushedTransaction {
                val stock1 =
                    testDb.createStockEntity {
                        symbol = "GOOG"
                    }

                val stock2 =
                    testDb.createStockEntity {
                        symbol = "MSFT"
                    }

                Pair(stock1, stock2)
            }

        describe("findAllBySymbolIn") {
            it("returns empty list for empty input") {
                setup()

                val returnedStocks = stockRepository.findAllBySymbolIn(listOf())

                returnedStocks.shouldHaveSize(0)
            }

            it("returns only existing symbols") {
                val (stock1) = setup()

                val returnedStocks = stockRepository.findAllBySymbolIn(listOf("AAPL.US", "GOOG", "TSL"))

                returnedStocks.shouldHaveSize(1)
                returnedStocks[0].shouldBeEqualToComparingFields(
                    stock1,
                )
            }

            it("requires exact symbol match") {
                val (stock1) = setup()

                val returnedStocks = stockRepository.findAllBySymbolIn(listOf("GOOG", "MSFT.US"))

                returnedStocks.shouldHaveSize(1)
                returnedStocks[0].shouldBeEqualToComparingFields(
                    stock1,
                )
            }

            it("returns multiple existing symbols") {
                val (stock1, stock2) = setup()

                val returnedStocks = stockRepository.findAllBySymbolIn(listOf("GOOG", "MSFT"))

                returnedStocks.shouldHaveSize(2)
                returnedStocks[0].shouldBeEqualToComparingFields(
                    stock1,
                )
                returnedStocks[1].shouldBeEqualToComparingFields(
                    stock2,
                )
            }
        }
    }
}
