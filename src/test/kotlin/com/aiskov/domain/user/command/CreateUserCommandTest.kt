package com.aiskov.domain.user.command

import com.aiskov.utils.UserData
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.mongodb.client.MongoClient
import jakarta.inject.Inject
import org.bson.Document
import org.eclipse.microprofile.config.inject.ConfigProperty

@QuarkusTest
class CreateUserCommandTest {

    @Inject
    lateinit var mongoClient: MongoClient

    @Inject
    @ConfigProperty(name = "app.db.name")
    lateinit var dbName: String

    private val usersColl = "User"

    @BeforeEach
    fun cleanup() {
        val db = mongoClient.getDatabase(dbName)
        db.getCollection(usersColl).deleteMany(Document())
    }

    @Test
    fun `create user happy path`() {
        val payload = UserData.createUserPayload()

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`()
            .post("/api/command/v1/users")
            .then()
            .statusCode(200)
            .body("id", equalTo(payload["email"]))
            .body("version", equalTo(1))

        // verify stored in DB
        val db = mongoClient.getDatabase(dbName)
        val doc = db.getCollection(usersColl).find(Document("_id", payload["email"])).first()
        assert(doc != null)
        assert((doc.getString("_id")) == payload["email"])
    }

    @Test
    fun `create user duplicate email returns validation error`() {
        // generate email to reuse
        val email = UserData.email()

        // insert existing user directly
        val db = mongoClient.getDatabase(dbName)
        val users = db.getCollection(usersColl)
        users.insertOne(Document("_id", email).append("name", "Existing").append("password", "x").append("createdAt", System.currentTimeMillis()).append("version", 1))

        val payload = mapOf(
            "email" to email,
            "name" to UserData.name(),
            "password" to UserData.password()
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`()
            .post("/api/command/v1/users")
            .then()
            .statusCode(400)
            .body("code", equalTo("VALIDATION_ERROR"))
            .body("message", containsString("Non unique value"))
            .body("payload.aggregateType", equalTo("User"))
            .body("payload.field", equalTo("email"))
            .body("payload.fieldValue", equalTo(email))
    }
}
