# Digital Wallet Service

A Quarkus-based microservice for managing digital wallets and processing transactions.

## Features

- Create and manage digital wallets
- Add funds to wallets
- Make payments from wallets
- Transaction history and reversal
- Secure API with JWT authentication
- Idempotent operations
- Transaction management with rollback support
- OpenAPI documentation

## Prerequisites

- Java 21
- Postgres 17
- Gradle 8.x

## Building and Running

1. Build the project:

```bash
./gradlew build
```

2. Run the service:

```bash
./gradlew quarkusDev
```

## API Documentation

Once the service is running, you can access the OpenAPI documentation at:

```
http://localhost:8080/q/swagger-ui
```

## Using the Java SDK

Add the SDK dependency to your project:

```gradle
implementation 'com.example:digital-wallet-sdk:1.0.0'
```

Example usage:

```java
@Inject
@RestClient
DigitalWalletClient walletClient;

// Create a wallet
WalletDTO wallet = walletClient.createWallet(new WalletDTO("user123", BigDecimal.ZERO, null));

// Add funds
TransactionDTO transaction = walletClient.addFunds(
    wallet.walletId,
    new BigDecimal("100.00"),
    "ref-123"
);

// Make a payment
TransactionDTO payment = walletClient.makePayment(
    wallet.walletId,
    new BigDecimal("50.00"),
    "payment-123"
);
```

## Security

The service uses JWT for authentication and authorization. Users must have either the "admin" or "user" role to access
the API.

## Error Handling

The service returns appropriate HTTP status codes and error messages:

- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 409: Conflict
- 500: Internal Server Error

## Monitoring

The service exposes health and metrics endpoints:

- Health: `http://localhost:8080/q/health`
- Metrics: `http://localhost:8080/q/metrics`
