package com.aiskov.utils.handlers

import com.aiskov.domain.common.Policies
import kotlin.reflect.KClass

sealed interface Command {
    interface CreateCommand : Command
    interface ModifyCommand : Command {
        val version: Int
    }
}

sealed interface CommandHandler<C: Command, A: Aggregate<*>, P: Policies<A>> {
    val commandType: KClass<C>
    val aggregateType: KClass<A>

    class CreateCommandHandler<C: Command.CreateCommand, A: Aggregate<*>, P: Policies<A>>(
        override val commandType: KClass<C>,
        override val aggregateType: KClass<A>,
        val operation: (C, P) -> Result<A>,
    ) : CommandHandler<C, A, P>

    class ModifyCommandHandler<C: Command.ModifyCommand, A: Aggregate<*>, P: Policies<A>>(
        override val commandType: KClass<C>,
        override val aggregateType: KClass<A>,
        val operation: (C, A, P) -> Result<A>,
    ) : CommandHandler<C, A, P> {
    }
}