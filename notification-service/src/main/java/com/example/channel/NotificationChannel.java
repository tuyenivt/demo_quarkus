package com.example.channel;

import com.example.model.NotificationRequest;

public interface NotificationChannel {
    void send(NotificationRequest request);

    String NOTIFICATION_QUEUE = "notification.queue";
}
