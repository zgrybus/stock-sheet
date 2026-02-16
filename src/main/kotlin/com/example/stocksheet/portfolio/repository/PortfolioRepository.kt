package com.example.stocksheet.portfolio.repository

import com.example.stocksheet.portfolio.entity.PortfolioEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface PortfolioRepository :
    JpaRepository<PortfolioEntity, Long>,
    JpaSpecificationExecutor<PortfolioEntity>
