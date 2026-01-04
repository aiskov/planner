package com.aiskov.domain.common

data class CommandResponse<T>(
    val id: T,
    val version: Int,
)