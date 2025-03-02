package com.example.service;

import com.example.exception.NotificationException;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class IdempotencyService {
    private static final String PREFIX = "notification:reqId:";

    @ConfigProperty(name = "idempotency.expire-seconds", defaultValue = "86400")
    private Long expireSeconds;

    private final Redis redisClient;

    @Inject
    public IdempotencyService(Redis redisClient) {
        this.redisClient = redisClient;
    }

    public boolean isDuplicate(String requestId) {
        try {
            var request = Request.cmd(Command.GET).arg(createKey(requestId));
            var response = redisClient.send(request).toCompletionStage().toCompletableFuture().get();
            return response != null; // if present, it's a duplicate
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt(); // restore interrupted status if needed
            throw new NotificationException("Redis operation failed", e);
        }
    }

    public boolean markRequest(String requestId) {
        try {
            var request = Request.cmd(Command.SET)
                    .arg(createKey(requestId))
                    .arg("")    // Empty value for required argument.
                    .arg("NX")  // Only set if the key does not exist.
                    .arg("EX")  // Set an expiration time.
                    .arg(String.valueOf(expireSeconds));
            var response = redisClient.send(request).toCompletionStage().toCompletableFuture().get();
            // If the response is "OK", the key was set. Otherwise, it already existed.
            return response != null && "OK".equalsIgnoreCase(response.toString());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt(); // restore interrupted status if needed
            throw new NotificationException("Redis operation failed", e);
        }
    }

    private String createKey(String requestId) {
        return PREFIX + requestId;
    }
}
