package com.aiskov.utils

import com.aiskov.domain.common.errors.DomainError
import com.aiskov.domain.common.errors.ErrorCodes.RULE_VIOLATION
import com.aiskov.domain.common.errors.ErrorCodes.VALIDATION_ERROR
import com.aiskov.domain.common.errors.ErrorResponse
import com.aiskov.domain.common.errors.ErrorCodes
import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("Response")

fun Result<*>.toResponse(): Response {
    if (isSuccess) {
        return Response.ok(this.getOrNull()).build()
    }

    val payload = when (val exception = this.exceptionOrNull()) {
        is ConstraintViolationException -> {
            val report = exception.constraintViolations
                .groupBy(
                    { "${it.propertyPath}" },
                    { it.message }
                )

            log.info("Constraint violation:\n${report.entries.map { "${it.key}: ${it.value.joinToString { "," }}" }.joinToString("\n")}")
            ErrorResponse(
                code = VALIDATION_ERROR,
                message = "Validation error",
                payload = report,
            )
        }
        is DomainError -> {
            log.atLevel(exception.level).let {
                val message = "Domain error: ${exception.message ?: "Unknown"}"
                if (exception.logTrace) {
                    it.log(message, exception)
                } else {
                    it.log(message)
                }
            }
            ErrorResponse(
                code = exception.code,
                message = exception.message ?: "Domain error",
                payload = exception.payload(),
            )
        }
        is IllegalArgumentException -> {
            log.warn("Validation error: ${exception.message ?: "Unknown"}")
            ErrorResponse(
                code = RULE_VIOLATION,
                message = exception.message ?: "Validation error",
            )
        }
        else -> {
            log.error("Internal server error: ${exception?.message ?: "Unknown"}", exception)
            ErrorResponse(
                code = ErrorCodes.UNKNOWN_ERROR,
                message = exception?.message ?: "Internal server error"
            )
        }
    }

    return Response.status(payload.code.httpCode)
        .entity(payload)
        .build()
}