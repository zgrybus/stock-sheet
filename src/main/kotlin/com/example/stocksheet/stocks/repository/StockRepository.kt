package com.example.stocksheet.stocks.repository

import com.example.stocksheet.stocks.entity.StockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface StockRepository :
    JpaRepository<StockEntity, Long>,
    JpaSpecificationExecutor<StockEntity> {
    fun findBySymbol(symbol: String): StockEntity?
}
