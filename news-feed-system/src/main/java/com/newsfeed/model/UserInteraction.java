package com.newsfeed.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@Entity
@Table(name = "user_interactions")
@EqualsAndHashCode(callSuper = true)
public class UserInteraction extends PanacheEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;
    
    @Column(nullable = false)
    private InteractionType type;
    
    @Column(nullable = false)
    private Instant timestamp;
    
    @Column
    private Integer weight;
    
    public enum InteractionType {
        VIEW(1),
        LIKE(2),
        SHARE(3),
        COMMENT(2),
        SAVE(1);
        
        private final int baseWeight;
        
        InteractionType(int baseWeight) {
            this.baseWeight = baseWeight;
        }
        
        public int getBaseWeight() {
            return baseWeight;
        }
    }
} 