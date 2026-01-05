package com.aiskov.utils

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class ApiAlias(
    val name: String,
)

val KProperty<*>.alias: String
    get() {
        val apiAlias = this.annotations.filterIsInstance<ApiAlias>().firstOrNull()
        return apiAlias?.name ?: this.name
    }

val KClass<*>.alias: String
    get() {
        val apiAlias = this.annotations.filterIsInstance<ApiAlias>().firstOrNull()
        return apiAlias?.name ?: this.simpleName!!
    }
