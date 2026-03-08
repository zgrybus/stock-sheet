package com.example.stocksheet.portfolio.controller

import com.example.stocksheet.portfolio.dto.PortfolioRequestDTO
import com.example.stocksheet.portfolio.dto.PortfolioResponseDTO
import com.example.stocksheet.portfolio.service.PortfolioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/portfolio")
@Validated
class PortfolioController(
    val portfolioService: PortfolioService,
) {
    @PostMapping(
        consumes = ["application/json"],
        produces = ["application/json"],
    )
    fun createPortfolio(
        @Valid @RequestBody body: PortfolioRequestDTO,
    ): PortfolioResponseDTO = portfolioService.createPortfolio(body)

    @GetMapping(
        value = ["/list"],
        produces = ["application/json"],
    )
    fun getPortfolioList(): List<PortfolioResponseDTO> = portfolioService.getPortfolioList()

    @GetMapping(
        value = ["/{id}"],
        produces = ["application/json"],
    )
    fun getPortfolio(
        @PathVariable(value = "id") id: Long,
    ): PortfolioResponseDTO = portfolioService.getPortfolio(id)

    @DeleteMapping(
        value = ["/{id}"],
        produces = ["application/json"],
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePortfolio(
        @PathVariable(value = "id") id: Long,
    ) = portfolioService.deletePortfolio(id)
}
