package com.aiskov.domain.common

import com.aiskov.config.DbCollectionProvider
import com.aiskov.utils.db.ID
import com.aiskov.utils.db.doc
import com.aiskov.utils.db.findById
import com.aiskov.utils.handlers.Aggregate
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.MongoCollection
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlin.reflect.KClass

@ApplicationScoped
@Suppress("UNCHECKED_CAST")
class CommandRepository {
    @Inject
    private lateinit var db: DbCollectionProvider

    fun <T: Aggregate<*>> create(aggregate: T): Result<T> {
        return runCatching {
            val collection = db.collection(aggregate::class) as MongoCollection<T>
            collection.insertOne(aggregate, InsertOneOptions())

            aggregate
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Aggregate<*>> update(aggregate: T): Result<T> {
        return runCatching {
            val selector = doc(
                ID to aggregate.id,
                Aggregate<*>::version.name to aggregate.version
            )

            val options = ReplaceOptions().upsert(true)
            aggregate.incrementVersion()

            val collection = db.collection(aggregate::class) as MongoCollection<T>
            collection.replaceOne(selector, aggregate, options)

            aggregate
        }
    }

    fun <I : Any, T: Aggregate<I>> findById(type: KClass<T>, id: I): Result<T?> {
        return runCatching {
            db.collection(type).findById(id)
        }
    }
}