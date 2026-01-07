package com.aiskov.domain.user.query

import com.aiskov.domain.common.QueryDetailsRequest
import com.aiskov.domain.user.UserService
import com.aiskov.utils.toResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag

data class UserDetailsV1Request(
    override val id: String
) : QueryDetailsRequest<UserDetailsV1Query> {
    override fun toQuery(): UserDetailsV1Query {
        return UserDetailsV1Query(
            id = this.id,
        )
    }
}

data class UserDetailsV1Query(
    val id: String
)

data class UserDetailsV1Response(
    val id: String,
    val email: String,
    val name: String,
    val version: Int,
)

@Path("/")
@ApplicationScoped
@Tag(
    name = "Users",
    description = "Endpoints for managing users in the system.",
)
class UserDetailsV1Resource {
    @Inject
    private lateinit var userService: UserService

    @GET
    @Path("/api/query/v1/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun listUsers(
        @PathParam("id") id: String
    ): Response {
        return userService.findDetailsV1(
            UserDetailsV1Request(
                id = id,
            )
        ).toResponse()
    }
}
