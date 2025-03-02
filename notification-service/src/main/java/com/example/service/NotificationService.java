package com.example.service;

import com.example.exception.NotificationException;
import com.example.model.NotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class NotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class);
    private static final String NOTIFICATION_QUEUE = "notification.queue";

    private final RateLimiterService rateLimiterService;
    private final IdempotencyService idempotencyService;
    private final RabbitMQClient rabbitMQClient;
    private final ObjectMapper objectMapper;

    private Connection connection;
    private Channel channel;

    @Inject
    public NotificationService(RateLimiterService rateLimiterService, IdempotencyService idempotencyService,
                               RabbitMQClient rabbitMQClient) {
        this.rateLimiterService = rateLimiterService;
        this.idempotencyService = idempotencyService;
        this.rabbitMQClient = rabbitMQClient;
        this.objectMapper = new ObjectMapper();
    }

    public void onStart(@Observes StartupEvent event) {
        try {
            connection = rabbitMQClient.connect();
            channel = connection.createChannel();
            channel.queueDeclare(NOTIFICATION_QUEUE, true, false, false, null);
        } catch (IOException e) {
            throw new NotificationException("Failed to initialize RabbitMQ channel", e);
        }
    }

    public void submitNotification(NotificationRequest request) {
        if (!rateLimiterService.isAllowed(request.getRequestId())) {
            LOGGER.warn("Rate limit exceeded.");
            throw new NotificationException("Rate limit exceeded.");
        }

        if (idempotencyService.isDuplicate(request.getRequestId())) {
            LOGGER.infof("Duplicate request detected for requestId=%s", request.getRequestId());
            return;
        }

        if (!idempotencyService.markRequest(request.getRequestId())) {
            LOGGER.infof("Concurrent request detected for requestId=%s", request.getRequestId());
            return;
        }

        try {
            channel.basicPublish("", NOTIFICATION_QUEUE, null, objectMapper.writeValueAsBytes(request));
            LOGGER.infof("Request enqueued for requestId=%s", request.getRequestId());
        } catch (Exception e) {
            LOGGER.error("Failed to publish to RabbitMQ", e);
            throw new NotificationException("Failed to enqueue notification", e);
        }
    }

    @PreDestroy
    void cleanup() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Failed to close RabbitMQ resources", e);
        }
    }
}
