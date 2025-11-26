# Teamwalk REST API

A REST API for tracking team step counts in a competitive leaderboard system. Teams compete to accumulate the most steps, with real-time tracking and leaderboard functionality.

## Features

- Create and remove team step counters
- Add steps to teams (thread-safe operations)
- Retrieve individual team step counts
- View leaderboard with teams sorted by step count (descending)

## Technology Stack

- **Java 21**
- **Spring Boot 4.0.0**
- **Maven** (build tool)
- **In-memory storage** (ConcurrentHashMap + AtomicLong for thread safety)

## Requirements

- Java 21 or higher
- Maven 3.6+ (or use included Maven wrapper)

## Building and Running

### Build the project

```bash
./mvnw clean compile
```

### Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### Build JAR file

```bash
./mvnw clean package
```

Run the JAR:

```bash
java -jar target/assessemnt-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### 1. Create Team

Create a new team step counter.

**Request:**
```http
PUT /api/teams/{teamId}
```

**Example:**
```bash
curl -X PUT http://localhost:8080/api/teams/Engineering
```

**Response:** `200 OK`

---

### 2. Remove Team

Remove a team step counter.

**Request:**
```http
DELETE /api/teams/{teamId}
```

**Example:**
```bash
curl -X DELETE http://localhost:8080/api/teams/Engineering
```

**Response:** `200 OK`

**Error:** `404 Not Found` if team doesn't exist

---

### 3. Add Steps to Team

Add steps to a team's counter.

**Request:**
```http
POST /api/teams/{teamId}/steps
Content-Type: application/json

{
  "steps": 1000
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/teams/Engineering/steps \
  -H "Content-Type: application/json" \
  -d '{"steps": 1000}'
```

**Response:** `200 OK`

**Errors:**
- `400 Bad Request` if steps is negative or null
- `404 Not Found` if team doesn't exist

---

### 4. Get Team Step Count

Retrieve a team's current step count.

**Request:**
```http
GET /api/teams/{teamId}
```

**Example:**
```bash
curl http://localhost:8080/api/teams/Engineering
```

**Response:** `200 OK`
```json
{
  "teamId": "Engineering",
  "stepCount": 1000
}
```

**Error:** `404 Not Found` if team doesn't exist

---

### 5. Get Leaderboard

Get all teams sorted by step count in descending order.

**Request:**
```http
GET /api/teams/leaderboard
```

**Example:**
```bash
curl http://localhost:8080/api/teams/leaderboard
```

**Response:** `200 OK`
```json
[
  {
    "teamId": "Engineering",
    "stepCount": 5000
  },
  {
    "teamId": "Sales",
    "stepCount": 3000
  },
  {
    "teamId": "Marketing",
    "stepCount": 2000
  }
]
```

## Example Usage Flow

```bash
# 1. Create teams
curl -X PUT http://localhost:8080/api/teams/Engineering
curl -X PUT http://localhost:8080/api/teams/Sales
curl -X PUT http://localhost:8080/api/teams/Marketing

# 2. Add steps to teams
curl -X POST http://localhost:8080/api/teams/Engineering/steps \
  -H "Content-Type: application/json" \
  -d '{"steps": 5000}'

curl -X POST http://localhost:8080/api/teams/Sales/steps \
  -H "Content-Type: application/json" \
  -d '{"steps": 3000}'

curl -X POST http://localhost:8080/api/teams/Marketing/steps \
  -H "Content-Type: application/json" \
  -d '{"steps": 2000}'

# 3. Check individual team
curl http://localhost:8080/api/teams/Engineering

# 4. View leaderboard
curl http://localhost:8080/api/teams/leaderboard
```

## Architecture

### Thread Safety

The service uses `ConcurrentHashMap<String, AtomicLong>` for storage to ensure:
- **Thread-safe operations** under high concurrent load
- **No lost steps** - atomic operations guarantee data consistency
- **Scalability** - O(1) lookups and efficient concurrent access

### Error Handling

- **400 Bad Request**: Invalid input (negative steps, empty team ID, validation errors)
- **404 Not Found**: Team doesn't exist
- Consistent error response format:
  ```json
  {
    "error": "Team not found: Engineering"
  }
  ```

## Project Structure

```
src/main/java/com/example/assessemnt/
├── controller/
│   └── TeamStepController.java      # REST endpoints
├── service/
│   └── TeamStepService.java         # Business logic
├── dto/
│   ├── AddStepsRequest.java         # Request DTO
│   ├── TeamResponse.java            # Response DTO
│   └── LeaderboardEntry.java        # Leaderboard DTO
├── exception/
│   ├── TeamNotFoundException.java   # Custom exception
│   └── GlobalExceptionHandler.java  # Error handling
└── AssessemntApplication.java       # Main application class
```

## Design Decisions

- **In-memory storage**: No external database required (per requirements)
- **RESTful API**: Standard HTTP methods and status codes
- **Idempotent operations**: PUT for team creation (safe to retry)
- **Input validation**: Jakarta Validation annotations
- **No authentication**: Out of scope per requirements
- **No logging**: Out of scope per requirements

## Testing

Run tests:

```bash
./mvnw test
```

## License

This project is part of an assessment task.

