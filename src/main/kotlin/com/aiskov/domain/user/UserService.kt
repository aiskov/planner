package com.aiskov.domain.user

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
}