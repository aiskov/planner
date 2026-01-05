package com.aiskov.utils.handlers

import com.aiskov.config.CommandHandlersConfig
import com.aiskov.domain.common.CommandRepository
import com.aiskov.domain.common.CommandRequest
import com.aiskov.domain.common.CommandResponse
import com.aiskov.domain.common.Policies
import com.aiskov.utils.ValidationService
import com.aiskov.utils.check
import com.aiskov.utils.handlers.Command.CreateCommand
import com.aiskov.utils.handlers.Command.ModifyCommand
import com.aiskov.utils.handlers.CommandHandler.CreateCommandHandler
import com.aiskov.utils.handlers.CommandHandler.ModifyCommandHandler
import com.aiskov.utils.silentSuccess
import com.aiskov.utils.then
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

@ApplicationScoped
class CommandGateway {
    val log = LoggerFactory.getLogger(javaClass)

    @Inject
    private lateinit var config: CommandHandlersConfig

    @Inject
    private lateinit var repository: CommandRepository

    @Inject
    private lateinit var validationService: ValidationService

    @Suppress("UNCHECKED_CAST")
    fun process(
        request: CommandRequest<*>,
        targetId: Any = Unit,
    ): Result<CommandResponse<*>> {
        return runCatching {
            log.info("Processing command request: $request")
            validationService.validate(request).onFailure { return Result.failure(it) }

            val command = request.toCommand()
            val commandType = command::class
            val handler = config.handlerOf(commandType)
            val aggregateType = handler.aggregateType
            val policy = config.policiesOf(aggregateType)

            val result: Result<Aggregate<*>> = when (handler) {
                is CreateCommandHandler<*, *, *> -> {
                    (handler as CreateCommandHandler<CreateCommand, Aggregate<Any>, Policies<Aggregate<Any>>>)
                        .operation(
                            command as CreateCommand,
                            policy as Policies<Aggregate<Any>>
                        )
                        .then { repository.create(it) }
                }

                is ModifyCommandHandler<*, *, *> -> {
                    if (targetId == Unit) {
                        return Result.failure(IllegalArgumentException("Target ID is null"))
                    }
                    if (command !is ModifyCommand) {
                        return Result.failure(IllegalStateException("Incorrect command definition"))
                    }

                    repository.findById(aggregateType as KClass<Aggregate<Any>>, targetId)
                        .check { aggregate ->
                            if (aggregate == null) {
                                return@check Result.failure(IllegalArgumentException("Aggregate not found null"))
                            }
                            if (aggregate.deleted) {
                                return@check Result.failure(IllegalArgumentException("Aggregate deleted"))
                            }
                            if (aggregate.version == command.version) {
                                return@check Result.failure(IllegalStateException("Concurrent change detected, version doesn't match"))
                            }

                            Result.silentSuccess()
                        }
                        .then { aggregate ->
                            (handler as ModifyCommandHandler<ModifyCommand, Aggregate<Any>, Policies<Aggregate<Any>>>)
                                .operation(
                                    command,
                                    aggregate as Aggregate<Any>,
                                    policy as Policies<Aggregate<Any>>
                                )
                        }
                        .then { repository.update(it) }
                }
            }

            return result
                .mapCatching { saved ->
                    CommandResponse(
                        id = saved.id as Any,
                        version = saved.version,
                    )
                }
        }
    }
}
