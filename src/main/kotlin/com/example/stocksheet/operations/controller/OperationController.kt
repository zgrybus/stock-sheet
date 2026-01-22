package com.example.stocksheet.operations.controller

import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.dto.PortfolioSummaryDTO
import com.example.stocksheet.operations.service.OperationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/api/operations")
class OperationController(
    private val operationService: OperationService,
) {
    @GetMapping("/portfolio")
    fun getPortfolioSummary(currency: String): PortfolioSummaryDTO = operationService.getPortfolioSummary(currency)

    @PostMapping
    fun addOperations(
        @RequestBody operations: OperationsBatchRequestDTO,
    ) = operationService.addOperations(operations)
}
