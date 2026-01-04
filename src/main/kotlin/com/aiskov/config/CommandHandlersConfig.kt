package com.aiskov.config

import com.aiskov.domain.user.PasswordHasher
import com.aiskov.domain.user.User
import com.aiskov.domain.user.command.CreateUserCommand
import com.aiskov.domain.user.port.UserCommandRepository
import com.aiskov.utils.handlers.CommandHandler
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class CommandHandlersConfig {
    @Inject
    private lateinit var userRepo: UserCommandRepository

    @Inject
    private lateinit var passwordHasher: PasswordHasher

    val handlers: Map<String, CommandHandler<*>>
        get() = listOf(
            CommandHandler.CreateCommandHandler(
                type = CreateUserCommand::class,
                operation = { cmd ->
                    // check duplicate
                    val existsResult = userRepo.existsById(cmd.email)
                    if (existsResult.isFailure) {
                        return@CreateCommandHandler Result.failure(existsResult.exceptionOrNull()!!)
                    }
                    val exists = existsResult.getOrNull() ?: false
                    if (exists) {
                        return@CreateCommandHandler Result.failure(IllegalArgumentException("User already exists: ${cmd.email}"))
                    }

                    val hash = passwordHasher.hash(cmd.password)
                    val userResult = User.create(cmd, hash)
                    if (userResult.isFailure) {
                        return@CreateCommandHandler Result.failure(userResult.exceptionOrNull()!!)
                    }
                    val user = userResult.getOrNull()!!

                    val savedResult = userRepo.save(user)
                    if (savedResult.isFailure) {
                        return@CreateCommandHandler Result.failure(savedResult.exceptionOrNull()!!)
                    }
                    Result.success(savedResult.getOrNull()!!)
                }
            )
        ).associateBy { it.type.qualifiedName!! }
}