package com.example.stocksheet.scenarios

import com.example.stocksheet.mocks.createMockOperationsList
import com.example.stocksheet.mocks.createMockPortfolioEntity
import com.example.stocksheet.mocks.createMockStockEntity
import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import com.example.stocksheet.stocks.entity.StockEntity
import com.example.stocksheet.stocks.repository.StockRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class StandardMarketScenario(
    private val portfolioRepository: PortfolioRepository,
    private val stockRepository: StockRepository,
    private val operationRepository: OperationRepository,
) {
    data class Setup(
        val portfolios: List<PortfolioEntity>,
        val stocks: List<StockEntity>,
        val operations: List<OperationEntity>,
    )

    fun setup(): Setup {
        val stockGoogle =
            stockRepository.save(
                createMockStockEntity(
                    symbol = "GOOGL.US",
                    name = "Alphabet",
                    price = BigDecimal("100.0126"),
                    dividend = BigDecimal("2.0153"),
                ),
            )
        val stockApple =
            stockRepository.save(
                createMockStockEntity(
                    symbol = "AAPL.US",
                    name = "Apple",
                    price = BigDecimal("75.2500"),
                    dividend = BigDecimal("0.5523"),
                ),
            )

        val portfolioUSD = portfolioRepository.save(createMockPortfolioEntity(name = "Global USD", currency = "USD"))
        val portfolioPLN = portfolioRepository.save(createMockPortfolioEntity(name = "Portfolio PLN", currency = "PLN"))
        val portfolioEUR = portfolioRepository.save(createMockPortfolioEntity(name = "Global EUR Portfolio", currency = "EUR"))

        val operationsList =
            createMockOperationsList().apply {
                this[0].stock = stockApple
                this[0].portfolio = portfolioUSD

                this[1].stock = stockApple
                this[1].portfolio = portfolioUSD

                this[2].stock = stockApple
                this[2].portfolio = portfolioEUR

                this[3].stock = stockGoogle
                this[3].portfolio = portfolioUSD
            }
        val operations = operationRepository.saveAll(operationsList)

        return Setup(
            operations = operations,
            stocks = listOf(stockGoogle, stockApple),
            portfolios = listOf(portfolioUSD, portfolioPLN, portfolioEUR),
        )
    }
}
