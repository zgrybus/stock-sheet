package com.example.stocksheet.mocks

import com.example.stocksheet.portfolio.entity.PortfolioEntity

fun createMockPortfolioEntity(
    id: Long? = null,
    name: String = "My Retirement Portfolio",
    currency: String = "USD",
): PortfolioEntity =
    PortfolioEntity(
        id = id,
        name = name,
        currency = currency,
    )
