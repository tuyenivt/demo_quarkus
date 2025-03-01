# Notification Service

## Overview
- Handles sending various types of notifications (email, SMS, push) to users based on different events and triggers within an application.
- Meet high-throughput, resilient, and extensible notification service for a healthcare system

## Key Features for POC
- **Multiple Channels**: Email, SMS, Push Notification
- **High Performance & Scale**: 100M requests/day (handle bursts, high concurrency)
- **Idempotency**: Prevent duplicate notifications using unique identifiers
- **Dynamic Rate Limiting**: Throttle and control request flow to avoid overload
- **Resilience**: Circuit breakers to isolate third-party failures

## Docker Commands
```shell
docker run -d --name notification-postgres -e POSTGRES_USER=yourusername -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_DB=notificationdb -p 5432:5432 postgres:17
```

## Start the Application
```shell
./gradlew quarkus:dev
```
