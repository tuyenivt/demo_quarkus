package com.example.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction extends PanacheEntity {

    @NotNull
    @Column(unique = true)
    public String transactionId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    public Wallet wallet;

    @NotNull
    @Column(precision = 19, scale = 4)
    public BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    public TransactionType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    public TransactionStatus status;

    @NotNull
    public LocalDateTime timestamp;

    public String referenceId;

    public String description;

    public Transaction() {
        this.transactionId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    public static Transaction findByTransactionId(String transactionId) {
        return find("transactionId", transactionId).firstResult();
    }

    public static Transaction findByReferenceId(String referenceId) {
        return find("referenceId", referenceId).firstResult();
    }
}
