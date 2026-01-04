package com.aiskov.domain.user.adapter.mongo

import com.aiskov.domain.user.User
import com.aiskov.domain.user.port.UserCommandRepository
import com.mongodb.client.MongoClient
import com.mongodb.client.model.ReplaceOptions
import org.bson.Document
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class UserCommandRepositoryMongo : UserCommandRepository {

    @Inject
    private lateinit var client: MongoClient

    private val dbName = "time-tracker"
    private val collectionName = "users"

    private fun collection() = client.getDatabase(dbName).getCollection(collectionName)

    override fun save(user: User): Result<User> {
        return runCatching {
            val doc = Document("_id", user.id)
                .append("name", user.name)
                .append("passwordHash", user.passwordHash)
                .append("config", Document(user.config))
                .append("isAdmin", user.isAdmin)
                .append("createdAt", user.createdAt.toString())
                .append("deleted", user.deleted)
                .append("version", user.version)

            collection().replaceOne(Document("_id", user.id), doc, ReplaceOptions().upsert(true))
            user
        }
    }

    override fun findById(id: String): Result<User?> {
        return runCatching {
            val doc = collection().find(Document("_id", id)).first() ?: return@runCatching null
            val configMap: Map<String, Any?> = (doc.get("config") as? Document)?.let { d ->
                d.entries.associate { it.key to it.value }
            } ?: mapOf()

            User(
                id = doc.getString("_id"),
                name = doc.getString("name"),
                passwordHash = doc.getString("passwordHash"),
                config = configMap,
                isAdmin = doc.getBoolean("isAdmin", false),
                createdAt = java.time.Instant.parse(doc.getString("createdAt")),
                deleted = doc.getBoolean("deleted", false),
                version = doc.getInteger("version", 1)
            )
        }
    }

    override fun existsById(id: String): Result<Boolean> {
        return runCatching {
            collection().countDocuments(Document("_id", id)) > 0
        }
    }
}
