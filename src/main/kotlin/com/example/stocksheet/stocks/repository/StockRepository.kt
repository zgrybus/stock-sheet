package com.example.stocksheet.stocks.repository

import com.example.stocksheet.stocks.entity.StockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StockRepository :
    JpaRepository<StockEntity, Long>,
    JpaSpecificationExecutor<StockEntity> {
    @Query(
        """
            SELECT 
                stock
            FROM StockEntity stock
            WHERE stock.symbol IN :symbols
        """,
    )
    fun findAllBySymbolIn(
        @Param("symbols") symbols: Set<String>,
    ): List<StockEntity>
}
