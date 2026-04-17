package com.example.stocksheet.stocks.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.integration.fmp.service.FmpService
import com.example.stocksheet.stocks.dto.StockDTO
import com.example.stocksheet.stocks.entity.DividendFrequency
import com.example.stocksheet.stocks.entity.StockEntity
import com.example.stocksheet.stocks.mappers.StockMapper
import com.example.stocksheet.stocks.quotes.service.StockQuoteService
import com.example.stocksheet.stocks.repository.StockRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val stockMapper: StockMapper,
    private val fmpService: FmpService,
    private val stockQuoteService: StockQuoteService,
) : Loggable {
    data class StockIdentifier(
        val symbol: String,
    )

    fun getOrCreateStocks(stocks: List<StockIdentifier>): List<StockEntity> {
        val existingStocks = stockRepository.findAllBySymbolIn(stocks.map { it.symbol })
        val existingStocksSymbols = existingStocks.map { it.symbol }

        val (_, missingStocks) = stocks.partition { existingStocksSymbols.contains(it.symbol) }

        if (missingStocks.isEmpty()) {
            return existingStocks
        }

        val savedStocks = createStocks(missingStocks)

        val stockQuotes =
            savedStocks.map { stock ->
                StockQuoteService.StockQuote(symbol = stock.symbol, price = stock.price)
            }
        stockQuoteService.upsertStockQuotes(stockQuotes)

        return existingStocks.plus(savedStocks)
    }

    private fun createStocks(stocks: List<StockIdentifier>): List<StockEntity> {
        val stocksToSave =
            stocks.map { stock ->
                runCatching {
                    val stockDTO = fetchStock(stock)
                    stockMapper.toEntity(stockDTO)
                }.onFailure { exception ->
                    logger.error { "Failed to create stock for symbol: $stock.symbol. Error: $exception" }
                }.getOrDefault(
                    stockMapper.toEntity(
                        StockDTO(
                            symbol = stock.symbol,
                            exchange = "",
                            name = stock.symbol,
                            industry = "",
                            price = BigDecimal.ZERO,
                            dividend = BigDecimal.ZERO,
                            dividendFrequency = DividendFrequency.NONE,
                        ),
                    ),
                )
            }

        return if (stocksToSave.isEmpty()) {
            emptyList()
        } else {
            stockRepository.saveAll(stocksToSave)
        }
    }

    private fun fetchStock(stock: StockIdentifier): StockDTO {
        val stockProfile = fmpService.getCompanyProfile(stock.symbol)

        return StockDTO(
            name = stockProfile?.name ?: stock.symbol,
            symbol = stockProfile?.symbol ?: stock.symbol,
            industry = stockProfile?.industry ?: "",
            exchange = stockProfile?.exchange ?: "",
            price = stockProfile?.price ?: BigDecimal.ZERO,
            dividend = stockProfile?.lastDividend ?: BigDecimal.ZERO,
            dividendFrequency = DividendFrequency.NONE,
        )
    }
}
