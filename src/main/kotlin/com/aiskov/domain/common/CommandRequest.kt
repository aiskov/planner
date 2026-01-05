package com.aiskov.domain.common

import com.aiskov.config.JSON
import com.aiskov.utils.handlers.Command
import kotlin.reflect.KClass

interface CommandRequest<T : Command> {
    fun toCommand(): T

    fun toCommandString(type: KClass<T>): String {
        return "${type.simpleName} - ${JSON.writeValueAsString(this)}"
    }
}