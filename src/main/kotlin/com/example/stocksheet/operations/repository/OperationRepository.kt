package com.example.stocksheet.operations.repository

import com.example.stocksheet.operations.entity.OperationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.time.LocalDate

interface OperationRepository :
    JpaRepository<OperationEntity, Long>,
    JpaSpecificationExecutor<OperationEntity> {
    interface OperationExternalIdProjection {
        val id: Long
        val externalId: String
    }

    fun findAllByExternalIdIn(externalIds: Collection<String>): List<OperationExternalIdProjection>

    data class PortfolioHoldingProjection(
        val stockSymbol: String,
        val stockName: String,
        val stockPrice: BigDecimal,
        val totalVolume: BigDecimal,
        val totalCost: BigDecimal,
    )

    @Query(
        """
            SELECT
                stock.symbol AS stockSymbol,
                stock.name AS stockName,
                stock.price as stockPrice,
                SUM(operation.volume) AS totalVolume,
                SUM(operation.totalPrice) AS totalCost
            FROM OperationEntity AS operation
            JOIN operation.stock AS stock
            WHERE operation.portfolio.id = :portfolio_id
            GROUP BY
                stock.id,
                stock.name,
                stock.symbol,
                stock.price
            ORDER BY SUM(operation.totalPrice) DESC
        """,
    )
    fun getHoldingsSummaryByPortfolioId(
        @Param("portfolio_id") portfolioId: Long,
    ): List<PortfolioHoldingProjection>

    data class PortfolioSummaryProjection(
        val totalValue: BigDecimal,
        val investedCapital: BigDecimal,
    )

    @Query(
        """
            SELECT 
                COALESCE(SUM(operation.volume * stock.price), 0) AS totalValue,
                COALESCE(SUM(operation.totalPrice), 0) AS investedCapital 
            FROM OperationEntity AS operation
            JOIN operation.stock AS stock
            WHERE operation.portfolio.id = :portfolio_id
        """,
    )
    fun calculatePortfolioSummaryByPortfolioId(
        @Param("portfolio_id") portfolioId: Long,
    ): PortfolioSummaryProjection

    data class ValuationSnapshotProjection(
        val currentValue: BigDecimal,
        val historicalValue: BigDecimal,
    )

    @Query(
        """
            SELECT
                COALESCE(SUM(stock.price * operation.volume), 0) AS currentValue,
                COALESCE(SUM(COALESCE(stockQuote.closedPrice, stock.price) * operation.volume), 0) AS historicalValue
            FROM OperationEntity AS operation
                JOIN operation.stock AS stock
                LEFT JOIN 
                    StockQuoteEntity AS stockQuote 
                    ON stockQuote.stock.id = stock.id AND stockQuote.date = :referenceDate 
            WHERE operation.portfolio.id = :portfolio_id
        """,
    )
    fun calculateValuationSnapshotByPortfolioId(
        @Param("portfolio_id") portfolioId: Long,
        @Param("referenceDate") referenceDate: LocalDate,
    ): ValuationSnapshotProjection
}
