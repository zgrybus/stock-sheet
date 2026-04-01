package com.example.stocksheet.operations.repository

import com.example.stocksheet.BaseRepositoryTest
import com.example.stocksheet.scenarios.StandardMarketScenario
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

class OperationRepositoryTest : BaseRepositoryTest() {
    @Autowired lateinit var operationRepository: OperationRepository

    @Autowired lateinit var standardMarketScenario: StandardMarketScenario

    init {
        describe("calculateInvestedCapitalByPortfolioId") {
            it("returns zero when portfolio has no assigned operations") {
                val data = standardMarketScenario.setup()
                entityManager.flush()
                entityManager.clear()

                val portfolioPLN = data.portfolios[1]

                val investedCapital = operationRepository.calculateInvestedCapitalByPortfolioId(portfolioPLN.id!!)

                investedCapital.shouldBe(BigDecimal.ZERO)
            }

            it("returns sum of total price for all operations within specified portfolio") {
                val data = standardMarketScenario.setup()
                entityManager.flush()
                entityManager.clear()

                val portfolioUSD = data.portfolios[0]

                val investedCapital = operationRepository.calculateInvestedCapitalByPortfolioId(portfolioUSD.id!!)

                investedCapital.shouldBeEqualComparingTo(BigDecimal("2400.00"))
            }
        }
    }
}
