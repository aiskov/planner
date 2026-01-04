package com.aiskov.config

import com.aiskov.domain.user.User
import com.aiskov.domain.user.command.CreateUserCommand
import com.aiskov.utils.handlers.CommandHandler
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CommandHandlersConfig {
    val handlers: Map<String, CommandHandler<*>> = listOf(
        CommandHandler.CreateCommandHandler(
            type = CreateUserCommand::class,
            operation = { User.create(it) }
        )
    ).associateBy { it.type.qualifiedName!! }
}