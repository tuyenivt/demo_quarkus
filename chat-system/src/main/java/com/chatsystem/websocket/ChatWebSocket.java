package com.chatsystem.websocket;

import com.chatsystem.model.Message;
import com.chatsystem.model.User;
import com.chatsystem.service.MessageService;
import com.chatsystem.service.UserService;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{username}")
@ApplicationScoped
public class ChatWebSocket {

    private static final Logger LOG = Logger.getLogger(ChatWebSocket.class);

    @Inject
    MessageService messageService;

    @Inject
    UserService userService;

    @Inject
    RedisClient redisClient;

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        User user = userService.findByUsername(username);
        if (user != null) {
            user.setOnline(true);
            user.setLastSeen(null);
            userService.updateUser(user);
            broadcastUserStatus(username, true);
        }
        LOG.info("User connected: " + username);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        User user = userService.findByUsername(username);
        if (user != null) {
            user.setOnline(false);
            user.setLastSeen(java.time.LocalDateTime.now());
            userService.updateUser(user);
            broadcastUserStatus(username, false);
        }
        LOG.info("User disconnected: " + username);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        LOG.error("Error for user " + username + ": " + throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        try {
            Message chatMessage = messageService.processIncomingMessage(message, username);
            if (chatMessage != null) {
                // Store message in database
                messageService.saveMessage(chatMessage);

                // Publish to Redis for other instances
                redisClient.publish("chat-messages", message);

                // Send to receiver if online
                Session receiverSession = sessions.get(chatMessage.getReceiver().getUsername());
                if (receiverSession != null) {
                    receiverSession.getAsyncRemote().sendText(message);
                    chatMessage.setDelivered(true);
                    chatMessage.setDeliveredAt(java.time.LocalDateTime.now());
                    messageService.updateMessage(chatMessage);
                }
            }
        } catch (Exception e) {
            LOG.error("Error processing message: " + e.getMessage());
        }
    }

    private void broadcastUserStatus(String username, boolean isOnline) {
        String statusMessage = String.format("{\"type\":\"status\",\"username\":\"%s\",\"online\":%b}",
            username, isOnline);
        sessions.values().forEach(session -> {
            session.getAsyncRemote().sendText(statusMessage);
        });
    }

    public static Map<String, Session> getSessions() {
        return sessions;
    }
} 