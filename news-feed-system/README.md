# News Feed System

A scalable and personalized news feed system.

## Features

- Content aggregation from multiple sources
- Personalized feed generation based on user preferences
- Real-time updates using Kafka
- Caching with Redis for improved performance
- REST API endpoints
- Reactive programming with SmallRye Mutiny
- Clean architecture and dependency injection

## Prerequisites

- Java 21
- Gradle 8.x
- Docker and Docker Compose
- Postgres 17
- Redis 7
- Apache Kafka 3.x

## Quick Start

Build and run the application:
```bash
./gradlew quarkusDev
```

The application will be available at `http://localhost:8080`

## API Documentation

Once the application is running, you can access:
- Swagger UI: `http://localhost:8080/swagger-ui`
- OpenAPI specification: `http://localhost:8080/openapi`

## Key Endpoints

- `GET /api/v1/feed/{userId}` - Get personalized feed for a user
- `POST /api/v1/feed/{userId}/interact` - Record user interaction with content
- `GET /api/v1/feed/health` - Health check endpoint

## Architecture

The system follows a clean architecture with the following layers:

1. **API Layer**: REST endpoints and request/response handling
2. **Service Layer**: Business logic and orchestration
3. **Repository Layer**: Data access and persistence
4. **Model Layer**: Domain entities and data transfer objects

### Key Components

- **FeedService**: Handles feed generation and personalization
- **ContentService**: Manages content creation and updates
- **UserService**: Handles user management and interactions
- **KafkaService**: Manages real-time updates and event streaming

## Personalization Algorithm

The feed personalization uses a weighted scoring system that considers:

1. Recency (40% weight)
2. Interest matching (30% weight)
3. Social relevance (20% weight)
4. Engagement metrics (10% weight)

## Caching Strategy

- Redis is used to cache pre-computed feeds
- Cache TTL: 15 minutes
- Cache invalidation on content updates

## Monitoring

The application includes:
- Health checks
- Metrics endpoint (`/metrics`)
- Structured logging

## Future Improvements

1. Machine Learning Integration
   - Content recommendation models
   - User behavior prediction
   - A/B testing framework

2. Advanced Features
   - Content moderation
   - Spam detection
   - Trending topics
   - Content scheduling

3. Performance Optimizations
   - Feed pre-computation
   - Advanced caching strategies
   - Database sharding
