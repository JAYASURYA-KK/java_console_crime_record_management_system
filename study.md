# Crime Record Management System - Study Guide

## Overview
This document explains the architecture, main components, Java logic, database integration, WebSocket flow, and Maven usage of the project located at `e:\inteconsole\console`.

- Console application entry: `com.crimemanagement.CrimeManagementApplication`
- Web application entry: `com.crimemanagement.CrimeManagementWebApplication`
- Persistence: MongoDB via Mongo Java driver (sync)
- Web UI: Spring Boot MVC + Thymeleaf (`templates/crime-records.html`)
- Real-time updates: STOMP over WebSocket with SockJS
- Build/Run: Maven (Spring Boot plugins + exec plugin)

---

## Architecture at a Glance
- `config`: App wiring for database and WebSocket message broker.
- `controller`: Web MVC endpoints for UI and resources.
- `model`: Plain Java objects (POJOs) representing domain entities.
- `service`: Core business logic for authentication, users, crimes, search, WebSocket notifications, and web launch/orchestration.
- `ui`: Console UI menus and flows.
- `util`: Input validation, ASCII, error handling, helpers.
- `resources`: App properties, Thymeleaf templates, and logging configuration.

The system supports two UIs over the same data/services:
- A console menu-driven UI (`MenuManager`) launched by `CrimeManagementApplication`.
- A Spring Boot web UI (`crime-records.html`) launched by `CrimeManagementWebApplication` (usually started by `WebLauncherService`).

MongoDB stores crimes and users; services maintain in-memory views (ArrayList/Stack and indexes) to optimize display and search.

---

## Entry Points

### `com.crimemanagement.CrimeManagementApplication`
- Initializes MongoDB via `DatabaseConfig.connect()`.
- Starts the console UI using `ui.MenuManager.start()`.
- Ensures clean shutdown via `DatabaseConfig.disconnect()` in `finally`.

### `com.crimemanagement.CrimeManagementWebApplication`
- Standard Spring Boot application entry for the web tier.
- Bootstraps MVC, WebSocket messaging, and component-scanned services.

---

## Configuration

### `config.DatabaseConfig`
- Manages Mongo connection using the sync driver.
- Provides accessors for `users` and `crimes` collections.
- On connect: ensures collections exist and seeds a default admin user (`admin/admin123`).
- Public API:
  - `connect()` / `disconnect()`
  - `getDatabase()`
  - `getUsersCollection()`
  - `getCrimesCollection()`

### `config.WebSocketConfig`
- Enables STOMP over WebSocket (`@EnableWebSocketMessageBroker`).
- Configures simple broker for `/topic` and `/queue`, app prefix `/app`, user prefix `/user`.
- Registers SockJS endpoint at `/websocket-crime` with heartbeat.

### `config.ServiceConfig`
- Enables `@ComponentScan("com.crimemanagement.service")` so Spring can discover `@Service` beans like `WebSocketService`.

---

## Models

### `model.Crime`
- Fields: `id`, `name`, `city`, `crimeType`, `details`, `photoPath`, `createdAt`.
- Auto-sets `createdAt` on construction; standard getters/setters.

### `model.User`
- Represents a user (email, password, role). Used by `UserService` and `AuthService`.

---

## Services

### `service.SharedServiceHolder`
- Static holder allowing the console-created `CrimeService` and Spring-created `WebSocketService` to reference each other.
- Methods: `set/getCrimeService`, `set/getWebSocketService`.

### `service.CrimeService`
- Responsibilities:
  - CRUD against Mongo `crimes` collection.
  - Maintain in-memory `ArrayList<Crime>` and `Stack<Crime>` for quick display (latest-first order).
  - Notify web clients via `WebSocketService.notifyCrimeUpdate()` after mutations.
