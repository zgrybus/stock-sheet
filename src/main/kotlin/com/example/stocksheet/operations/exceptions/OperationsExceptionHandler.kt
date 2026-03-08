package com.example.stocksheet.operations.exceptions

import com.example.stocksheet.Loggable
import com.example.stocksheet.exceptions.dto.ErrorDTO
import com.example.stocksheet.exceptions.dto.ErrorResponse
import com.example.stocksheet.operations.exceptions.OperationsErrorType
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class OperationsExceptionHandler : Loggable {
    @ExceptionHandler(OperationsBatchEmptyException::class)
    fun handleOperationsBatchEmptyException(
        ex: OperationsBatchEmptyException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.info { "Rejected operations batch request at ${request.getDescription(false)}. Reason: ${ex.message}" }

        val message = ex.message ?: "Could not find new operations"

        val errorDTO = ErrorDTO(type = OperationsErrorType.BATCH_EMPTY_OPERATIONS.name, message = message)
        val response =
            ErrorResponse(
                path = request.getDescription(false),
                status = HttpStatus.BAD_REQUEST.value(),
                errors = listOf(errorDTO),
            )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
}
