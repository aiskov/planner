package com.aiskov.domain.user

import com.aiskov.domain.user.command.CreateUserCommand
import com.aiskov.utils.handlers.Aggregate
import java.time.Instant

data class User(
    override val id: String,
    var name: String,
    var passwordHash: String,
    var config: Map<String, Any?>,
    var isAdmin: Boolean = false,
    val createdAt: Instant = Instant.now(),
    var deleted: Boolean = false,
    override var version: Int = 1
) : Aggregate<String> {

    companion object {
        fun create(command: CreateUserCommand, passwordHash: String): Result<User> {
            return runCatching {
                User(
                    id = command.email,
                    name = command.name,
                    passwordHash = passwordHash,
                    config = mapOf(),
                    isAdmin = false,
                )
            }
        }
    }
}