- Key methods:
  - `addCrime(name, city, crimeType, details, photoPath)`: inserts into Mongo, mirrors into memory, notifies WebSocket.
  - `editCrime(id, ...)`: updates Mongo, updates in-memory structures, notifies.
  - `deleteCrime(id)`: removes from Mongo and memory, notifies.
  - `getCrimeById(id)`, `getAllCrimes()`, `getAllCrimesInFIFOOrder()`.
  - Console helpers: `displayAllCrimes()`, `displayCrimeDetails(Crime)`.
- Startup behavior: loads crimes from DB sorted by `createdAt` (desc) into the list/stack.

### `service.SearchService`
- Builds in-memory indexes (HashMaps) for `name`, `city`, and `crimeType` from the `crimes` collection.
- Provides search by name, city, crime type, details, and an `advancedSearch` that composes regex filters.
- Includes console display helpers and a simple interactive flow.

### `service.UserService`
- CRUD-like operations on Mongo `users` collection.
- Adds users with roles (`admin`, `special`, `normal`), prevents duplicate emails, prevents deleting `admin`.
- Hides passwords in read listings.

### `service.AuthService`
- Handles login, session state, role-based permissions used by the console menus.

### `service.WebSocketService` (`@Service("webSocketNotifier")`)
- Wraps `SimpMessagingTemplate` to broadcast crime updates to `/topic/crimes`.
- Sends a second delayed message to increase delivery reliability.
- Integrated with `SharedServiceHolder` so non-Spring-created services can trigger notifications.

### `service.WebLauncherService`
- Finds a free port, sets `server.port`, and starts the Spring Boot web app in a daemon thread.
- After start, obtains the `WebSocketService` bean and injects it into the shared `CrimeService`.
- Opens the system browser to `http://localhost:{port}/crime-records`.
- Provides `stopWebInterface()` and `isRunning()` helpers.

---

## Controller and Web UI

### `controller.WebController`
- Endpoints:
  - `GET /crime-records`: Renders Thymeleaf view with search parameters and grouped lists (Today/Yesterday/Earlier). Uses `CrimeService` and `SearchService`.
  - `GET /photo/{id}`: Streams a crime photo from an absolute path stored in the record.
  - `GET /defaultcriminal.jpg` and `GET /favico.ico`: Serve static files from working dir or classpath fallback.
- Sorting/grouping: converts `createdAt` to `LocalDate`, groups into Today/Yesterday/Earlier, total count to the model.

### `resources/templates/crime-records.html`
- Displays crime records with search UI.
- Connects to `/websocket-crime` via SockJS/STOMP, subscribes to `/topic/crimes` to refresh when updates are broadcast.

---

## Console UI

### `ui.MenuManager`
- Top-level loop managing login and role-based menus.
- Roles:
  - `admin`: manage users + full crime CRUD + web view launcher.
  - `special`: crime CRUD + web view launcher.
  - `normal`: web view launcher only.
- Integrates `CrimeInputHelper` and `InputValidator` from `util` for robust input.
- Launches web interface on demand via `WebLauncherService`.

---

## Database Logic (MongoDB)
- Connection: `DatabaseConfig.connect()` uses `MongoClients.create(CONNECTION_STRING)`; default `mongodb://localhost:27017`, database `crime_db`.
- Collections:
  - `users`: seeded with admin.
  - `crimes`: documents shaped as `{ name, city, crimeType, details, photoPath, createdAt }` with `_id` (ObjectId).
- Conversions:
  - Dates stored as `java.util.Date` in Mongo; converted to `LocalDateTime` in models using `ZoneId.systemDefault()`.
- Query patterns:
  - CRUD by `_id` using `ObjectId`.
  - Regex searches for partial matching across name/city/crimeType/details.
- In-memory mirrors:
  - `CrimeService` uses `ArrayList` and `Stack` to maintain display order and fast reads.
  - `SearchService` builds HashMap indexes to speed exact-match lookups, falling back to DB regex for partials.

---

