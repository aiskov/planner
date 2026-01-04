package com.aiskov.domain.user.port

import com.aiskov.domain.user.User

interface UserCommandRepository {
    fun save(user: User): Result<User>
    fun findById(id: String): Result<User?>
    fun existsById(id: String): Result<Boolean>
}
