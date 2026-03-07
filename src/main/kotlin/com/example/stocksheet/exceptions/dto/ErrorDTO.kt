package com.example.stocksheet.exceptions.dto

data class ErrorDTO(
    val type: String,
    val message: String,
)

data class ErrorResponse(
    val path: String,
    val status: Int,
    val errors: List<ErrorDTO>,
)
