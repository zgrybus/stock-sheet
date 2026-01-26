package com.example.stocksheet.operations.repository

import com.example.stocksheet.operations.entity.OperationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface OperationRepository :
    JpaRepository<OperationEntity, Long>,
    JpaSpecificationExecutor<OperationEntity> {
    fun findAllByCurrency(currency: String): List<OperationEntity>

    fun findAllByExternalIdIn(externalIds: Collection<String>): MutableList<OperationEntity>
}
