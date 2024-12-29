# Healthcare Appointment and Records Management System

## Overview
- **Manages Appointments**: Schedule, update, and cancel appointments between patients and healthcare providers.
- **Handles Medical Records**: Securely store and manage patient medical records.
- **Ensures Data Security**: Implement robust authentication and authorization mechanisms to protect sensitive data.
- **Supports FHIR Standards**: Facilitate interoperability by adhering to Fast Healthcare Interoperability Resources (FHIR) standards.
- **Provides Real-Time Updates**: Notify users of appointment changes or updates in real-time.

## Key Features for POC
- **User Management**: CRUD operations for patients and healthcare providers.
- **Appointment Scheduling**: Create, view, update, and cancel appointments.
- **Medical Records Management**: Securely store and retrieve patient medical records.
- **Authentication & Authorization**: Secure APIs using JWT-based OAuth2.
- **FHIR Compliance**: Implement FHIR standards for data exchange.
- **Real-Time Notifications**: Use WebSockets to broadcast appointment updates.
- **Reactive Database Access**: Efficient handling of database operations using reactive programming.

## Docker Commands
```shell
docker run -d --name healthcare-postgres -e POSTGRES_USER=yourusername -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_DB=healthcaredb -p 5432:5432 postgres:17
docker run -d --name keycloak -p 8081:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.0.7 start-dev
```

## Keycloak Realm Configuration
1. Login to Keycloak with user `admin` and password `admin`
2. Go to create realm page: http://localhost:8081/admin/master/console/#/master/add-realm
   Crete realm with name `HealthcareRealm`
3. Go to create client page: http://localhost:8081/admin/master/console/#/HealthcareRealm/clients/add-client
   Create client with Client ID `healthcare-client` with `Standard flow` and `Direct access grants` are enabled
   Go to Client Credentials to get Client Secret and replace to `quarkus.oidc.credentials.secret` in `application.properties`
4. Go to create role page: http://localhost:8081/admin/master/console/#/HealthcareRealm/roles/new
   Create role with name `PATIENT`, `DOCTOR`, `ADMIN`
5. Go to create group page: http://localhost:8081/admin/master/console/#/HealthcareRealm/groups
   Create groups with name `PATIENT GROUP`, `DOCTOR GROUP`, `ADMIN GROUP`
   PATIENT GROUP roles assigned: PATIENT
   DOCTOR GROUP roles assigned: DOCTOR, PATIENT
   ADMIN GROUP roles assigned: ADMIN, DOCTOR, PATIENT
6. Go to create user page: http://localhost:8081/admin/master/console/#/HealthcareRealm/users/add-user
   Create user with username `patient`, `doctor`, `admin` and assign to respective groups

## Start the Application
```shell
./gradlew quarkus:dev
```

## Testing REST APIs

### Create a Patient
```shell
curl -X POST http://localhost:8080/users \
     -H "Content-Type: application/json" \
     -d '{
           "username": "patient1",
           "password": "123456",
           "role": "PATIENT",
           "fullName": "Patient 1",
           "email": "patient1@example.com"
         }'
```

### Create a Doctor
```shell
curl -X POST http://localhost:8080/users \
     -H "Content-Type: application/json" \
     -d '{
           "username": "doctor1",
           "password": "123456",
           "role": "DOCTOR",
           "fullName": "Doctor 1",
           "email": "doctor1@example.com"
         }'
```

### Create an Appointment for the Patient and Doctor created above
```shell
curl -X POST http://localhost:8080/appointments \
     -H "Content-Type: application/json" \
     -d '{
           "patient": { "id": 1 },
           "doctor": { "id": 2 },
           "appointmentTime": "2024-01-15T10:00:00",
           "status": "SCHEDULED",
           "notes": "Health Screening"
         }'
```

### Retrieve All Appointments
```shell
curl http://localhost:8080/appointments
```

## Testing Real-Time Updates
```javascript
// In browser console, use a WebSocket client to connect to the WebSocket endpoint
let ws = new WebSocket("ws://localhost:8080/ws/notifications");
ws.onmessage = (event) => {
    console.log("Notification:", event.data);
};
```

## Accessing FHIR Endpoint
```shell
curl http://localhost:8080/fhir/Patient/1
```
