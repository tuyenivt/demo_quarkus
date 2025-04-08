package com.example.service;

import com.example.model.Transaction;
import com.example.model.TransactionStatus;
import com.example.model.TransactionType;
import com.example.model.Wallet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.math.BigDecimal;

@ApplicationScoped
public class WalletService {

    private static final Logger LOG = Logger.getLogger(WalletService.class);

    @Transactional
    public Wallet createWallet(String userId) {
        Wallet existingWallet = Wallet.findByUserId(userId);
        if (existingWallet != null) {
            throw new WebApplicationException("Wallet already exists for user: " + userId,
                    Response.Status.CONFLICT);
        }

        Wallet wallet = new Wallet();
        wallet.userId = userId;
        wallet.persist();

        LOG.info("Created new wallet for user: " + userId);
        return wallet;
    }

    @Transactional
    public Transaction addFunds(String walletId, BigDecimal amount, String referenceId) {
        Wallet wallet = Wallet.findByWalletId(walletId);
        if (wallet == null) {
            throw new WebApplicationException("Wallet not found: " + walletId,
                    Response.Status.NOT_FOUND);
        }

        // Check for duplicate transaction
        if (referenceId != null) {
            Transaction existing = Transaction.findByReferenceId(referenceId);
            if (existing != null) {
                return existing;
            }
        }

        Transaction transaction = new Transaction();
        transaction.wallet = wallet;
        transaction.amount = amount;
        transaction.type = TransactionType.CREDIT;
        transaction.referenceId = referenceId;
        transaction.description = "Adding funds to wallet";

        try {
            wallet.balance = wallet.balance.add(amount);
            transaction.status = TransactionStatus.COMPLETED;
            transaction.persist();
            wallet.persist();

            LOG.info("Added funds to wallet: " + walletId + ", amount: " + amount);
            return transaction;
        } catch (Exception e) {
            transaction.status = TransactionStatus.FAILED;
            transaction.persist();
            LOG.error("Failed to add funds to wallet: " + walletId, e);
            throw new WebApplicationException("Failed to add funds",
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Transaction makePayment(String walletId, BigDecimal amount, String referenceId) {
        Wallet wallet = Wallet.findByWalletId(walletId);
        if (wallet == null) {
            throw new WebApplicationException("Wallet not found: " + walletId,
                    Response.Status.NOT_FOUND);
        }

        // Check for duplicate transaction
        if (referenceId != null) {
            Transaction existing = Transaction.findByReferenceId(referenceId);
            if (existing != null) {
                return existing;
            }
        }

        if (wallet.balance.compareTo(amount) < 0) {
            throw new WebApplicationException("Insufficient funds",
                    Response.Status.BAD_REQUEST);
        }

        Transaction transaction = new Transaction();
        transaction.wallet = wallet;
        transaction.amount = amount;
        transaction.type = TransactionType.DEBIT;
        transaction.referenceId = referenceId;
        transaction.description = "Payment from wallet";

        try {
            wallet.balance = wallet.balance.subtract(amount);
            transaction.status = TransactionStatus.COMPLETED;
            transaction.persist();
            wallet.persist();

            LOG.info("Processed payment from wallet: " + walletId + ", amount: " + amount);
            return transaction;
        } catch (Exception e) {
            transaction.status = TransactionStatus.FAILED;
            transaction.persist();
            LOG.error("Failed to process payment from wallet: " + walletId, e);
            throw new WebApplicationException("Failed to process payment",
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Wallet getWallet(String walletId) {
        Wallet wallet = Wallet.findByWalletId(walletId);
        if (wallet == null) {
            throw new WebApplicationException("Wallet not found: " + walletId,
                    Response.Status.NOT_FOUND);
        }
        return wallet;
    }

    @Transactional
    public Transaction reverseTransaction(String transactionId) {
        Transaction transaction = Transaction.findByTransactionId(transactionId);
        if (transaction == null) {
            throw new WebApplicationException("Transaction not found: " + transactionId,
                    Response.Status.NOT_FOUND);
        }

        if (transaction.status != TransactionStatus.COMPLETED) {
            throw new WebApplicationException("Cannot reverse non-completed transaction",
                    Response.Status.BAD_REQUEST);
        }

        Wallet wallet = transaction.wallet;
        Transaction reversal = new Transaction();
        reversal.wallet = wallet;
        reversal.amount = transaction.amount;
        reversal.type = transaction.type == TransactionType.CREDIT ?
                TransactionType.DEBIT : TransactionType.CREDIT;
        reversal.referenceId = "REV-" + transaction.transactionId;
        reversal.description = "Reversal of transaction: " + transaction.transactionId;

        try {
            if (reversal.type == TransactionType.DEBIT) {
                if (wallet.balance.compareTo(transaction.amount) < 0) {
                    throw new WebApplicationException("Insufficient funds for reversal",
                            Response.Status.BAD_REQUEST);
                }
                wallet.balance = wallet.balance.subtract(transaction.amount);
            } else {
                wallet.balance = wallet.balance.add(transaction.amount);
            }

            transaction.status = TransactionStatus.REVERSED;
            reversal.status = TransactionStatus.COMPLETED;

            transaction.persist();
            reversal.persist();
            wallet.persist();

            LOG.info("Reversed transaction: " + transactionId);
            return reversal;
        } catch (Exception e) {
            reversal.status = TransactionStatus.FAILED;
            reversal.persist();
            LOG.error("Failed to reverse transaction: " + transactionId, e);
            throw new WebApplicationException("Failed to reverse transaction",
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
} 