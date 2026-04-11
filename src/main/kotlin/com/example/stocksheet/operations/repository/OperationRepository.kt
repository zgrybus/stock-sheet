package com.example.stocksheet.operations.repository

import com.example.stocksheet.operations.entity.OperationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface OperationRepository :
    JpaRepository<OperationEntity, Long>,
    JpaSpecificationExecutor<OperationEntity> {
    interface OperationExternalIdProjection {
        val id: Long
        val externalId: String
    }

    fun findAllByExternalIdIn(externalIds: Collection<String>): List<OperationExternalIdProjection>

    @Query(
        """
        SELECT COALESCE(SUM(operation.totalPrice), 0) 
        FROM OperationEntity operation
        WHERE operation.portfolio.id = :portfolio_id
    """,
    )
    fun calculateInvestedCapitalByPortfolioId(
        @Param("portfolio_id") portfolioId: Long,
    ): BigDecimal

    data class PortfolioHoldingProjection(
        val stockSymbol: String,
        val stockName: String,
        val totalVolume: BigDecimal,
        val totalCost: BigDecimal,
    )

    @Query(
        """
            SELECT
                stock.symbol AS stockSymbol,
                stock.name AS stockName,
                SUM(operation.volume) AS totalVolume,
                SUM(operation.totalPrice) AS totalCost
            FROM OperationEntity operation
            JOIN operation.stock stock
            WHERE operation.portfolio.id = :portfolio_id
            GROUP BY
                stock.id,
                stock.name,
                stock.symbol
            ORDER BY SUM(operation.totalPrice) DESC
        """,
    )
    fun getHoldingsSummaryByPortfolioId(
        @Param("portfolio_id") portfolioId: Long,
    ): List<PortfolioHoldingProjection>
}
