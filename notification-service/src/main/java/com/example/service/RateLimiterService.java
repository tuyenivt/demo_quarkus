package com.example.service;

import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class RateLimiterService {
    @ConfigProperty(name = "rate-limiter.max-requests", defaultValue = "1000")
    private Integer defaultMaxRequests;

    @ConfigProperty(name = "rate-limiter.window-seconds", defaultValue = "60")
    private Integer defaultWindowSeconds;

    private final Redis redisClient;

    @Inject
    public RateLimiterService(Redis redisClient) {
        this.redisClient = redisClient;
    }

    public boolean isAllowed(String key) {
        return isAllowed(key, defaultMaxRequests, defaultWindowSeconds);
    }

    /**
     * Checks if a request is allowed based on a maximum number of requests in a given time window.
     *
     * @param key           the unique key for the rate limit (e.g., user ID or API endpoint or Idempotency-Key)
     * @param maxRequests   the maximum number of allowed requests in the window
     * @param windowSeconds the duration of the window in seconds
     * @return true if the request is allowed, false if rate limited
     */
    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {
        // Increment the counter atomically
        var incrRequest = Request.cmd(Command.INCR).arg(key);
        var response = redisClient.send(incrRequest);
        var currentCount = response.result().toLong();

        // If this is the first request in the window, set the expiration for the key
        if (currentCount == 1) {
            redisClient.send(Request.cmd(Command.EXPIRE).arg(key).arg(windowSeconds));
        }

        // If the count exceeds the allowed maximum, deny the request
        return currentCount <= maxRequests;
    }
}
