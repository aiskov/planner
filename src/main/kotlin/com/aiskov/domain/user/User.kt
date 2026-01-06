package com.aiskov.domain.user

import com.aiskov.domain.user.command.CreateUserV1Command
import com.aiskov.utils.ApiAlias
import com.aiskov.utils.handlers.Aggregate
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import java.time.Instant

data class User(
    @param:BsonId
    @property:ApiAlias("email")
    override val id: String,

    var name: String,
    var password: String,
    var config: Document = Document(),
    var isAdmin: Boolean = false,

    override val createdAt: Instant = Instant.now(),
    override var deleted: Boolean = false,
    override var version: Int = 1
) : Aggregate<String> {

    companion object {
        fun create(
            command: CreateUserV1Command,
            policies: UserPolicies,
        ): Result<User> {
            policies.ensureUniqueEmail(command.email)
                .getOrElse { return Result.failure(it) }

            return runCatching {
                User(
                    id = command.email,
                    name = command.name,
                    password = policies.storeHashed(command.password),
                    isAdmin = false,
                )
            }
        }
    }
}