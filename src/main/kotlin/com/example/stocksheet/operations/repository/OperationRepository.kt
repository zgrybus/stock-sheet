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
    fun findAllByPortfolioId(portfolioId: Long): List<OperationEntity>

    fun findAllByExternalIdIn(externalIds: Collection<String>): List<OperationEntity>

    @Query(
        """
        SELECT COALESCE(SUM(operation.totalPrice), 0) FROM OperationEntity operation
        WHERE operation.portfolio.id = :portfolio_id
    """,
    )
    fun calculateInvestedCapitalByPortfolioId(
        @Param("portfolio_id") portfolioId: Long,
    ): BigDecimal
}
