package com.example.stocksheet.stocks.quotes.repository

import com.example.stocksheet.stocks.quotes.entity.StockQuoteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface StockQuoteRepository :
    JpaRepository<StockQuoteEntity, Long>,
    JpaSpecificationExecutor<StockQuoteEntity>
