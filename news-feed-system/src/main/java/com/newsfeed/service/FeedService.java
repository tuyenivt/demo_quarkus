package com.newsfeed.service;

import com.newsfeed.model.Content;
import com.newsfeed.model.User;
import io.quarkus.redis.client.RedisClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class FeedService {
    private static final Logger LOG = Logger.getLogger(FeedService.class);
    private static final String FEED_CACHE_PREFIX = "feed:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    @Inject
    RedisClient redisClient;

    @Inject
    ContentService contentService;

    @Inject
    UserService userService;

    public Uni<List<Content>> getPersonalizedFeed(Long userId, int limit) {
        return Uni.createFrom().item(() -> {
            // Try to get cached feed first
            String cacheKey = FEED_CACHE_PREFIX + userId;
            String cachedFeed = redisClient.get(cacheKey).toString();
            
            if (cachedFeed != null) {
                LOG.debugf("Retrieved feed from cache for user %d", userId);
                return deserializeFeed(cachedFeed);
            }

            // Generate new feed if not in cache
            List<Content> feed = generateFeed(userId, limit);
            
            // Cache the new feed
            redisClient.setex(cacheKey, String.valueOf(CACHE_TTL.getSeconds()), serializeFeed(feed));
            
            return feed;
        });
    }

    private List<Content> generateFeed(Long userId, int limit) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Get content from various sources
        List<Content> content = contentService.getRecentContent(limit * 2); // Get more content than needed for ranking

        // Apply personalization and ranking
        return content.stream()
                .map(item -> {
                    double score = calculateRelevanceScore(item, user);
                    item.setRelevanceScore(score);
                    return item;
                })
                .sorted(Comparator.comparingDouble(Content::getRelevanceScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calculateRelevanceScore(Content content, User user) {
        double score = 0.0;

        // Recency factor (exponential decay)
        double recencyFactor = Math.exp(-Duration.between(content.getPublishedAt(), Instant.now()).toHours() / 24.0);
        score += recencyFactor * 0.4;

        // Interest matching
        double interestMatch = calculateInterestMatch(content, user);
        score += interestMatch * 0.3;

        // Social relevance
        double socialRelevance = calculateSocialRelevance(content, user);
        score += socialRelevance * 0.2;

        // Engagement factor
        double engagementFactor = calculateEngagementFactor(content);
        score += engagementFactor * 0.1;

        return score;
    }

    private double calculateInterestMatch(Content content, User user) {
        if (user.getInterests().isEmpty()) return 0.5;
        
        long matchingTags = content.getTags().stream()
                .filter(tag -> user.getInterests().contains(tag))
                .count();
        
        return (double) matchingTags / Math.max(user.getInterests().size(), 1);
    }

    private double calculateSocialRelevance(Content content, User user) {
        // Check if content is from followed users
        boolean isFromFollowedUser = content.getInteractions().stream()
                .anyMatch(interaction -> user.getFollowing().contains(interaction.getUser()));
        
        return isFromFollowedUser ? 1.0 : 0.0;
    }

    private double calculateEngagementFactor(Content content) {
        if (content.getInteractions().isEmpty()) return 0.0;

        double totalWeight = content.getInteractions().stream()
                .mapToInt(interaction -> interaction.getType().getBaseWeight())
                .sum();

        return Math.min(totalWeight / 100.0, 1.0); // Normalize to 0-1 range
    }

    private String serializeFeed(List<Content> feed) {
        // Simple serialization - in production, use a proper serializer
        return feed.stream()
                .map(content -> content.id.toString())
                .collect(Collectors.joining(","));
    }

    private List<Content> deserializeFeed(String serialized) {
        // Simple deserialization - in production, use a proper deserializer
        return Arrays.stream(serialized.split(","))
                .map(Long::parseLong)
                .map(contentService::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
} 