package com.example.stocksheet.stocks.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class StockEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    val symbol: String,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val exchange: String,
    @Column(nullable = false)
    val industry: String,
)
