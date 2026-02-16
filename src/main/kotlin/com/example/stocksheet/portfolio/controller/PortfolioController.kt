package com.example.stocksheet.portfolio.controller

import com.example.stocksheet.portfolio.dto.PortfolioRequestDTO
import com.example.stocksheet.portfolio.dto.PortfolioResponseDTO
import com.example.stocksheet.portfolio.service.PortfolioService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/portfolio")
class PortfolioController(
    val portfolioService: PortfolioService,
) {
    @PostMapping(
        consumes = ["application/json"],
        produces = ["application/json"],
    )
    fun createPortfolio(
        @RequestBody body: PortfolioRequestDTO,
    ): PortfolioResponseDTO = portfolioService.createPortfolio(body)

    @GetMapping(
        value = ["/list"],
        produces = ["application/json"],
    )
    fun getPortfolioList(): List<PortfolioResponseDTO> = portfolioService.getPortfolioList()
}
