package com.aiskov.domain.user.command

import com.aiskov.commandResponse
import com.aiskov.config.DbCollectionProvider
import com.aiskov.domain.common.errors.ErrorCodes
import com.aiskov.domain.user.User
import com.aiskov.jsonBody
import com.aiskov.post
import com.aiskov.utils.UserData
import com.aiskov.utils.db.any
import com.aiskov.utils.db.findById
import com.aiskov.utils.ensureExists
import com.aiskov.utils.equalAsString
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import jakarta.inject.Inject
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@QuarkusTest
class CreateUserV1CommandTest {

    @Inject
    private lateinit var dbProvider: DbCollectionProvider

    @BeforeEach
    fun cleanup() {
        dbProvider.collection(User::class).deleteMany(any())
    }

    @Test
    fun `should save`() {
        // Given
        val payload = UserData.createUser
        val id = payload.email!!

        // When
        val response = post("/api/command/v1/users", payload)

        // Then
        response.statusCode(200)
            .commandResponse(payload.email, 1)

        val doc = dbProvider.collection(User::class).findById(id)
        assert(doc != null)
        assert(doc!!.id == payload.email)
    }

    @Test
    fun `should fail on non unique email`() {
        // Given
        val user = dbProvider.ensureExists(UserData.aggregate)
        val payload = UserData.createUser.copy(email = user.id)

        // When
        val response = post("/api/command/v1/users", payload)

        // Then
        response
            .statusCode(400)
            .body("code", equalAsString(ErrorCodes.RULE_VIOLATION))
            .body("message", containsString("Non unique value"))
            .body("payload.aggregateType", equalTo(User::class.simpleName))
            .body("payload.field", equalTo(CreateUserV1Request::email.name))
            .body("payload.fieldValue", equalTo(payload.email))
    }
}
