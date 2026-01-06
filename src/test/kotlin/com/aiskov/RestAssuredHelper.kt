package com.aiskov

import com.aiskov.config.JSON
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.equalTo

fun RequestSpecification.jsonBody(body: Any): RequestSpecification {
    return contentType(ContentType.JSON)
        .body(body)
}

fun ValidatableResponse.commandResponse(id: Any, version: Int): ValidatableResponse {
    return body("id", equalTo(id))
        .body("version", equalTo(version))
}

fun post(url: String, body: Any): ValidatableResponse {
    return RestAssured.given()
        .jsonBody(body)
        .`when`()
        .post(url)
        .then()
}