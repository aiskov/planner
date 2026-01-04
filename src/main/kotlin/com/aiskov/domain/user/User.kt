package com.aiskov.domain.user

import com.aiskov.domain.user.command.CreateUserCommand
import com.aiskov.utils.handlers.Aggregate
import java.time.Instant

data class User(
    override val id: String,
    var name: String,
    var config: Map<String, Any?>,
    val createdAt: Instant = Instant.now(),
    var deleted: Boolean = false,
    override var version: Int = 1
) : Aggregate<String> {

    companion object {
        fun create(command: CreateUserCommand): Result<User> {
            return runCatching {
                User(
                    id = command.email,
                    name = command.name,
                    config = mapOf()
                )
            }
        }
    }
}