# Real-Time Chat System with Quarkus

A real-time chat system that implements modern messaging features with a focus on scalability, reliability, and real-time communication.

## Features

- Real-time messaging using WebSockets
- User presence management (online/offline status)
- Message history and persistence
- Unread message tracking
- File and media sharing support
- Scalable architecture with Redis for pub/sub
- Postgres for data persistence
- REST API for user and message management
- Security features (authentication, authorization)

## Architecture

The system follows a microservices-inspired architecture with the following components:

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│  WebSocket      │◄───►│  Redis Pub/Sub  │◄───►│  Other Instances│
│  Endpoint       │     │                 │     │                 │
│                 │     │                 │     │                 │
└────────┬────────┘     └─────────────────┘     └─────────────────┘
         │
         ▼
┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │
│  Message        │◄───►│  PostgreSQL     │
│  Service        │     │  Database       │
│                 │     │                 │
└────────┬────────┘     └─────────────────┘
         │
         ▼
┌─────────────────┐
│                 │
│  REST API       │
│  Endpoints      │
│                 │
└─────────────────┘
```

### Key Components

1. **WebSocket Endpoint**: Handles real-time communication between clients
2. **Redis Pub/Sub**: Enables message broadcasting across multiple instances
3. **Message Service**: Manages message processing and persistence
4. **User Service**: Handles user management and presence
5. **Postgres**: Stores user data and message history
6. **REST API**: Provides endpoints for user management and message history

## Prerequisites

- Java 21
- Gradle 8.x
- Postgres 17+
- Redis 7.x
- Docker (optional, for containerized deployment)

## Setup

1. **Configure Postgres**
   - Create a database named `chatsystem`
   - Update the database configuration in `application.properties`

2. **Configure Redis**
   - Install and start Redis server
   - Update Redis configuration in `application.properties`

3. **Build the application**
```bash
./gradlew build
```

4. **Run the application**
```bash
./gradlew quarkusDev
```

## API Endpoints

### User Management
- `POST /api/users/register` - Register a new user
- `GET /api/users/online` - Get list of online users
- `GET /api/users/search?q={query}` - Search users
- `GET /api/users/{username}` - Get user details
- `DELETE /api/users/{username}` - Delete user

### Message Management
- `GET /api/messages/history/{username1}/{username2}` - Get chat history
- `GET /api/messages/unread/{username}` - Get unread messages
- `POST /api/messages/mark-read/{username1}/{username2}` - Mark messages as read
- `POST /api/messages/send` - Send a new message

### WebSocket
- `ws://localhost:8080/chat/{username}` - WebSocket endpoint for real-time chat

## WebSocket Protocol

### Message Format
```json
{
    "sender": "username",
    "receiver": "username",
    "content": "message content",
    "messageType": "TEXT|IMAGE|VIDEO|FILE",
    "fileUrl": "url-to-file"
}
```

### Status Updates
```json
{
    "type": "status",
    "username": "username",
    "online": true|false
}
```

## Security

- WebSocket connections are secured with TLS
- User authentication is required for all operations
- Passwords are hashed before storage
- Rate limiting is implemented for API endpoints

## Scaling

The system can be scaled horizontally by:
1. Running multiple instances of the application
2. Using Redis for message broadcasting
3. Configuring Postgres for read replicas
4. Implementing load balancing

## Monitoring

- Health checks available at `/q/health`
- Metrics available at `/q/metrics`
- Logging configured for debugging and monitoring

## Future Improvements

1. Implement end-to-end encryption for messages
2. Add support for group chats
3. Implement message reactions and replies
4. Add support for voice and video calls
5. Implement message search functionality
6. Add support for message editing and deletion
7. Implement message read receipts
8. Add support for message translation
9. Implement message archiving
10. Add support for message scheduling
