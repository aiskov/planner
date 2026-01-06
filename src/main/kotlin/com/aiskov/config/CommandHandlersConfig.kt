package com.aiskov.config

import com.aiskov.domain.common.Policies
import com.aiskov.domain.user.User
import com.aiskov.domain.user.UserPolicies
import com.aiskov.domain.user.command.CreateUserV1Command
import com.aiskov.utils.handlers.Aggregate
import com.aiskov.utils.handlers.Command
import com.aiskov.utils.handlers.CommandHandler
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlin.reflect.KClass

@ApplicationScoped
class CommandHandlersConfig {
    @Inject
    private lateinit var userPolicies: UserPolicies

    private val handlers: Map<String, CommandHandler<*, *, *>> = listOf<CommandHandler<*, *, *>>(
        CommandHandler.CreateCommandHandler(
            commandType = CreateUserV1Command::class,
            aggregateType = User::class,
            operation = { cmd: CreateUserV1Command, policies: UserPolicies ->
                User.create(cmd, policies)
            }
        )
    ).associateBy { it.commandType.simpleName!! }

    @Suppress("UNCHECKED_CAST")
    fun <A: Aggregate<*>> policiesOf(aggregateType: KClass<A>): Policies<A> {
        return when (aggregateType.simpleName) {
            User::class.simpleName!! -> userPolicies as Policies<A>
            else -> error("Unknown aggregate type for policies ${aggregateType.simpleName}")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <C: Command> handlerOf(commandType: KClass<C>): CommandHandler<C, *, *> {
        return handlers[commandType.simpleName] as? CommandHandler<C, *, *>
            ?: error("Unknown command type for handler ${commandType.simpleName}")
    }
}