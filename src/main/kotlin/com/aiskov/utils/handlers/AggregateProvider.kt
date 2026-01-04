package com.aiskov.utils.handlers

interface AggregateProvider<A: Aggregate<*>> {
    fun getById(): A?
}