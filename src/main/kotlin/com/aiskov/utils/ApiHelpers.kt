package com.aiskov.utils

import com.aiskov.domain.common.error.ErrorCodes.RULE_VIOLATION
import com.aiskov.domain.common.error.ErrorCodes.VALIDATION_ERROR
import com.aiskov.domain.common.error.ErrorResponse
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
                code = com.aiskov.domain.common.error.ErrorCodes.UNKNOWN_ERROR,
                message = exception?.message ?: "Internal server error"
            )
        }
    }

    return Response.status(payload.code.httpCode)
        .entity(payload)
        .build()
}