@file:Suppress("UNCHECKED_CAST")

package com.aiskov.utils

fun <I, O> Result<I>.then(step: (I) -> Result<O>): Result<O> {
    if (isFailure) return this as Result<O>
    return runCatching {
        return step(getOrNull() as I)
    }
}

fun <T> Result<T>.check(step: (T) -> Result<Unit>): Result<T> {
    if (isFailure) return this

    return runCatching {
        val result = step(getOrNull() as T)
        if (result.isFailure) return result as Result<T>
        return this
    }
}

fun Result.Companion.silentSuccess(): Result<Unit> {
    return Result.success(Unit)
}