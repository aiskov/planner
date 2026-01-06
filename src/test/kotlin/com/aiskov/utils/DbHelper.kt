package com.aiskov.utils

import com.aiskov.config.DbCollectionProvider
import com.aiskov.utils.handlers.Aggregate
import com.mongodb.client.model.InsertOneOptions
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.hamcrest.core.IsEqual
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
fun <T: Aggregate<*>> DbCollectionProvider.ensureExists(entry: T): T {
    return runBlocking {
        collection(entry::class as KClass<T>).insertOne(entry, InsertOneOptions())
        entry
    }
}

fun equalAsString(operand: Any?): Matcher<String?> {
    return IsEqual.equalTo<String?>(operand?.toString())
}