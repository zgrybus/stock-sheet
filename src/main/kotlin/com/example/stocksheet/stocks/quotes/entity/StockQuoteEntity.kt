package com.example.stocksheet.stocks.quotes.entity

import com.example.stocksheet.stocks.entity.StockEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(
    name = "stocks_quotes",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_stock_quote_date_id",
            columnNames = ["date", "stock_id"],
        ),
    ],
)
class StockQuoteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false, updatable = false)
    var closedPrice: BigDecimal,
    @Column(nullable = false, updatable = false)
    var date: LocalDate? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false, updatable = false)
    var stock: StockEntity,
)
