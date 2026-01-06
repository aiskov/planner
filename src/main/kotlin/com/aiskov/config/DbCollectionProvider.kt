package com.aiskov.config

import com.aiskov.domain.user.User
import com.aiskov.utils.handlers.Aggregate
import com.mongodb.kotlin.client.MongoClient
import com.mongodb.kotlin.client.MongoCollection
import com.mongodb.kotlin.client.MongoDatabase
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlin.reflect.KClass
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class DbCollectionProvider {
    private lateinit var client: MongoClient

    @Inject
    @ConfigProperty(name = "app.db.uri")
    private lateinit var uri: String

    @Inject
    @ConfigProperty(name = "app.db.name")
    private lateinit var dbName: String

    private lateinit var collections: Map<String, MongoCollection<out Aggregate<*>>>

    @PostConstruct
    fun init() {
        client = MongoClient.create(uri)

        collections = mapOf(
            User::class.simpleName!! to db.getCollection<User>(User::class.simpleName!!)
        )
    }

    @PreDestroy
    fun closeClient() {
        try {
            if (::client.isInitialized) {
                client.close()
            }
        } catch (_: Throwable) {
            // ignore
        }
    }

    val db: MongoDatabase
        get() = client.getDatabase(dbName)

    @Suppress("UNCHECKED_CAST")
    fun <T : Aggregate<*>> collection(type: KClass<T>): MongoCollection<T> {
        val name = type.simpleName ?: throw IllegalStateException("Type must have a simple name")
        return collections[name] as MongoCollection<T>
    }
}