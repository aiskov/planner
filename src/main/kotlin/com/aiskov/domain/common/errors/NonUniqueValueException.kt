package com.aiskov.domain.common.errors

import com.aiskov.utils.alias
import com.aiskov.utils.handlers.Aggregate
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class NonUniqueValueException(
    val aggregateType: KClass<out Aggregate<*>>,
    val field: KProperty<*>,
    val fieldValue: Any,
    cause: Throwable? = null
) : DomainError(
    message = "Non unique value ${aggregateType.simpleName}.${field.name} - $fieldValue",
    cause = cause
) {
    override fun payload(): Map<String, Any?> {
        return mapOf(
            "aggregateType" to aggregateType.alias,
            "field" to field.alias,
            "fieldValue" to fieldValue
        )
    }
}

