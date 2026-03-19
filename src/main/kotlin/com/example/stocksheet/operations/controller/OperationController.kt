package com.example.stocksheet.operations.controller

import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
import com.example.stocksheet.operations.dto.OperationsImportResponseDTO
import com.example.stocksheet.operations.dto.PortfolioHoldingsDTO
import com.example.stocksheet.operations.service.OperationService
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/operations/{portfolioId}")
@Validated
class OperationController(
    private val operationService: OperationService,
) {
    @GetMapping(
        value = ["/holdings"],
        produces = ["application/json"],
    )
    fun getHoldings(
        @PathVariable portfolioId: Long,
    ): PortfolioHoldingsDTO = operationService.getHoldings(portfolioId)

    @PostMapping(
        value = ["/operations/import"],
        consumes = ["application/json"],
        produces = ["application/json"],
    )
    fun importOperations(
        @Valid @RequestBody body: OperationsImportRequestDTO,
        @PathVariable portfolioId: Long,
    ): OperationsImportResponseDTO = operationService.importOperations(body, portfolioId)
}
