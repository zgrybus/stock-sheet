package com.example.stocksheet.portfolio.exceptions

import com.example.stocksheet.Loggable
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
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

        logger.info { "Resource not found at ${request.getDescription(false)}: $message" }

        val errorDTO = ErrorDTO(type = PortfolioErrorType.PORTFOLIO_NOT_FOUND.name, message = message)
        val response = ErrorResponse(errors = listOf(errorDTO), status = HttpStatus.NOT_FOUND.value(), path = request.getDescription(false))

        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(PortfolioNameDuplicatedException::class)
    fun handlePortfolioNameDuplicated(
        ex: PortfolioNameDuplicatedException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val message = ex.message ?: "Portfolio name is duplicated"

        logger.info { "Portfolio name duplicated at: ${request.getDescription(false)}: $message" }

        val errorDTO = ErrorDTO(type = PortfolioErrorType.PORTFOLIO_NAME_DUPLICATED.name, message = message)
        val response =
            ErrorResponse(errors = listOf(errorDTO), status = HttpStatus.BAD_REQUEST.value(), path = request.getDescription(false))

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
}
