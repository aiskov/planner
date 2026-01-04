package com.aiskov.utils.handlers

import com.aiskov.config.CommandHandlersConfig
import com.aiskov.domain.common.CommandRequest
import com.aiskov.domain.common.CommandResponse
import com.aiskov.utils.handlers.CommandHandler.CreateCommandHandler
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import org.slf4j.LoggerFactory

@ApplicationScoped
class CommandGateway {
    val log = LoggerFactory.getLogger(javaClass)

    @Inject
    private lateinit var config: CommandHandlersConfig

    @Inject
    private lateinit var validator: Validator

    @Suppress("UNCHECKED_CAST")
    fun process(
        request: CommandRequest<*>,
    ): Result<CommandResponse<*>> {
        return runCatching {
            log.info("Processing command request: $request")

            val violations = validator.validate(request)
            if (violations.isNotEmpty()) {
                val msg = violations.joinToString("; ") { "${it.propertyPath}: ${it.message}" }
                log.warn("Validation failed for request: $msg")
                return Result.failure(ConstraintViolationException(msg, violations))
            }

            val command = request.toCommand()
            val type = command::class.qualifiedName

            val handler = config.handlers[type]
                ?: return Result.failure(
                    IllegalArgumentException("No handler found for command type: $type")
                )

            val result: Result<Aggregate<*>> = if (handler is CreateCommandHandler<*, *>) {
                (handler as CreateCommandHandler<Any, *>).operation(command)
            } else {
                // TODO: Process non create operations
                return Result.failure(
                    IllegalArgumentException("Unsupported command handler type: ${handler::class.qualifiedName}")
                )
            }

            return result.mapCatching {
                // TODO: Save aggregate in DB
                CommandResponse(
                    id = it.id as Any,
                    version = it.version,
                )
            }
        }
    }
}