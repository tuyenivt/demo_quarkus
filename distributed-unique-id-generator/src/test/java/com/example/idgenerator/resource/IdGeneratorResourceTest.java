package com.example.idgenerator.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class IdGeneratorResourceTest {

    @Test
    void testGenerateSingleId() {
        given()
            .when()
            .get("/api/v1/ids/generate")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", notNullValue());
    }

    @Test
    void testGenerateBatchIds() {
        given()
            .when()
            .get("/api/v1/ids/generate-batch?count=10")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("ids", hasSize(10));
    }

    @Test
    void testInvalidBatchSize() {
        given()
            .when()
            .get("/api/v1/ids/generate-batch?count=0")
            .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("error", containsString("Count must be between 1 and 1000"));
    }

    @Test
    void testHealthCheck() {
        given()
            .when()
            .get("/health/live")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }
} 