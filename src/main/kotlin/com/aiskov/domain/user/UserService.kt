package com.aiskov.domain.user

import com.aiskov.domain.user.query.UserDetailsV1Query
import com.aiskov.domain.user.query.UserDetailsV1Request
import com.aiskov.domain.user.query.UserDetailsV1Resource
import com.aiskov.domain.user.query.UserDetailsV1Response
import com.aiskov.domain.user.query.UserListV1Request
import com.aiskov.domain.user.query.UserListV1Response
import com.aiskov.utils.ValidationService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class UserService {
    @Inject
    private lateinit var repository: UserQueryRepository

    @Inject
    private lateinit var validation: ValidationService

    suspend fun findAllV1(request: UserListV1Request): Result<List<UserListV1Response>> {
        validation.validate(request).onFailure { return Result.failure(it) }
        val query = request.toQuery()

        return repository.findByFilter(
            type = UserListV1Response::class,
            filter = UserFilter(
                search = query.search,
                sort = query.sort,
                desc = query.desc
            )
        )
    }

    suspend fun findDetailsV1(request: UserDetailsV1Request): Result<UserDetailsV1Response?> {
        validation.validate(request).onFailure { return Result.failure(it) }
        val query = request.toQuery()

        return repository.findByFilter(
            type = UserDetailsV1Response::class,
            filter = UserFilter(
                ids = listOf(query.id)
            )
        ).map { it.firstOrNull() }
    }
}