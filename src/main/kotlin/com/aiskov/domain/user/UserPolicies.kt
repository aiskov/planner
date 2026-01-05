package com.aiskov.domain.user

import com.aiskov.domain.common.Policies
import com.aiskov.utils.then
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class UserPolicies : Policies<User> {
    @Inject
    private lateinit var passwordHasher: PasswordHasher

    @Inject
    private lateinit var userRepo: UserQueryRepository

    fun storeHashed(password: String): String {
        return passwordHasher.hash(password)
    }

    fun ensureUniqueEmail(email: String): Result<Unit> {
        return userRepo.existsById(email)
            .then { exists ->
                if (exists) {
                    Result.failure(IllegalArgumentException("User with email $email already exists"))
                } else {
                    Result.success(Unit)
                }
            }
    }
}