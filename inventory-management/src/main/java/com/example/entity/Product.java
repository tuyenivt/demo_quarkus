package com.example.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Product extends PanacheEntity {
    @Column(unique = true, nullable = false)
    public String sku;

    @Column(nullable = false)
    public String name;

    public String description;
}
