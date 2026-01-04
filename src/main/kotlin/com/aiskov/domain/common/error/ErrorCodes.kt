package com.aiskov.domain.common.error

import jakarta.ws.rs.core.Response

enum class ErrorCodes(
    val httpCode: Response.Status,
) {
    UNKNOWN_ERROR(Response.Status.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(Response.Status.BAD_REQUEST),
    RULE_VIOLATION(Response.Status.BAD_REQUEST),
    NOT_FOUND(Response.Status.NOT_FOUND),
    CONFLICT(Response.Status.CONFLICT),
    ACCESS_DENIED(Response.Status.FORBIDDEN),
}