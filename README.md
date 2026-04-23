# smart-campus-api

- Student: Sahan Adithya
- Student ID: w2120593
- Module: 5COSC022C.2 Client-Server Architectures

A JAX-RS based RESTful API for managing smart campus rooms, sensors, and sensor readings using in-memory data structures only. This project was developed for the CSA coursework and follows the requirement to use **JAX-RS only** with **no database** and **no Spring Boot**.

## Overview of API Design

The API is designed around four main resource areas:

- **Discovery**
  - Base endpoint to confirm the API is running.

- **Rooms**
  - Create rooms
  - List rooms
  - Get a room by ID
  - Delete a room
  - Prevent deletion when sensors are attached

- **Sensors**
  - Create sensors
  - List all sensors
  - Filter sensors by type
  - Get a sensor by ID
  - Validate that sensors can only be attached to an existing room

- **Sensor Readings**
  - Get reading history for a sensor
  - Add a new reading to a sensor
  - Automatically update the parent sensor's `currentValue`
  - Block readings when sensor status is `MAINTENANCE`

- **Error Handling**
  - Returns proper HTTP status codes such as `404`, `409`, `422`, `403`, and `500`
  - Includes global exception handling for clean API responses

## Technology Stack

- Java
- JAX-RS (`javax.ws.rs`)
- Maven
- Apache NetBeans
- Apache Tomcat
- Postman

## Project Structure

```text
com.smartcampus.api
├── JAXRSConfiguration.java
├── exception
├── model
│   ├── Room.java
│   ├── Sensor.java
│   ├── SensorReading.java
│   └── ApiError.java
├── resource
│   ├── DiscoveryResource.java
│   ├── RoomResource.java
│   ├── SensorResource.java
│   ├── SensorReadingResource.java
│   └── DebugResource.java
└── store
    └── InMemoryStore.java
```

## Base URL

```text
http://localhost:8080/SmartCampusApi/api/v1
```

## How to Build and Run the Project

### Option 1: Run using NetBeans

1. Clone the repository:
   ```bash
   git clone https://github.com/SahanAdithya/smart-campus-api.git
   ```

2. Open NetBeans.

3. Select:
   - `File` → `Open Project`
   - choose the `smart-campus-api` folder

4. Wait for Maven dependencies to load.

5. Right-click the project and select:
   - `Clean and Build`

6. Right-click the project again and select:
   - `Run`

7. Make sure the server starts successfully.

8. Open this URL in a browser or Postman to confirm the API is running:
   ```text
   http://localhost:8080/SmartCampusApi/api/v1
   ```


## Main API Endpoints

### Discovery
- `GET /api/v1`

### Rooms
- `GET /api/v1/rooms`
- `POST /api/v1/rooms`
- `GET /api/v1/rooms/{roomId}`
- `DELETE /api/v1/rooms/{roomId}`

### Sensors
- `GET /api/v1/sensors`
- `GET /api/v1/sensors?type=CO2`
- `POST /api/v1/sensors`
- `GET /api/v1/sensors/{sensorId}`
- `DELETE /api/v1/sensors/{sensorId}`

### Sensor Readings
- `GET /api/v1/sensors/{sensorId}/readings`
- `POST /api/v1/sensors/{sensorId}/readings`

### Debug
- `GET /api/v1/debug/error`

## Sample curl Commands

### 1. Discovery endpoint
```bash
curl -i http://localhost:8080/SmartCampusApi/api/v1
```

### 2. Create a room
```bash
curl -i -X POST http://localhost:8080/SmartCampusApi/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 80
  }'
```

### 3. Get all rooms
```bash
curl -i http://localhost:8080/SmartCampusApi/api/v1/rooms
```

### 4. Create a valid sensor
```bash
curl -i -X POST http://localhost:8080/SmartCampusApi/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "CO2-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 420.0,
    "roomId": "LIB-301"
  }'
```

### 5. Filter sensors by type
```bash
curl -i "http://localhost:8080/SmartCampusApi/api/v1/sensors?type=CO2"
```

### 6. Get sensor readings
```bash
curl -i http://localhost:8080/SmartCampusApi/api/v1/sensors/CO2-001/readings
```

### 7. Add a reading to a sensor
```bash
curl -i -X POST http://localhost:8080/SmartCampusApi/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{
    "value": 455.7
  }'
```

### 8. Check updated parent sensor value
```bash
curl -i http://localhost:8080/SmartCampusApi/api/v1/sensors/CO2-001
```

### 9. Try deleting a room with sensors attached
```bash
curl -i -X DELETE http://localhost:8080/SmartCampusApi/api/v1/rooms/LIB-301
```

### 10. Trigger global error handling
```bash
curl -i http://localhost:8080/SmartCampusApi/api/v1/debug/error
```

## Expected Behaviour Summary

- Creating a room with a new ID returns `201 Created`
- Creating a duplicate room returns `409 Conflict`
- Creating a sensor for a non-existing room returns `422 Unprocessable Entity`
- Filtering sensors by type returns `200 OK`
- Deleting a room with linked sensors returns `409 Conflict`
- Adding a reading updates the parent sensor's `currentValue`
- Posting a reading to a sensor in `MAINTENANCE` returns `403 Forbidden`
- Debug endpoint returns `500 Internal Server Error` with a clean JSON response

## Testing Order Used in Postman

1. Discovery endpoint
2. Get initial rooms list
3. Create room `DEL-101`
4. Get room `DEL-101`
5. Delete room `DEL-101`
6. Confirm `DEL-101` is deleted
7. Create room `LIB-301`
8. Get room `LIB-301`
9. Duplicate room check
10. Invalid sensor room validation
11. Create valid CO2 sensor
12. Create valid temperature sensor
13. Get all sensors
14. Filter sensors by CO2
15. Filter sensors by Temperature
16. Attempt to delete room with attached sensors
17. Get CO2 sensor readings
18. Add first CO2 reading
19. Get readings again
20. Confirm parent sensor updated
21. Add second CO2 reading
22. Confirm sensor updated again
23. Create maintenance sensor
24. Confirm maintenance status
25. Attempt to post reading to maintenance sensor
26. Trigger global 500 error




