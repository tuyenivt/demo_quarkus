package com.example.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallets")
public class Wallet extends PanacheEntity {

    @NotNull
    @Column(unique = true)
    public String userId;

    @NotNull
    @Column(precision = 19, scale = 4)
    public BigDecimal balance;

    @Version
    public Long version;

    @Column(unique = true)
    public String walletId;

    public Wallet() {
        this.walletId = UUID.randomUUID().toString();
        this.balance = BigDecimal.ZERO;
    }

    public static Wallet findByUserId(String userId) {
        return find("userId", userId).firstResult();
    }

    public static Wallet findByWalletId(String walletId) {
        return find("walletId", walletId).firstResult();
    }
} 