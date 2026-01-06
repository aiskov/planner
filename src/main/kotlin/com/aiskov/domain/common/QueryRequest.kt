package com.aiskov.domain.common

interface QueryRequest<T : Any> {
    val search: String?
    val sort: String?
    val desc: Boolean?

    fun toQuery(): T
}