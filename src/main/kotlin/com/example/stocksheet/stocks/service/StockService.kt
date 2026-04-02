package com.example.stocksheet.stocks.service

import com.example.stocksheet.stocks.dto.StockDTO
import com.example.stocksheet.stocks.entity.StockEntity
import com.example.stocksheet.stocks.mappers.StockMapper
import com.example.stocksheet.stocks.repository.StockRepository
import org.springframework.stereotype.Service

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val stockMapper: StockMapper,
) {
    fun getOrCreateStocks(symbols: Set<String>): List<StockEntity> {
        val existingStocks = stockRepository.findAllBySymbolIn(symbols)
        val existingStocksSymbols = existingStocks.map { it.symbol }.toSet()

        val (_, missingSymbols) = symbols.partition { existingStocksSymbols.contains(it) }

        if (missingSymbols.isEmpty()) {
            return existingStocks
        }

        val newStocks =
            missingSymbols.map {
                stockMapper.toEntity(StockDTO(name = it, symbol = it, industry = it, exchange = it))
            }

        val savedStocks = stockRepository.saveAll(newStocks)

        return existingStocks.plus(savedStocks)
    }
}
