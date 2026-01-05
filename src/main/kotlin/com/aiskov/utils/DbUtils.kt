package com.aiskov.utils

import org.bson.Document

const val ID = "_id"

fun doc(vararg fields: Pair<String, Any?>): Document {
    return Document(mapOf(*fields))
}

fun byId(id: Any): Document {
    return Document(ID, id)
}