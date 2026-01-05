package com.aiskov.domain.common.errors

import org.slf4j.event.Level

abstract class DomainError(
    val level: Level = Level.INFO,
    val logTrace: Boolean = false,
    val code: ErrorCodes = ErrorCodes.VALIDATION_ERROR,
    message: String,
    cause: Throwable?
) : RuntimeException(message, cause) {
    abstract fun payload(): Map<String, Any?>
}