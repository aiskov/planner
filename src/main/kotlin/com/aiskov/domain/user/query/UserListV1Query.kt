package com.aiskov.domain.user.query

import com.aiskov.domain.common.QueryListRequest
import com.aiskov.domain.user.UserService
import com.aiskov.utils.toResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag

data class UserListV1Request(
    override val search: String?,
    override val sort: String?,
    override val desc: Boolean?
) : QueryListRequest<UserListV1Query> {
    override fun toQuery(): UserListV1Query {
        return UserListV1Query(
            search = this.search,
            sort = this.sort ?: UserListV1Response::email.name,
            desc = this.desc ?: false,
        )
    }
}

data class UserListV1Query(
    val search: String?,
    val sort: String,
    val desc: Boolean
)

data class UserListV1Response(
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
class UserListV1Resource {
    @Inject
    private lateinit var userService: UserService

    @GET
    @Path("/api/query/v1/users")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun listUsers(
        @QueryParam("search") search: String?,
        @QueryParam("sort") sort: String?,
        @QueryParam("desc") desc: Boolean?
    ): Response {
        return userService.findAllV1(
            UserListV1Request(
                search = search,
                sort = sort,
                desc = desc,
            )
        ).toResponse()
    }
}
