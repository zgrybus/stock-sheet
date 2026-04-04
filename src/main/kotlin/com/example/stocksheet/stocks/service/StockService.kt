package com.example.stocksheet.stocks.service

import com.example.stocksheet.Loggable
import com.example.stocksheet.integration.finnhub.dto.FinnhubSymbolLookupResponse
import com.example.stocksheet.integration.finnhub.service.FinnhubService
import com.example.stocksheet.stocks.dto.StockDTO
import com.example.stocksheet.stocks.entity.StockEntity
import com.example.stocksheet.stocks.mappers.StockMapper
import com.example.stocksheet.stocks.repository.StockRepository
import org.springframework.stereotype.Service

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val stockMapper: StockMapper,
    private val finnhubService: FinnhubService,
) : Loggable {
    data class StockIdentifier(
        val symbol: String,
        val exchange: String,
    )

    fun getOrCreateStocks(stocks: List<StockIdentifier>): List<StockEntity> {
        val existingStocks = stockRepository.findAllBySymbolIn(stocks.map { it.symbol })
        val existingStocksSymbols = existingStocks.map { it.symbol }

        val (_, missingStocks) = stocks.partition { existingStocksSymbols.contains(it.symbol) }

        if (missingStocks.isEmpty()) {
            return existingStocks
        }

        val savedStocks = createStocks(missingStocks)

        return existingStocks.plus(savedStocks)
    }

    private fun createStocks(stocks: List<StockIdentifier>): List<StockEntity> {
        val stocksToSave =
            stocks.map { stock ->
                runCatching {
                    val stockDTO = fetchStock(stock)
                    stockMapper.toEntity(stockDTO)
                }.onFailure { exception ->
                    logger.error { "Failed to create stock for symbol: ${stock.symbol}. Error: ${exception.message}" }
                }.getOrDefault(
                    stockMapper.toEntity(
                        StockDTO(
                            symbol = stock.symbol,
                            exchange = stock.exchange,
                            name = stock.symbol,
                            industry = "",
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
        val stockType = finnhubService.getSymbolLookup(stock.symbol, stock.exchange)

        return when (stockType) {
            FinnhubSymbolLookupResponse.FinnhubSymbolLookupType.CommonStock,
            FinnhubSymbolLookupResponse.FinnhubSymbolLookupType.REIT,
            -> {
                val stockProfile = finnhubService.getCompanyProfile2(stock.symbol)
                StockDTO(
                    name = stockProfile?.name ?: stock.symbol,
                    symbol = stockProfile?.ticker ?: stock.symbol,
                    industry = stockProfile?.industry ?: "",
                    exchange = stockProfile?.exchange ?: stock.exchange,
                )
            }

            else -> {
                StockDTO(name = stock.symbol, symbol = stock.symbol, industry = "", exchange = stock.exchange)
            }
        }
    }
}
