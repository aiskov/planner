package com.aiskov.utils

import com.mongodb.client.MongoCollection
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

fun <T> MongoCollection<T>.findById(id: Any): T? {
    return this.find(byId(id)).first()
}