package com.aiskov.domain.user.command

import com.aiskov.config.DbCollectionProvider
import com.aiskov.domain.user.User
import com.aiskov.utils.UserData
import com.aiskov.utils.any
import com.aiskov.utils.byId
import com.aiskov.utils.findById
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import jakarta.inject.Inject
import org.bson.Document
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@QuarkusTest
class CreateUserCommandTest {

    @Inject
    lateinit var db: DbCollectionProvider

    private val usersColl = "User"

    @BeforeEach
    fun cleanup() {
        db.collection(User::class).deleteMany(any())
    }

    @Test
    fun `create user happy path`() {
        // Given
        val payload = UserData.createUserPayload()
        val id = payload["email"]!!

        // When
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`()
            .post("/api/command/v1/users")
            .then()

        // Then
        response
            .statusCode(200)
            .body("id", equalTo(payload["email"]))
            .body("version", equalTo(1))

        val doc = db.collection(User::class).findById(id)
        assert(doc != null)
        assert(doc!!.id == payload["email"])
    }

    @Test
    fun `create user duplicate email returns validation error`() {
        // Given
        val email = UserData.email()

        // insert existing user directly
        val users = db.collection(User::class)
        users.insertOne(
            User(
                id = email,
                name = "Existing",
                password = "x",
            )
        )

        val payload = mapOf(
            "email" to email,
            "name" to UserData.name(),
            "password" to UserData.password()
        )

        // When
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`()
            .post("/api/command/v1/users")
            .then()
        
        // Then
        response
            .statusCode(400)
            .body("code", equalTo("VALIDATION_ERROR"))
            .body("message", containsString("Non unique value"))
            .body("payload.aggregateType", equalTo("User"))
            .body("payload.field", equalTo("email"))
            .body("payload.fieldValue", equalTo(email))
    }
}
