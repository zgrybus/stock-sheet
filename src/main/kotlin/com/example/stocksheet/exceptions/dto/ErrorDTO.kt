package com.example.stocksheet.exceptions.dto

enum class ErrorType {
    SOMETHING_WENT_WRONG,
    NOT_FOUND,
    BAD_REQUEST,
}

data class ErrorDTO(
    val type: ErrorType,
    val message: String,
)

data class ErrorResponse(
    val path: String,
    val status: Int,
    val errors: List<ErrorDTO>,
)
