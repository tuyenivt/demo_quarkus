package com.example.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class WalletDTO {

    @NotNull
    public String userId;

    @NotNull
    @PositiveOrZero
    public BigDecimal balance;

    public String walletId;

    public WalletDTO() {
    }

    public WalletDTO(String userId, BigDecimal balance, String walletId) {
        this.userId = userId;
        this.balance = balance;
        this.walletId = walletId;
    }
} 