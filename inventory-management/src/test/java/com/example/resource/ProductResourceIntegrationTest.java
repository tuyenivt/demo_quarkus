package com.example.resource;

import com.example.entity.Product;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@Testcontainers
class ProductResourceIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("inventorydb")
            .withUsername("yourusername")
            .withPassword("yourpassword");

    @Test
    void testCreateAndGetProduct() {
        var product = new Product();
        product.name = "Test Product";
        product.sku = "TP-001";
        product.description = "A product for testing";

        RestAssured.given()
                .contentType("application/json")
                .body(product)
                .when().post("/products")
                .then()
                .statusCode(200)
                .body("name", is(product.name))
                .body("sku", is(product.sku))
                .body("description", is(product.description));
    }
}
