package com.example.stocksheet.stocks.quotes.repository

import com.example.stocksheet.stocks.quotes.entity.StockQuoteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface StockQuoteRepository :
    JpaRepository<StockQuoteEntity, Long>,
    JpaSpecificationExecutor<StockQuoteEntity> {
    @Query(
        """
            SELECT
                stockQuoute
            FROM StockQuoteEntity AS stockQuoute
            JOIN FETCH stockQuoute.stock AS stock
            WHERE 
                stock.symbol IN :symbols AND stockQuoute.date = :targetDate
        """,
    )
    fun findExistingQuotesBySymbolsAndDate(
        @Param("symbols") symbols: List<String>,
        @Param("targetDate") date: LocalDate,
    ): List<StockQuoteEntity>
}
