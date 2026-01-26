package com.example.stocksheet.operations.dto

data class OperationImportResponseDTO(
    val added: List<OperationSummaryDTO>,
    val duplicated: List<OperationSummaryDTO>,
) {
    data class OperationSummaryDTO(
        val id: Long,
        val externalId: String,
    )
}
