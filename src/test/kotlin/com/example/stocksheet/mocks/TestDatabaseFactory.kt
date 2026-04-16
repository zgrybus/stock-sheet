package com.example.stocksheet.mocks

import com.example.stocksheet.operations.entity.OperationEntity
import com.example.stocksheet.operations.repository.OperationRepository
import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.portfolio.repository.PortfolioRepository
import com.example.stocksheet.stocks.entity.StockEntity
import com.example.stocksheet.stocks.quotes.entity.StockQuoteEntity
import com.example.stocksheet.stocks.quotes.repository.StockQuoteRepository
import com.example.stocksheet.stocks.repository.StockRepository
import org.springframework.stereotype.Component

@Component
class TestDatabaseFactory(
    private val operationRepository: OperationRepository,
    private val stockRepository: StockRepository,
    private val stockQuoteRepository: StockQuoteRepository,
    private val portfolioRepository: PortfolioRepository,
) {
    fun createStockEntity(modifier: StockEntity.() -> Unit = {}): StockEntity {
        val stock = createMockStockEntity().apply(modifier)
        return stockRepository.save(stock)
    }

    fun createStockQuoteEntity(modifier: StockQuoteEntity.() -> Unit = {}): StockQuoteEntity {
        val stockQuote = createMockStockQuoteEntityMock().apply(modifier)
        return stockQuoteRepository.save(stockQuote)
    }

    fun createOperationEntity(modifier: OperationEntity.() -> Unit = {}): OperationEntity {
        val operation = createMockOperationEntity().apply(modifier)

        operation.totalPrice = operation.volume.multiply(operation.pricePerVolume)

        return operationRepository.save(operation)
    }

    fun createPortfolioEntity(modifier: PortfolioEntity.() -> Unit = {}): PortfolioEntity {
        val portfolio = createMockPortfolioEntity().apply(modifier)
        return portfolioRepository.save(portfolio)
    }
}
