package com.example.stocksheet.operations.controller

import com.example.stocksheet.operations.dto.OperationImportResponseDTO
import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.dto.PortfolioSummaryDTO
import com.example.stocksheet.operations.service.OperationService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/operations")
@Validated
class OperationController(
    private val operationService: OperationService,
) {
    @GetMapping(
        value = ["/portfolio/{currency}"],
        produces = ["application/json"],
    )
    fun getPortfolioSummary(
        @PathVariable currency: String,
    ): PortfolioSummaryDTO = operationService.getPortfolioSummary(currency)

    @PostMapping(
        value = ["/import/{currency}"],
        consumes = ["application/json"],
        produces = ["application/json"],
    )
    fun addOperations(
        @RequestBody operations: OperationsBatchRequestDTO,
        @PathVariable currency: String,
    ): OperationImportResponseDTO = operationService.addOperations(operations, currency)
}
