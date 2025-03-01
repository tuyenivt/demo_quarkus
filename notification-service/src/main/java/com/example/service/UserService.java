package com.example.service;

import com.example.entity.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserService {
    public Uni<List<User>> listAll() {
        return User.listAll();
    }

    public Uni<User> findById(Long id) {
        return User.findById(id);
    }

    public Uni<User> create(User user) {
        return user.persist().replaceWith(user);
    }

    public Uni<User> update(Long id, User updatedUser) {
        return User.<User>findById(id)
                .onItem().ifNotNull().invoke(existing -> {
                    existing.fullName = updatedUser.fullName;
                    existing.email = updatedUser.email;
                });
    }

    public Uni<Void> delete(Long id) {
        return User.deleteById(id).replaceWithVoid();
    }
}
