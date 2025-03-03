package com.example.consumer;

import com.example.channel.EmailChannel;
import com.example.channel.NotificationChannel;
import com.example.channel.PushChannel;
import com.example.channel.SmsChannel;
import com.example.exception.NotificationException;
import com.example.model.NotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class NotificationConsumer {
    private static final Logger LOGGER = Logger.getLogger(NotificationConsumer.class);

    private final EmailChannel emailChannel;
    private final SmsChannel smsChannel;
    private final PushChannel pushChannel;
    private final RabbitMQClient rabbitMQClient;
    private final ObjectMapper objectMapper;

    private Connection connection;
    private Channel channel;

    @Inject
    public NotificationConsumer(EmailChannel emailChannel, SmsChannel smsChannel, PushChannel pushChannel,
                                RabbitMQClient rabbitMQClient) {
        this.emailChannel = emailChannel;
        this.smsChannel = smsChannel;
        this.pushChannel = pushChannel;
        this.rabbitMQClient = rabbitMQClient;
        this.objectMapper = new ObjectMapper();
    }

    public void onStart(@Observes StartupEvent event) {
        try {
            connection = rabbitMQClient.connect();
            channel = connection.createChannel();
            channel.queueDeclare(NotificationChannel.NOTIFICATION_QUEUE, true, false, false, null);
            channel.basicConsume(NotificationChannel.NOTIFICATION_QUEUE, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    var message = new String(body, StandardCharsets.UTF_8);
                    LOGGER.infof("Received message:%s", message);
                    process(objectMapper.readValue(message, NotificationRequest.class));
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            });
        } catch (IOException e) {
            throw new NotificationException("Failed to initialize RabbitMQ channel", e);
        }
    }

    public void process(NotificationRequest request) {
        try {
            var notificationChannel = switch (request.getChannel()) {
                case SMS -> smsChannel;
                case PUSH -> pushChannel;
                default -> emailChannel;
            };

            notificationChannel.send(request);
            LOGGER.infof("Notification sent successfully for requestId=%s", request.getRequestId());
        } catch (Exception ex) {
            LOGGER.errorf(ex, "Failed to process notification for requestId=%s", request.getRequestId());
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
