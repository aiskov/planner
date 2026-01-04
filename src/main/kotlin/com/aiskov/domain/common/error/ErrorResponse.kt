package com.aiskov.domain.common.error

data class ErrorResponse(
    val code: ErrorCodes,
    val message: String,
    val payload: Map<String, Any?> = mapOf(),
)