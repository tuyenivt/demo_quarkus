package com.chatsystem.service;

import com.chatsystem.model.Message;
import com.chatsystem.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MessageService {
    
    private static final Logger LOG = Logger.getLogger(MessageService.class);
    
    @Inject
    UserService userService;
    
    @Inject
    ObjectMapper objectMapper;
    
    @Transactional
    public Message processIncomingMessage(String jsonMessage, String senderUsername) {
        try {
            Map<String, Object> messageMap = objectMapper.readValue(jsonMessage, Map.class);
            String receiverUsername = (String) messageMap.get("receiver");
            String content = (String) messageMap.get("content");
            String messageType = (String) messageMap.getOrDefault("messageType", "TEXT");
            String fileUrl = (String) messageMap.get("fileUrl");
            
            User sender = userService.findByUsername(senderUsername);
            User receiver = userService.findByUsername(receiverUsername);
            
            if (sender == null || receiver == null) {
                LOG.error("Invalid sender or receiver username");
                return null;
            }
            
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(content);
            message.setMessageType(messageType);
            message.setFileUrl(fileUrl);
            
            return message;
        } catch (Exception e) {
            LOG.error("Error processing incoming message: " + e.getMessage());
            return null;
        }
    }
    
    @Transactional
    public void saveMessage(Message message) {
        message.persist();
    }
    
    @Transactional
    public void updateMessage(Message message) {
        message.persist();
    }
    
    public List<Message> getChatHistory(String username1, String username2, int limit) {
        PanacheQuery<Message> query = Message.find(
            "(sender.username = ?1 and receiver.username = ?2) or " +
            "(sender.username = ?2 and receiver.username = ?1)",
            username1, username2
        );
        return query.page(0, limit).list();
    }
    
    public List<Message> getUnreadMessages(String username) {
        return Message.find("receiver.username = ?1 and isRead = false", username).list();
    }
    
    @Transactional
    public void markMessagesAsRead(String username1, String username2) {
        List<Message> unreadMessages = Message.find(
            "receiver.username = ?1 and sender.username = ?2 and isRead = false",
            username1, username2
        ).list();
        
        for (Message message : unreadMessages) {
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
            message.persist();
        }
    }
} 