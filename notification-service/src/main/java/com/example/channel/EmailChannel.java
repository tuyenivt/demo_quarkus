package com.example.channel;

import com.example.model.NotificationRequest;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailChannel implements NotificationChannel {
    private static final Logger LOGGER = Logger.getLogger(EmailChannel.class);

    /**
     * <p>Sends an email to the recipients in the request</p>
     *
     * <p>CircuitBreaker opens if at least 4 requests have been made and 75% failed, then waits 1 second before trying again
     * <p>Retry up to 3 times with a 100ms delay between attempts</p>
     */
    @Override
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    @Retry(maxRetries = 3, delay = 100)
    public void send(NotificationRequest request) {
        LOGGER.infof("Sending EMAIL to %s with message: %s", request.getRecipients(), request.getMessage());
        // In production, call e.g. SendGrid, SES, or an SMTP server
    }
}
