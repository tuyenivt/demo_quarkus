package com.chatsystem.service;

import com.chatsystem.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class UserService {

    @Transactional
    public User createUser(String username, String password, String email) {
        User existingUser = User.findByUsername(username);
        if (existingUser != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // In production, hash the password
        user.setEmail(email);
        user.setOnline(false);
        user.setLastSeen(LocalDateTime.now());
        user.persist();
        
        return user;
    }
    
    public User findByUsername(String username) {
        return User.findByUsername(username);
    }
    
    @Transactional
    public void updateUser(User user) {
        user.persist();
    }
    
    public List<User> getOnlineUsers() {
        return User.find("isOnline = true").list();
    }
    
    public List<User> searchUsers(String query) {
        return User.find("username like ?1 or email like ?1", "%" + query + "%").list();
    }
    
    @Transactional
    public void updateLastSeen(String username) {
        User user = findByUsername(username);
        if (user != null) {
            user.setLastSeen(LocalDateTime.now());
            user.persist();
        }
    }
    
    @Transactional
    public void deleteUser(String username) {
        User user = findByUsername(username);
        if (user != null) {
            user.delete();
        }
    }
} 