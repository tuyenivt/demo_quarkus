package com.example.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Inventory extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    public int quantity;
}
