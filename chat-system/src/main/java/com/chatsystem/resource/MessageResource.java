package com.chatsystem.resource;

import com.chatsystem.model.Message;
import com.chatsystem.service.MessageService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/api/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {
    
    private static final Logger LOG = Logger.getLogger(MessageResource.class);
    
    @Inject
    MessageService messageService;
    
    @GET
    @Path("/history/{username1}/{username2}")
    public List<Message> getChatHistory(
        @PathParam("username1") String username1,
        @PathParam("username2") String username2,
        @QueryParam("limit") @DefaultValue("50") int limit
    ) {
        return messageService.getChatHistory(username1, username2, limit);
    }
    
    @GET
    @Path("/unread/{username}")
    public List<Message> getUnreadMessages(@PathParam("username") String username) {
        return messageService.getUnreadMessages(username);
    }
    
    @POST
    @Path("/mark-read/{username1}/{username2}")
    public Response markMessagesAsRead(
        @PathParam("username1") String username1,
        @PathParam("username2") String username2
    ) {
        try {
            messageService.markMessagesAsRead(username1, username2);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error marking messages as read: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error"))
                .build();
        }
    }
    
    @POST
    @Path("/send")
    public Response sendMessage(MessageRequest request) {
        try {
            Message message = messageService.processIncomingMessage(
                request.toJson(),
                request.getSender()
            );
            if (message != null) {
                messageService.saveMessage(message);
                return Response.ok(message).build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Invalid message format"))
                .build();
        } catch (Exception e) {
            LOG.error("Error sending message: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error"))
                .build();
        }
    }
    
    // Inner classes for request/response objects
    public static class MessageRequest {
        private String sender;
        private String receiver;
        private String content;
        private String messageType;
        private String fileUrl;
        
        // Getters and setters
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public String getReceiver() { return receiver; }
        public void setReceiver(String receiver) { this.receiver = receiver; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        
        public String toJson() {
            return String.format(
                "{\"sender\":\"%s\",\"receiver\":\"%s\",\"content\":\"%s\",\"messageType\":\"%s\",\"fileUrl\":\"%s\"}",
                sender, receiver, content, messageType, fileUrl
            );
        }
    }
    
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
} 