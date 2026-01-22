package com.example.stocksheet.exceptions

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

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
class GlobalExceptionHandler : Loggable {
    @ExceptionHandler(Exception::class)
    fun handleException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.error { "An exception occurred: $ex" }

        val errorDTO = ErrorDTO(type = ErrorType.SOMETHING_WENT_WRONG, message = "An exception occurred")

        val errorResponse =
            ErrorResponse(
                errors = listOf(errorDTO),
                path = request.getDescription(false),
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            )

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
