package com.example.stocksheet.stocks.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

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
    @Column(nullable = false)
    var industry: String,
)
