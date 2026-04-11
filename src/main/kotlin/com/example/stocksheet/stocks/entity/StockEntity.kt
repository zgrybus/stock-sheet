package com.example.stocksheet.stocks.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "stocks")
class StockEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false, unique = true)
    var symbol: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var exchange: String,
    @Column(nullable = false, scale = 4, precision = 19)
    var dividend: BigDecimal,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var dividendFrequency: DividendFrequency,
    @Column(nullable = false, scale = 4, precision = 19)
    var price: BigDecimal,
    @Column(nullable = false)
    var industry: String,
)

enum class DividendFrequency {
    MONTHLY,
    QUARTERLY,
    SEMI_ANNUALLY,
    ANNUALLY,
    NONE,
}
