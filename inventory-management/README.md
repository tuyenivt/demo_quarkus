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
docker run -d --name inventory-postgres -e POSTGRES_USER=yourusername -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_DB=inventorydb -p 5432:5432 postgres:17
```

## Start the Application
```shell
./gradlew quarkus:dev
```

## Testing REST APIs

### Create a Product
```shell
curl -X POST http://localhost:8080/products \
     -H "Content-Type: application/json" \
     -d '{
           "sku": "SKU-1001",
           "name": "Wireless Mouse",
           "description": "Ergonomic wireless mouse"
         }'
```

### Create Inventory for the Product created above
```shell
curl -X POST http://localhost:8080/inventories \
     -H "Content-Type: application/json" \
     -d '{
           "product": { "id": 1 },
           "quantity": 50
         }'
```

### Adjust Inventory Quantity
```shell
# Increase stock by 10
curl -X POST http://localhost:8080/inventories/1/adjust \
     -H "Content-Type: application/json" \
     -d '10'
# Decrease stock by 5
curl -X POST http://localhost:8080/inventory/1/adjust \
     -H "Content-Type: application/json" \
     -d '-5'
```

## Testing Real-Time Updates
```javascript
// In browser console, use a WebSocket client to connect to the WebSocket endpoint
let ws = new WebSocket("ws://localhost:8080/ws/inventories");
ws.onmessage = (event) => {
    console.log("Inventory Update: ", event.data);
};
```