## WebSocket Flow
1. Client loads `crime-records.html` and opens SockJS/STOMP connection to `/websocket-crime`.
2. Client subscribes to `/topic/crimes`.
3. When a crime is added/edited/deleted via console services, `CrimeService` calls `WebSocketService.notifyCrimeUpdate()`.
4. `WebSocketService` publishes a JSON message to `/topic/crimes` (and repeats after 500 ms).
5. Client receives the update and refreshes the list (typically via a small JS handler in the template).

---

## Maven: Dependencies and Purpose

`pom.xml` key points:
- Parent: `spring-boot-starter-parent:2.7.14`
- Java: 11
- Dependencies:
  - `spring-boot-starter-web`: MVC + embedded server (Tomcat by default)
  - `spring-boot-starter-thymeleaf`: templating
  - `spring-boot-starter-log4j2`: logging with Log4j2 (overrides default logging)
  - `spring-boot-starter-websocket`: STOMP endpoints
  - WebJars: `sockjs-client`, `stomp-websocket` for client-side runtime assets
  - MongoDB: `mongodb-driver-sync`, `mongodb-driver-core`, `bson`
- Plugins:
  - `spring-boot-maven-plugin`: repackage to executable jar, define main class for web app
  - `exec-maven-plugin`: run console main class directly
  - `maven-compiler-plugin`: sets `source/target=11` and `-parameters`

---

## Building and Running

### Prerequisites
- Java 11 installed and on PATH
- MongoDB running locally at `mongodb://localhost:27017`

### Common Commands
```bash
# clean and build
mvn clean package

# run console app main
mvn -q -DskipTests exec:java -Dexec.mainClass="com.crimemanagement.CrimeManagementApplication"

# run Spring Boot web app directly (optional)
mvn spring-boot:run -Dspring-boot.run.main-class=com.crimemanagement.CrimeManagementWebApplication

# run the shaded jar (after package)
java -jar target/crime-record-system-1.0-SNAPSHOT.jar
```

The console UI can launch the web UI dynamically via `WebLauncherService` (browser opens to `/crime-records`).

---

## Error Handling and Logging
- Exceptions during DB operations are caught and logged to `stderr`; user-friendly messages are printed to console.
- Log4j2 configuration is provided in `resources/log4j2.xml`.

---

## Security and Roles
- Default admin user: `admin/admin123` created on first DB init.
- Roles: `admin`, `special`, `normal`.
- Console menus enforce permissions via `AuthService.hasPermission(...)`.

---

## File-by-File Responsibilities (Key Files)
- `com.crimemanagement.CrimeManagementApplication`: console bootstrap and DB lifecycle.
- `com.crimemanagement.CrimeManagementWebApplication`: Spring Boot web bootstrap.
- `config.DatabaseConfig`: Mongo connection, seeding, collection access.
- `config.WebSocketConfig`: STOMP broker and endpoint registration.
- `config.ServiceConfig`: component scanning for services.
- `controller.WebController`: MVC endpoints and view model preparation.
- `model.Crime`: domain model for a crime record.
- `service.CrimeService`: CRUD + in-memory mirrors + WebSocket notifications.
- `service.SearchService`: indexed and regex-based search across crimes.
- `service.UserService`: user CRUD and role management.
- `service.WebSocketService`: publish updates to `/topic/crimes`.
- `service.WebLauncherService`: start/stop web server and open browser.
- `ui.MenuManager`: console interaction and role-based menus.
- `resources/templates/crime-records.html`: Thymeleaf page with live updates.

---

## Notes and Best Practices
- When adding new fields to `Crime`, update Mongo serialization/deserialization in services.
- Avoid blocking calls on the UI thread when launching the web interface; `WebLauncherService` already uses a daemon thread.
- Ensure images referenced in `photoPath` are accessible to the web process for `/photo/{id}` to work.
- Consider hashing passwords and enforcing stronger auth for production use.
