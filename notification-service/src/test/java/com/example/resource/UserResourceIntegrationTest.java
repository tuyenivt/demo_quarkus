package com.example.resource;

import com.example.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@Testcontainers
class UserResourceIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("notificationdb")
            .withUsername("yourusername")
            .withPassword("yourpassword");

    @Test
    void testCreateAndGetUser() {
        var user = new User();
        user.username = "testUsername";
        user.fullName = "Full Name";
        user.email = "test@example.com";

        RestAssured.given()
                .contentType("application/json")
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(200)
                .body("username", is(user.username))
                .body("fullName", is(user.fullName))
                .body("email", is(user.email));
    }
}
