package com.example.model;

import com.example.channel.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String requestId; // for idempotency
    private List<String> recipients;
    private String message;
    private ChannelType channel; // "SMS", "EMAIL", "PUSH"
}
