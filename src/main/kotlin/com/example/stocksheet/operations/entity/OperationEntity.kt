package com.example.stocksheet.operations.entity

import com.example.stocksheet.portfolio.entity.PortfolioEntity
import com.example.stocksheet.stocks.entity.StockEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "operations")
class OperationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false)
    var externalId: String,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false, updatable = false)
    val stock: StockEntity,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: OperationType,
    @Column(nullable = false)
    var volume: BigDecimal,
    @Column(nullable = false)
    var openDate: Instant,
    @Column(nullable = false)
    var pricePerVolume: BigDecimal,
    @Column(nullable = false)
    var totalPrice: BigDecimal,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false, updatable = false)
    var portfolio: PortfolioEntity,
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,
    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: Instant? = null,
)

enum class OperationType {
    BUY,
    SELL,
}
