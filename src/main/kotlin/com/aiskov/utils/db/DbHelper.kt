package com.aiskov.utils.db

import com.aiskov.domain.user.query.UserListV1Response
import com.aiskov.utils.handlers.Aggregate
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Aggregates.addFields
import com.mongodb.client.model.Aggregates.match
import com.mongodb.client.model.Aggregates.project
import com.mongodb.client.model.Aggregates.sort
import com.mongodb.client.model.Field
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import org.bson.Document
import org.bson.conversions.Bson
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

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


fun conditional(condition: Boolean, block: () -> Bson): Array<Bson> {
    return if (condition) {
        arrayOf(block())
    } else {
        emptyArray()
    }
}

fun matchById(ids: List<String>): Bson {
    return match(
        Filters.`in`(
            ID,
            ids
        )
    )
}

fun matchTermInFields(term: String, vararg fields: String): Bson {
    val pattern = ".*${term.replace("*", ".*").replace(" ", ".*")}.*"
    return match(Filters.or(fields.map { field ->
        Filters.regex(field, pattern, "i")
    }))
}

fun sortByField(field: String, desc: Boolean): Bson {
    return if (desc) {
        sort(Sorts.descending(field))
    } else {
        sort(Sorts.ascending(field))
    }
}

fun copyField(from: KProperty<*>, to: KProperty<*>): Bson {
    return addFields(Field(to.name, "\$${from.name}"))
}

fun copyIdField(to: KProperty<*>): Bson {
    return addFields(Field(to.name, "\$$ID"))
}

fun normalize(): Array<Bson> {
    return arrayOf(
        copyIdField(Aggregate<*>::id),
        addFields(Field(UserListV1Response::email.name, ""))
    )
}

fun dataFor(type: KClass<*>): Bson {
    return project(
        Projections.include(type.memberProperties.map { it.name })
    )
}