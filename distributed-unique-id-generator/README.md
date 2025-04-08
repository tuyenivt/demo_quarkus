# Distributed Unique ID Generator

A high-performance, distributed unique ID generator service that implements a Snowflake-like algorithm to generate unique, sortable IDs across multiple nodes in a distributed system.

## Features

- **Distributed Generation**: Generate unique IDs across multiple nodes
- **High Performance**: Capable of generating thousands of IDs per second
- **Sortable IDs**: IDs are time-ordered and sortable
- **Batch Generation**: Generate multiple IDs in a single request
- **Fault Tolerant**: Handles clock drift and node failures
- **Monitoring**: Built-in health checks and metrics
- **Thread-Safe**: Safe for concurrent access

## Technical Details

The ID generator uses a modified Snowflake algorithm with the following bit allocation:
- 41 bits for timestamp (milliseconds since epoch)
- 10 bits for node ID (supports up to 1024 nodes)
- 12 bits for sequence number (4096 IDs per millisecond)

This configuration provides:
- ~69 years of unique IDs
- Support for up to 1024 nodes
- 4096 unique IDs per millisecond per node
- Roughly sortable IDs based on timestamp

## Getting Started

### Prerequisites

- Java 21
- Gradle 8.x

### Building

```bash
./gradlew build
```

### Running

```bash
# Set node ID via environment variable
export NODE_ID=1
./gradlew quarkusDev
```

### API Endpoints

- `GET /api/v1/ids/generate` - Generate a single ID
- `GET /api/v1/ids/generate-batch?count=N` - Generate N IDs (1-1000)
- `GET /health/live` - Health check endpoint
- `GET /metrics` - Prometheus metrics

### Configuration

The service can be configured via `application.yml` or environment variables:

```yaml
id-generator:
  node-id: ${NODE_ID:0}  # Can be overridden by environment variable
  epoch: 1609459200000   # 2021-01-01 00:00:00 UTC
  sequence-bits: 12      # 4096 unique IDs per millisecond
  node-id-bits: 10       # 1024 unique nodes
  timestamp-bits: 41     # ~69 years of timestamps
```

## Testing

Run the test suite:

```bash
./gradlew test
```

## Monitoring

The service exposes the following metrics:
- `generateIdCount`: Number of single ID generation requests
- `generateIdTimer`: Time taken to generate a single ID
- `generateBatchCount`: Number of batch ID generation requests
- `generateBatchTimer`: Time taken to generate a batch of IDs

## Production Deployment

For production deployment:
1. Set appropriate node IDs for each instance
2. Configure proper logging and monitoring
3. Consider using a load balancer for high availability
4. Monitor system metrics and health checks
