package com.newsfeed.service;

import com.newsfeed.model.User;
import com.newsfeed.model.UserInteraction;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class UserService implements PanacheRepository<User> {

    @Override
    public User findById(Long id) {
        return find("id", id).firstResult();
    }

    @Transactional
    public Uni<User> createUser(User user) {
        return Uni.createFrom().item(() -> {
            persist(user);
            return user;
        });
    }

    @Transactional
    public Uni<Void> updateUserInterests(Long userId, Set<String> interests) {
        return Uni.createFrom().item(() -> {
            User user = findById(userId);
            if (user != null) {
                user.setInterests(interests);
                persist(user);
            }
            return null;
        });
    }

    @Transactional
    public Uni<Void> followUser(Long userId, Long followUserId) {
        return Uni.createFrom().item(() -> {
            User user = findById(userId);
            User followUser = findById(followUserId);
            
            if (user != null && followUser != null) {
                user.getFollowing().add(followUser);
                followUser.getFollowers().add(user);
                persist(user);
                persist(followUser);
            }
            return null;
        });
    }

    @Transactional
    public void recordInteraction(Long userId, UserInteraction interaction) {
        User user = findById(userId);
        if (user != null) {
            interaction.setUser(user);
            interaction.setTimestamp(java.time.Instant.now());
            user.getInteractions().add(interaction);
            persist(user);
        }
    }

    public List<User> getFollowers(Long userId) {
        User user = findById(userId);
        return user != null ? List.copyOf(user.getFollowers()) : List.of();
    }

    public List<User> getFollowing(Long userId) {
        User user = findById(userId);
        return user != null ? List.copyOf(user.getFollowing()) : List.of();
    }
} 