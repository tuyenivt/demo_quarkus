package com.newsfeed.service;

import com.newsfeed.model.Content;
import com.newsfeed.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@QuarkusTest
public class FeedServiceTest {

    @InjectMock
    ContentService contentService;

    @InjectMock
    UserService userService;

    @InjectMock
    RedisClient redisClient;

    private FeedService feedService;

    @BeforeEach
    void setUp() {
        feedService = new FeedService();
        feedService.contentService = contentService;
        feedService.userService = userService;
        feedService.redisClient = redisClient;
    }

    @Test
    void testGetPersonalizedFeed() {
        // Setup test data
        User user = new User();
        user.setId(1L);
        user.setInterests(new HashSet<>(Arrays.asList("tech", "science")));

        Content content1 = new Content();
        content1.setId(1L);
        content1.setTitle("Tech News");
        content1.setTags(new HashSet<>(Arrays.asList("tech")));
        content1.setPublishedAt(Instant.now());

        Content content2 = new Content();
        content2.setId(2L);
        content2.setTitle("Science News");
        content2.setTags(new HashSet<>(Arrays.asList("science")));
        content2.setPublishedAt(Instant.now().minusSeconds(3600));

        // Mock service responses
        when(userService.findById(any())).thenReturn(user);
        when(contentService.getRecentContent(anyInt())).thenReturn(Arrays.asList(content1, content2));
        when(redisClient.get(any())).thenReturn(null);

        // Test feed generation
        List<Content> feed = feedService.getPersonalizedFeed(1L, 10).await().indefinitely();

        // Verify results
        assertNotNull(feed);
        assertEquals(2, feed.size());
        assertTrue(feed.get(0).getRelevanceScore() >= feed.get(1).getRelevanceScore());
    }

    @Test
    void testGetPersonalizedFeedWithCachedData() {
        // Setup test data
        Content content = new Content();
        content.setId(1L);
        content.setTitle("Cached Content");
        content.setPublishedAt(Instant.now());

        // Mock Redis response
        when(redisClient.get(any())).thenReturn("1");
        when(contentService.findById(1L)).thenReturn(content);

        // Test feed retrieval from cache
        List<Content> feed = feedService.getPersonalizedFeed(1L, 10).await().indefinitely();

        // Verify results
        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals(content.getId(), feed.get(0).getId());
    }

    @Test
    void testCalculateRelevanceScore() {
        // Setup test data
        User user = new User();
        user.setInterests(new HashSet<>(Arrays.asList("tech")));

        Content content = new Content();
        content.setTags(new HashSet<>(Arrays.asList("tech")));
        content.setPublishedAt(Instant.now());

        // Test score calculation
        double score = feedService.calculateRelevanceScore(content, user);

        // Verify score is within expected range
        assertTrue(score >= 0.0 && score <= 1.0);
    }
} 