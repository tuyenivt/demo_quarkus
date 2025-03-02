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
docker run -d --name notification-postgres -e POSTGRES_USER=yourusername -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_DB=notificationdb -p 5432:5432 postgres:17.4
docker run -d --name notification-rabbitmq -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest -p 5672:5672 -p 15672:15672 rabbitmq:4.0-management
docker run -d --name notification-redis -p 6379:6379 redis:7.4-alpine
```

## Start the Application
```shell
./gradlew quarkus:dev
```

## Testing REST APIs

### Send a notification
```shell
curl -X POST http://localhost:8080/notifications \
     -H "Content-Type: application/json" \
     -d '{
           "requestId": "b4618a21-9fff-4ee8-8a11-cfb53dcea08d",
           "recipients": ["abc@example.com"],
           "message": "Your appointment is confirmed.",
           "channel": "EMAIL"
         }'
```
