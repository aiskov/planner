package com.aiskov.config

import com.aiskov.utils.handlers.Aggregate
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlin.reflect.KClass
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class DbCollectionProvider {
    @Inject
    private lateinit var client: MongoClient

    @Inject
    @ConfigProperty(name = "app.db.name")
    private lateinit var dbName: String

    fun <T : Aggregate<*>> collection(type: KClass<T>): MongoCollection<T> {
        val name = type.simpleName ?: throw IllegalArgumentException("Type must have a simple name")
        return client.getDatabase(dbName).getCollection<T>(name, type.java)
    }

}