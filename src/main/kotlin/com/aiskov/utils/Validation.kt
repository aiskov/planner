package com.aiskov.utils

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator

@ApplicationScoped
class ValidationService {
    @Inject
    private lateinit var validator: Validator

    fun validate(input: Any): Result<Unit> {
        return runCatching {
            val violations = validator.validate(input)
            if (violations.isNotEmpty()) {
                val msg = violations.joinToString("; ") { "${it.propertyPath}: ${it.message}" }
                return Result.failure(ConstraintViolationException(msg, violations))
            }
        }
    }
}