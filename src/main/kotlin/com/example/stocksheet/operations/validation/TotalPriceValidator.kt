package com.example.stocksheet.operations.validation

import com.example.stocksheet.operations.dto.OperationsImportRequestDTO
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.math.RoundingMode

class TotalPriceValidator : ConstraintValidator<ValidTotalPrice, OperationsImportRequestDTO.OperationRequestDTO> {
    override fun isValid(
        dto: OperationsImportRequestDTO.OperationRequestDTO,
        context: ConstraintValidatorContext,
    ): Boolean {
        val totalPrice = dto.totalPrice
        val pricePerVolume = dto.pricePerVolume
        val volume = dto.volume

        val expectedTotalPrice = volume.multiply(pricePerVolume).setScale(2, RoundingMode.HALF_UP)

        val isValid = expectedTotalPrice.compareTo(totalPrice) == 0

        if (isValid) {
            return true
        }

        context
            .buildConstraintViolationWithTemplate(
                context.defaultConstraintMessageTemplate,
            ).addPropertyNode("totalPrice")
            .addConstraintViolation()

        return false
    }
}
