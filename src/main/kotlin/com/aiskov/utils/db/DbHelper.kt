package com.aiskov.utils.db

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import org.bson.Document

const val ID = "_id"

fun doc(vararg fields: Pair<String, Any?>): Document {
    return Document(mapOf(*fields))
}

fun byId(id: Any): Document {
    return Document(ID, id)
}

fun any(): Document {
    return Document()
}

suspend fun <T : Any> MongoCollection<T>.findById(id: Any): T? {
    return this.find(byId(id)).firstOrNull()
}