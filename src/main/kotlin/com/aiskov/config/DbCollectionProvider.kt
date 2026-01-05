package com.aiskov.config

import com.aiskov.utils.handlers.Aggregate
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlin.reflect.KClass

@ApplicationScoped
class DbCollectionProvider {
    @Inject
    private lateinit var client: MongoClient

    private val dbName = "time-tracker"
    fun <T : Aggregate<*>> collection(type: KClass<T>): MongoCollection<T> {
        val name = type.simpleName ?: throw IllegalArgumentException("Type must have a simple name")
        return client.getDatabase(dbName).getCollection<T>(name, type.java)
    }

}