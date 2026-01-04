package com.aiskov.utils.handlers

import kotlin.reflect.KClass

sealed class CommandHandler<C : Any>(
    val type: KClass<C>,
) {
    class CreateCommandHandler<C : Any, A: Aggregate<*>>(
        type: KClass<C>,
        val operation: (C) -> Result<A>
    ) : CommandHandler<C>(type)

    class ModifyCommandHandler<C : Any, A: Aggregate<*>>(
        type: KClass<C>,
        val operation: (C, A) -> Result<A>
    ) : CommandHandler<C>(type)
}