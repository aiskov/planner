package com.aiskov.domain.user.command

import com.aiskov.utils.json.Secret
import com.aiskov.domain.common.CommandRequest
import com.aiskov.utils.handlers.Command.CreateCommand
import com.aiskov.utils.handlers.CommandGateway
import com.aiskov.utils.toResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.hibernate.validator.constraints.Length

data class CreateUserCommand(
    val email: String,
    val name: String,
    val password: String,
) : CreateCommand

@Schema(
    description = "Request payload to create a new user (API v1).",
)
data class CreateUserV1Request(
    @field:Schema(
        description = "User's email address, used as login and identifier.",
        example = "user@example.com",
        required = true,
        format = "email",
        minLength = 5,
        maxLength = 100,
        type = SchemaType.STRING,
    )
    @get:Email
    @get:NotNull
    @get:Length(min = 5, max = 100)
    val email: String?,

    @field:Schema(
        description = "Display name for the user.",
        example = "Jane Doe",
        required = true,
        minLength = 3,
        maxLength = 100,
        type = SchemaType.STRING,
    )
    @get:NotBlank
    @get:Length(
        min = 3,
        max = 100,
    )
    @get:Pattern(
        regexp = "^[^\\r\\n\\t]*$",
        message = "must not contain newline or tab characters",
    )
    val name: String?,

    @field:Schema(
        description = "Password for the user account.",
        example = "P@ssw0rd!",
        required = true,
        minLength = 8,
        maxLength = 60,
        format = "password",
        type = SchemaType.STRING,
    )
    @get:NotBlank
    @get:Length(
        min = 8,
        max = 60,
    )
    @get:Pattern(
        // Require at least one uppercase, one lowercase, one digit and one special character
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).+$",
        message = "password must contain uppercase, lowercase, digit and special character",
    )
    @get:Pattern(
        regexp = "^[^\\r\\n\\t]*$",
        message = "must not contain newline or tab characters",
    )
    @field:Secret
    val password: String?,
) : CommandRequest<CreateUserCommand> {
    override fun toCommand(): CreateUserCommand {
        return CreateUserCommand(
            email = email!!,
            name = name!!,
            password = password!!,
        )
    }

    override fun toString(): String {
        return toCommandString(CreateUserCommand::class)
    }
}

@ApplicationScoped
@Path("/")
@Tag(
    name = "Users",
    description = "Endpoints for managing users in the system.",
)
class UserCommandV1Resource {
    @Inject
    private lateinit var gateway: CommandGateway

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Create User (v1)",
        description = "Creates a new user in the system with the provided details.",
    )
    @Path("/api/command/v1/users")
    fun create(cmd: CreateUserV1Request): Response {
        return gateway.process(cmd).toResponse()
    }
}