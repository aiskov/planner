package com.aiskov.domain.common

interface QueryDetailsRequest<T : Any> {
    val id: String

    fun toQuery(): T
}