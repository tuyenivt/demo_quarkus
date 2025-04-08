package com.newsfeed.api;

import com.newsfeed.model.Content;
import com.newsfeed.model.UserInteraction;
import com.newsfeed.service.FeedService;
import com.newsfeed.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@QuarkusTest
class FeedResourceTest {

    @InjectMock
    FeedService feedService;

    @InjectMock
    UserService userService;

    @BeforeEach
    void setUp() {
        RestAssured.basePath = "/api/v1";
    }

    @Test
    void testGetFeed() {
        // Setup test data
        Content content = new Content();
        content.setId(1L);
        content.setTitle("Test Content");
        List<Content> feed = Arrays.asList(content);

        // Mock service response
        when(feedService.getPersonalizedFeed(any(), anyInt())).thenReturn(io.smallrye.mutiny.Uni.createFrom().item(feed));

        // Test API endpoint
        given()
            .pathParam("userId", 1)
            .queryParam("limit", 10)
        .when()
            .get("/feed/{userId}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    void testRecordInteraction() {
        // Setup test data
        UserInteraction interaction = new UserInteraction();
        interaction.setType(UserInteraction.InteractionType.LIKE);

        // Mock service response
        when(userService.recordInteraction(any(), any())).thenReturn(null);

        // Test API endpoint
        given()
            .pathParam("userId", 1)
            .contentType(ContentType.JSON)
            .body(interaction)
        .when()
            .post("/feed/{userId}/interact")
        .then()
            .statusCode(200);
    }

    @Test
    void testHealthCheck() {
        given()
        .when()
            .get("/feed/health")
        .then()
            .statusCode(200)
            .body(is("News Feed System is healthy"));
    }
} 