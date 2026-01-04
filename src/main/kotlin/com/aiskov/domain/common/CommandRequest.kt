package com.aiskov.domain.common

interface CommandRequest<T : Any> {
    fun toCommand(): T
}