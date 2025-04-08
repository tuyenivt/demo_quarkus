package com.newsfeed.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "content")
@EqualsAndHashCode(callSuper = true)
public class Content extends PanacheEntity {
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String body;
    
    @Column(nullable = false)
    private String source;
    
    @Column(nullable = false)
    private Instant publishedAt;
    
    @ElementCollection
    @CollectionTable(name = "content_tags", joinColumns = @JoinColumn(name = "content_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();
    
    @Column(nullable = false)
    private ContentType contentType;
    
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private Set<UserInteraction> interactions = new HashSet<>();
    
    @Column
    private double relevanceScore;
    
    public enum ContentType {
        NEWS,
        SOCIAL_POST,
        USER_GENERATED
    }
} 