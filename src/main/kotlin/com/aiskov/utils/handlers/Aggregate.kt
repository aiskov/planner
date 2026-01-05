package com.aiskov.utils.handlers

import java.time.Instant

interface Aggregate<T> {
    val id: T
    var deleted: Boolean
    val createdAt: Instant
    var version: Int

    fun delete() {
        deleted = true
    }

    fun incrementVersion() {
        version += 1
    }
}