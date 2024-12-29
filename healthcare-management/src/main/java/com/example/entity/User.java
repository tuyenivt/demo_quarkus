package com.example.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_")
public class User extends PanacheEntity {
    @Column(unique = true, nullable = false)
    public String username;

    @Column(nullable = false)
    public String password;

    @Column(nullable = false)
    public String role; // e.g., "PATIENT", "DOCTOR", "ADMIN"

    public String fullName;

    public String email;
}
