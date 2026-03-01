package com.example.stocksheet.portfolio.exceptions

import com.example.stocksheet.Loggable
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.exceptions.dto.ErrorType
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class PortfolioExceptionHandler : Loggable {
    @ExceptionHandler(PortfolioNotFoundException::class)
    fun handlePortfolioNotFound(
        ex: PortfolioNotFoundException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val message = ex.message ?: "Could not find portfolio"

        logger.error { "Resource not found at ${request.getDescription(false)}: $message" }

        val errorDTO = ErrorDTO(type = ErrorType.NOT_FOUND, message = message)
        val response = ErrorResponse(errors = listOf(errorDTO), status = HttpStatus.NOT_FOUND.value(), path = request.getDescription(false))

        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }
}
