package com.aiskov.domain.user

import com.aiskov.config.DbCollectionProvider
import com.aiskov.domain.user.query.UserListV1Response
import com.aiskov.utils.db.ID
import com.aiskov.utils.db.byId
import com.aiskov.utils.db.conditional
import com.aiskov.utils.db.copyIdField
import com.aiskov.utils.db.dataFor
import com.aiskov.utils.db.matchById
import com.aiskov.utils.db.matchNotDeleted
import com.aiskov.utils.db.matchTermInFields
import com.aiskov.utils.db.normalize
import com.aiskov.utils.db.sortByField
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlinx.coroutines.flow.toList
import kotlin.reflect.KClass

data class UserFilter(
    val ids: List<String> = emptyList(),
    val search: String? = null,
    val sort: String = UserListV1Response::email.name,
    val desc: Boolean = false,
)

@ApplicationScoped
class UserQueryRepository {
    @Inject
    private lateinit var db: DbCollectionProvider

    suspend fun existsById(email: String): Result<Boolean> {
        return runCatching {
            val collection = db.collection(User::class)
            collection.countDocuments(byId(email)) != 0L
        }
    }

    suspend fun <T : Any> findByFilter(type: KClass<T>, filter: UserFilter): Result<List<T>> {
        return runCatching {
            val collection = db.collection(User::class)
            collection.aggregate(
                pipeline = listOf(
                    matchNotDeleted(),

                    *conditional(filter.ids.isNotEmpty()) {
                        matchById(filter.ids)
                    },

                    *conditional(filter.search.isNullOrBlank().not()) {
                        matchTermInFields(filter.search!!, User::name.name, ID)
                    },

                    copyIdField(UserListV1Response::email),

                    *normalize(),
                    dataFor(type),

                    sortByField(filter.sort, filter.desc)
                ),
                resultClass = type.java
            ).toList()
        }
    }
}