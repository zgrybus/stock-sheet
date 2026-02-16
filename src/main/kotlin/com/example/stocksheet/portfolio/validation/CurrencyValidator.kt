package com.example.stocksheet.portfolio.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.util.Currency

class CurrencyValidator : ConstraintValidator<ValidCurrency, String?> {
    override fun isValid(
        currency: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (currency == null) {
            return true
        }

        try {
            Currency.getInstance(currency)
            return true
        } catch (e: IllegalArgumentException) {
            return false
        }
    }
}
