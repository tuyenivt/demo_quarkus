package com.example.resource;

import com.example.model.NotificationRequest;
import com.example.service.NotificationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.UUID;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {
    private static final Logger LOGGER = Logger.getLogger(NotificationResource.class);

    private final NotificationService notificationService;

    @Inject
    public NotificationResource(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @POST
    public String sendNotification(NotificationRequest request) {
        LOGGER.infof("Received notification request %s", request.getRequestId());
        if (request.getRequestId() == null || request.getRequestId().isEmpty()) {
            String generatedId = UUID.randomUUID().toString();
            request.setRequestId(generatedId);
            LOGGER.infof("RequestId is missing, service generated new requestId: %s", generatedId);
        }
        notificationService.submitNotification(request);
        return "{\"status\":\"queued\", \"requestId\":\"" + request.getRequestId() + "\"}";
    }
}
