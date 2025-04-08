package com.newsfeed.service;

import com.newsfeed.model.Content;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class ContentService implements PanacheRepository<Content> {

    @Inject
    KafkaService kafkaService;

    public List<Content> getRecentContent(int limit) {
        return find("publishedAt <= ?1", Instant.now())
                .page(0, limit)
                .list();
    }

    @Transactional
    public Uni<Content> createContent(Content content) {
        return Uni.createFrom().item(() -> {
            content.setPublishedAt(Instant.now());
            persist(content);
            
            // Publish to Kafka for real-time updates
            kafkaService.publishContent(content);
            
            return content;
        });
    }

    public Content findById(Long id) {
        return find("id", id).firstResult();
    }

    @Transactional
    public Uni<Void> updateContent(Content content) {
        return Uni.createFrom().item(() -> {
            Content existing = findById(content.id);
            if (existing != null) {
                existing.setTitle(content.getTitle());
                existing.setBody(content.getBody());
                existing.setTags(content.getTags());
                persist(existing);
                
                // Publish update to Kafka
                kafkaService.publishContentUpdate(existing);
            }
            return null;
        });
    }

    public Multi<Content> streamRecentContent() {
        return Multi.createFrom().iterable(
                find("publishedAt <= ?1", Instant.now())
                        .page(0, 100)
                        .list()
        );
    }
} 