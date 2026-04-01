package com.example.stocksheet.portfolio.entity

import com.example.stocksheet.operations.entity.OperationEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "portfolio")
class PortfolioEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var currency: String,
    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var operations: MutableList<OperationEntity> = mutableListOf(),
)
