package com.newsfeed.service;

import com.newsfeed.model.Content;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaService {
    private static final Logger LOG = Logger.getLogger(KafkaService.class);

    @Inject
    @Channel("content-out")
    Emitter<Record<Long, Content>> contentEmitter;

    @Inject
    @Channel("content-updates-out")
    Emitter<Record<Long, Content>> contentUpdatesEmitter;

    public void publishContent(Content content) {
        try {
            contentEmitter.send(Record.of(content.id, content));
            LOG.debugf("Published content to Kafka: %d", content.id);
        } catch (Exception e) {
            LOG.errorf("Error publishing content to Kafka: %s", e.getMessage());
        }
    }

    public void publishContentUpdate(Content content) {
        try {
            contentUpdatesEmitter.send(Record.of(content.id, content));
            LOG.debugf("Published content update to Kafka: %d", content.id);
        } catch (Exception e) {
            LOG.errorf("Error publishing content update to Kafka: %s", e.getMessage());
        }
    }
} 