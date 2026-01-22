package com.example.stocksheet.operations.service

import com.example.stocksheet.operations.dto.OperationsBatchRequestDTO
import com.example.stocksheet.operations.repository.OperationRepository
import org.springframework.stereotype.Service

@Service
class OperationService(
    private val operationRepository: OperationRepository,
) {
    fun getOperations(currency: String) {
    }

    fun addOperations(operations: OperationsBatchRequestDTO) {}
}
