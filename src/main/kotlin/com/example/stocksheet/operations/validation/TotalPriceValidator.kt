package com.example.stocksheet.operations.validation

import com.example.stocksheet.operations.dto.OperationRequestDTO
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class TotalPriceValidator : ConstraintValidator<ValidTotalPrice, OperationRequestDTO> {
    override fun isValid(
        dto: OperationRequestDTO,
        context: ConstraintValidatorContext,
    ): Boolean {
        val totalPrice = dto.totalPrice
        val pricePerVolume = dto.pricePerVolume
        val volume = dto.volume

        if (totalPrice == null || volume == null || pricePerVolume == null) {
            return false
        }

        val expectedTotalPrice = volume.multiply(pricePerVolume)

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
