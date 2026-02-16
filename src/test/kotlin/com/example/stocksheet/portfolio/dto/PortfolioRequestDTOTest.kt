package com.example.stocksheet.portfolio.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class PortfolioRequestDTOTest : DescribeSpec() {
    init {
        fun getPortfolioRequestDTO(): PortfolioRequestDTO = PortfolioRequestDTO(name = "portfolio_name_1", currency = "USD")

        describe("toEntity") {

            it("should correctly map all fields from DTO to Entity") {
                val dto = getPortfolioRequestDTO()

                dto.toEntity().should {
                    it.name.shouldBe(dto.name)
                    it.currency.shouldBe(dto.currency)
                }
            }
        }
    }
}
