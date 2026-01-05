package com.aiskov.domain.common

import com.aiskov.utils.handlers.Command

interface CommandRequest<T : Command> {
    fun toCommand(): T
}