package com.aiskov.utils.handlers

interface Aggregate<T> {
    val id: T
    val version: Int
}