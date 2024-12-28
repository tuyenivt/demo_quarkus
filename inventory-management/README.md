# Real-Time Inventory Management System

## Overview
- **Tracks Inventory**: Monitor stock levels of products in real-time.
- **Handles High Throughput**: Manage concurrent inventory updates efficiently.
- **Provides Real-Time Notifications**: Notify stakeholders of inventory changes instantly.

## Key Features for POC
- **Product Management**: CRUD operations for products.
- **Inventory Tracking**: Real-time tracking of stock levels.
- **Real-Time Notifications**: Use WebSockets to broadcast inventory updates.
- **Reactive Database Access**: Efficient handling of database operations using reactive programming.

## Docker Commands
```shell
docker run --name inventory-postgres -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_USER=yourusername -e POSTGRES_DB=inventorydb -p 5432:5432 -d postgres:17
```

## Start the Application
```shell
./gradlew quarkus:dev
```
