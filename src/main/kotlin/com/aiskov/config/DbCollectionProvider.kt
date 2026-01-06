package com.aiskov.config

import com.aiskov.domain.user.User
import com.aiskov.utils.handlers.Aggregate
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.eclipse.microprofile.config.inject.ConfigProperty
import kotlin.reflect.KClass

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
        val registry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
        )

        client = MongoClient.create(
            MongoClientSettings.builder()
                .applyConnectionString(
                    com.mongodb.ConnectionString(uri)
                )
                .codecRegistry(registry)
                .build()
        )

        collections = mapOf(
            User::class.simpleName!! to db.getCollection(User::class.simpleName!!, User::class.java)
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