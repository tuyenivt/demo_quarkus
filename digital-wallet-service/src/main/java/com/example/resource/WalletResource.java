package com.example.resource;

import com.example.dto.WalletDTO;
import com.example.model.Wallet;
import com.example.service.WalletService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.math.BigDecimal;

@Path("/api/wallets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Wallet Management", description = "Operations for managing digital wallets")
public class WalletResource {

    @Inject
    WalletService walletService;

    @POST
    @RolesAllowed("admin")
    @Operation(summary = "Create a new wallet")
    @APIResponse(responseCode = "201", description = "Wallet created successfully")
    @APIResponse(responseCode = "409", description = "Wallet already exists")
    public Response createWallet(@Valid WalletDTO walletDTO) {
        Wallet wallet = walletService.createWallet(walletDTO.userId);
        return Response.status(Response.Status.CREATED)
                .entity(mapToDTO(wallet))
                .build();
    }

    @GET
    @Path("/{walletId}")
    @RolesAllowed({"admin", "user"})
    @Operation(summary = "Get wallet details")
    @APIResponse(responseCode = "200", description = "Wallet details retrieved successfully")
    @APIResponse(responseCode = "404", description = "Wallet not found")
    public Response getWallet(@PathParam("walletId") String walletId) {
        Wallet wallet = walletService.getWallet(walletId);
        return Response.ok(mapToDTO(wallet)).build();
    }

    @POST
    @Path("/{walletId}/add-funds")
    @RolesAllowed({"admin", "user"})
    @Operation(summary = "Add funds to wallet")
    @APIResponse(responseCode = "200", description = "Funds added successfully")
    @APIResponse(responseCode = "404", description = "Wallet not found")
    public Response addFunds(
            @PathParam("walletId") String walletId,
            @QueryParam("amount") BigDecimal amount,
            @QueryParam("referenceId") String referenceId) {

        var transaction = walletService.addFunds(walletId, amount, referenceId);
        return Response.ok(transaction).build();
    }

    @POST
    @Path("/{walletId}/make-payment")
    @RolesAllowed({"admin", "user"})
    @Operation(summary = "Make a payment from wallet")
    @APIResponse(responseCode = "200", description = "Payment processed successfully")
    @APIResponse(responseCode = "400", description = "Insufficient funds")
    @APIResponse(responseCode = "404", description = "Wallet not found")
    public Response makePayment(
            @PathParam("walletId") String walletId,
            @QueryParam("amount") BigDecimal amount,
            @QueryParam("referenceId") String referenceId) {

        var transaction = walletService.makePayment(walletId, amount, referenceId);
        return Response.ok(transaction).build();
    }

    @POST
    @Path("/transactions/{transactionId}/reverse")
    @RolesAllowed("admin")
    @Operation(summary = "Reverse a transaction")
    @APIResponse(responseCode = "200", description = "Transaction reversed successfully")
    @APIResponse(responseCode = "400", description = "Cannot reverse transaction")
    @APIResponse(responseCode = "404", description = "Transaction not found")
    public Response reverseTransaction(@PathParam("transactionId") String transactionId) {
        var reversal = walletService.reverseTransaction(transactionId);
        return Response.ok(reversal).build();
    }

    private WalletDTO mapToDTO(Wallet wallet) {
        return new WalletDTO(
                wallet.userId,
                wallet.balance,
                wallet.walletId
        );
    }
} 