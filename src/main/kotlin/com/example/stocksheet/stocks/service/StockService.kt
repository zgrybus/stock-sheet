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
    fun getStock(symbol: String): StockEntity =
        stockRepository.findBySymbol(symbol) ?: stockRepository.save(
            stockMapper.toEntity(StockDTO(name = symbol, symbol = symbol, industry = symbol, exchange = symbol)),
        )
}
