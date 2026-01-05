package com.aiskov.domain.user

import com.aiskov.config.DbCollectionProvider
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.bson.Document

@ApplicationScoped
class UserQueryRepository {
    @Inject
    private lateinit var db: DbCollectionProvider

    fun existsById(email: String): Result<Boolean> {
        return runCatching {
            val collection = db.collection(User::class)
            collection.countDocuments(Document("_id", email)) != 0L
        }
    }
}