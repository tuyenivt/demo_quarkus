package com.example.sdk;

import com.example.dto.TransactionDTO;
import com.example.dto.WalletDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.math.BigDecimal;

@RegisterRestClient
@RegisterClientHeaders
@Path("/api/wallets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DigitalWalletClient {

    @POST
    WalletDTO createWallet(WalletDTO walletDTO);

    @GET
    @Path("/{walletId}")
    WalletDTO getWallet(@PathParam("walletId") String walletId);

    @POST
    @Path("/{walletId}/add-funds")
    TransactionDTO addFunds(
            @PathParam("walletId") String walletId,
            @QueryParam("amount") BigDecimal amount,
            @QueryParam("referenceId") String referenceId);

    @POST
    @Path("/{walletId}/make-payment")
    TransactionDTO makePayment(
            @PathParam("walletId") String walletId,
            @QueryParam("amount") BigDecimal amount,
            @QueryParam("referenceId") String referenceId);

    @POST
    @Path("/transactions/{transactionId}/reverse")
    TransactionDTO reverseTransaction(@PathParam("transactionId") String transactionId);
} 