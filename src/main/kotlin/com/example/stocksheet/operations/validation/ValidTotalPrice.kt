package com.example.stocksheet.operations.validation

import jakarta.validation.Constraint

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [TotalPriceValidator::class])
annotation class ValidTotalPrice(
    val message: String,
)
