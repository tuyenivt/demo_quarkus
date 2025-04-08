package com.chatsystem.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message extends PanacheEntity {
    
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "message_type")
    private String messageType; // TEXT, IMAGE, VIDEO, FILE
    
    @Column(name = "file_url")
    private String fileUrl;
    
    @Column(name = "is_delivered")
    private boolean isDelivered;
    
    @Column(name = "is_read")
    private boolean isRead;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    public Message() {
        this.createdAt = LocalDateTime.now();
        this.isDelivered = false;
        this.isRead = false;
    }
} 