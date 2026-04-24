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


# Conceptual Report Answers


<h2>Chapter 1: Setup &amp; Discovery</h2>

<h3>1. Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.</h3>
<p>The default is that JAX-RS resource classes are request-scoped, so the runtime will create a new object each time an HTTP request is received, and then destroy it as soon as it generates a response. Any state in the instance fields of the resource is volatile unless explicitly defined as a singleton and will not be available between requests.</p>
<p>To have an in-memory API (say a room or sensor data API), you have to put shared data in a different store class that is independent of the lifecycle of the resource. Nonetheless, the transfer of the data to a common store brings about concurrency issues. Since concurrent access and/or updates to the same data structures are possible by several requests, the implementation must be thread-safe.</p>
<p>To avoid race conditions, you should:</p>
<ul>
  <li>Use thread-safe collections, like ConcurrentHashMap.</li>
  <li>Introduce compound operations (e.g. adding a sensor and at the same time changing the status of a room).</li>
</ul>
<p>In the absence of these protections, the system can be susceptible to lost updates, inconsistent data, or partially implemented changes. Synchronization is needed to make multi-step workflows atomic and reliable in a multi-threaded environment.</p>

<h3>2. Why is the provision of Hypermedia (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?</h3>
<p>Hypermedia is another advanced principle of REST, which enables a server to dynamically direct clients. The API allows itself to be self-documented by embedding links in the response to advertise allowed next steps, eliminating the need to code URIs by hand, or over-depend on external documentation.</p>
<p>This method has a number of advantages:</p>
<ul>
  <li>Reduced Coupling: It minimizes client code dependency and server routing dependency. In case there is a change in the API structure, clients who are using the links provided by the server will be resilient but the ones with fixed strings will be broken.</li>
  <li>Increased Discoverability: Developers are able to explore available actions and resources involved in the response itself, which makes the API an active guide, as opposed to a passive endpoint.</li>
</ul>
<p>Although a static documentation remains useful in the context of onboarding, hypermedia makes the API reflect what it is like in reality in real-time. In case of a Smart Campus API, it would imply that a client is capable of navigating through a basic endpoint to each particular room, sensor and reading by just clicking on the links that the server offers. This allows the system to be more flexible, simpler to evolve and naturally more of an intuitive system to developers to integrate.</p>

<hr>

<h2>Chapter 2: Room Management</h2>

<h3>3. When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.</h3>
<p>The decision to send back room IDs or room objects will entail a trade-off between the efficiency of the network and the convenience of developers.</p>
<p><strong>Returning Room IDs:</strong></p>
<ul>
  <li>ID-only mitigates the bandwidth and serialization costs on the server by minimizing the payload. This works well with large-scale systems. Nevertheless, this method can easily result in under-fetching when the client has to do several more network round-trips to fetch certain information (such as room name or room capacity). This adds to the latency, and the processing load moves to the consumer.</li>
</ul>

<p><strong>Returning Full Objects:</strong></p>
<ul>
  <li>The additional response size is compensated by the fact that the API is much more user-friendly with the inclusion of full data objects. Clients are able to make listings in real time without subsequent lookups. In small-scale applications, like a coursework project, the ease of this solution often supersedes the small performance benefits of a small payload.</li>
</ul>
<p>The design option will be based on the scale and use. The ID-only or summary views can be useful in high-traffic production environments where bandwidth is limited, and the number of resources is large (in the thousands). However, in the case of smaller educational APIs, full objects are a better developer experience, and a simpler interaction model.</p>

<h3>4. Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.</h3>
<p>The DELETE operation in the REST architecture is idempotent since multiple requests will lead to the same final state of the server. There is often a misapprehension that idempotency means using the same response codes; actually, it only demands that the state of the system does not change once it has been successfully applied in the beginning.</p>
<p>Lifecycle of a DELETE with idempotence.</p>
<p>Take the case of a client who has deleted a given room:</p>
<ul>
  <li>Request to initial: The server removes the room and sends a success code (e.g., 200 OK or 204 No Content). The resource has been lost.</li>
  <li>Further Requests: The room is no longer available, so the server sends a 404 Not Found.</li>
</ul>
<p>This is a keystone to distributed systems. Retry logic can be safely used in both clients and proxies in settings where network timeouts or drops in connections are frequent.</p>
<p>Idempotent design guarantees that in case a DELETE request is sent twice by accident because of a "retry" the system will remain consistent instead of causing errors or unforeseen behaviors. This enables the API to be more resilient and predictable to developers.</p>

<hr>

<h2>Chapter 3: Sensors &amp; Filtering</h2>

<h3>5. We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?</h3>
<p>The @Consumes(MediaType.APPLICATION_JSON) annotation creates a hard-to-negotiate contract: the resource method will accept application/json request bodies only. When a client transmits a different format, e.g. application/xml, the JAX-RS framework will recognize the discrepancy in content negotiation prior to any application logic being invoked. In case no appropriate message body reader is discovered to service the declared Content-Type, the framework declines the request and usually responds with an HTTP 415 Unsupported Media Type.</p>
<p>Handling this at the framework level offers a number of benefits:</p>
<ul>
  <li>Enforced API Contracts: This makes sure that only anticipated formats are handled.</li>
  <li>Parsing Error Prevention: Blocks invalid data at the high application levels where it can lead to undesirable unknown failures.</li>
  <li>Standardized Feedback: Provides clients with instant standards-based rejection reasons.</li>
</ul>
<p>This is done to make sure that your resource code is business logic-oriented instead of focusing on the validation of a manual format.</p>

<h3>6. You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?</h3>
<p>Preferably, query parameters should be used to filter since it is a restricted perspective of a collection and not a specific resource. Sensors such as /sensors?type=CO2 intuitively describe: fetch the sensors collection, filtered by type.</p>
<p>Putting filters in the URI path (e.g., /sensors/type/CO2) makes the design inflexible, route-intensive, and not very scalable. Path-based routing is uncontrollable as the number of criteria increases. Query parameters enable composition in a graceful manner, enabling clients to put together multiple conditions, e.g. ?type=CO2&amp;status=ACTIVE or query?Query parameters promote composition in a beautiful way, letting the clients combine several conditions, e.g. query?type=CO2&amp;status=ACTIVE.roomId=LIB-301.</p>
<p>Key Advantages:</p>
<ul>
  <li>Extensibility: Best used to search, sort and page.</li>
  <li>Clarity: Maintains the resource hierarchy in order to express search conditions.</li>
  <li>Predictability: Aligns with standard REST conventions and developer expectations.</li>
</ul>

<hr>


<h2>Chapter 4: Sub-Resources</h2>

<h3>7. Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?</h3>
<p>Sub-Resource Locator pattern promotes modularity through assigning specific tasks to specialised classes. Sensors and readings in a Smart Campus API are connected with each other, but they are used in different ways: one of them is sensor metadata, and the other one is historical data.</p>
<p>Distributing these issues avoids classes of Gods and controllers are on track. The main resource delegates reading specific logic to a sub-resource class instead of having one huge class that manages all of the nested paths. This isolation offers great enhancement in readability and testability.</p>
<p>Key Benefits:</p>
<ul>
  <li>Scalable Evolution: Add additional features such as pagination or advanced filtering to readings in the sub-resource class without necessarily cluttering the parent sensor resource.</li>
  <li>Separation of Concerns: classes are small and are specialized in one task.</li>
  <li>Hierarchical Clarity: Deals with architectural complexity by ensuring a logical hierarchy of URIs, and strict isolation of implementation issues.</li>
</ul>

<hr>

<h2>Chapter 5: Error Handling &amp; Logging</h2>

<h3>8. Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?</h3>
<p>The more desirable option in the case of a request that can be syntactically correct but can be rejected on business logic grounds (such as a valid JSON sensor object with a roomId that does not exist) is HTTP 422 Unprocessable Entity.</p>
<p>It is not advisable to use 404 as it is normally an indication of an absent URI. Instead, 422 affirms that the server knew about the request structure but disapproved its semantic content.</p>
<p>This difference focuses API feedback. Developers are able to quickly detect that the format is right, and this leaves the logic as the only suspect. It makes sure your API is understandable and does not give your clients a wild goose chase.</p>

<h3>9. From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?</h3>
<p>Sharing unfiltered Java stack traces can be dangerous. They include critical implementation details such as package names, line numbers, and library versions - in effect, a blueprint of your system.</p>
<p><strong>The Attacker's Blueprint</strong></p>
<p>Using these traces, hackers can:</p>
<ul>
  <li>Figure out the tech stack: Target frameworks with known vulnerabilities.</li>
  <li>Understand business logic: Deduce database schema or validation rules to inform attacks.</li>
  <li>Expose internal paths: Reveal server-side directory structures and information.</li>
</ul>
<p><strong>Best Practice: Sanitized Responses</strong> The API needs to produce sanitized responses. A simple JSON body with a status code, a brief error identifier and a safe generic message is enough for legitimate users without "giving away the farm." Security through obscurity isn't just "best practice" - it is an important line of defence that makes your system harder to attack.</p>


<h3>10. Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?</h3>
<p>Logging is a cross cutting issue as it is applicable in numerous endpoints as opposed to a particular business operation. JAX-RS filters are suitable to such a liability since they are able to intercept requests and responses at a central point. This enables the use of logging behaviour to be specified only once and then uniformly across all resource methods.</p>
<p>When logging statements are manually coded within the individual resource methods, the code would soon turn out to be tedious and hard to maintain. Developers can change their mind and forget to record new endpoints, may have mixed message formats, and may be tempted to add business logic implementation details to business logic. The filters prevent such duplication and ensure resources methods are concerned with domain behaviour like room creation or sensor validation.</p>
<p>Later improvements are also easy with centralised logging. When changes are needed, like the addition of timing information, correlation identifiers, or masking sensitive fields, they can be made in a single location instead of having to edit all of the controller methods separately. This results in cleaner code, increased consistency, and reduced long-term maintenance.</p>
