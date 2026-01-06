package com.aiskov.config

import com.aiskov.domain.user.User
import com.aiskov.utils.handlers.Aggregate
import com.mongodb.MongoClientSettings
import com.mongodb.event.CommandFailedEvent
import com.mongodb.event.CommandListener
import com.mongodb.event.CommandStartedEvent
import com.mongodb.event.CommandSucceededEvent
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
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
    private val log = LoggerFactory.getLogger(javaClass)

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

        log.info("Connecting to MongoDB at {}", uri)

        val builder = MongoClientSettings.builder()
            .applyConnectionString(com.mongodb.ConnectionString(uri))
            .codecRegistry(registry)

        builder.addCommandListener(object : CommandListener {
            override fun commandStarted(event: CommandStartedEvent) {
                try {
                    log.debug("MongoDB command started: {} -> {}", event.commandName, event.command.toJson())
                } catch (_: Throwable) {
                    log.debug("MongoDB command started: {} (failed to render payload)", event.commandName)
                }
            }

            override fun commandSucceeded(event: CommandSucceededEvent) {
                val elapsedMs = try {
                    event.getElapsedTime(TimeUnit.MILLISECONDS)
                } catch (_: Throwable) {
                    -1L
                }
                log.debug("MongoDB command succeeded: {} (elapsed ms={})", event.commandName, elapsedMs)
            }

            override fun commandFailed(event: CommandFailedEvent) {
                val elapsedMs = try {
                    event.getElapsedTime(TimeUnit.MILLISECONDS)
                } catch (_: Throwable) {
                    -1L
                }
                log.warn("MongoDB command failed: {} (elapsed ms={})", event.commandName, elapsedMs)
            }
        })

        client = MongoClient.create(builder.build())

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