# Spendify API

A RESTful API for tracking personal expenses, built with Spring Boot. It supports full CRUD for expenses, filtering by month and category, and monthly spending summaries broken down by category.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)

## Features

- Create, read, update and delete expenses
- Filter expenses by month and/or category
- Monthly summary with total spent, expense count and totals per category
- Request validation with field-level error messages
- Consistent JSON error responses across the whole API
- Versioned database schema with Flyway
- Integration tests running against an in-memory database

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 (Web, Data JPA, Validation) |
| Persistence | Hibernate, MySQL 8 |
| Migrations | Flyway |
| Testing | JUnit 5, MockMvc, H2 |
| Build | Maven |

## Architecture

The project follows a layered architecture:

```
Controller  →  Service  →  Repository  →  Database
   (HTTP)      (business     (Spring Data
               logic)          JPA)
```

- **Controllers** handle HTTP concerns and validate input DTOs.
- **Services** hold business logic and define transaction boundaries.
- **Repositories** are Spring Data JPA interfaces.
- **DTOs** (Java records) decouple the API contract from the persistence model.
- **`GlobalExceptionHandler`** (`@RestControllerAdvice`) maps exceptions to HTTP responses.

## Getting started

### Prerequisites

- JDK 21 or later
- No Maven installation needed: the project ships with the Maven Wrapper (`./mvnw`, or `mvnw.cmd` on Windows)
- MySQL 8, or Docker to run it locally

### 1. Start MySQL

Using the provided Docker Compose file:

```bash
docker compose up -d
```

Or point the application to an existing MySQL instance using the environment variables below. The database is created automatically if it does not exist.

### 2. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`. On startup, Flyway creates the schema and loads sample data.

### Running without MySQL

Activate the `h2` profile to use an in-memory database:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:spendify`, user `sa`, empty password).

## Configuration

Database settings are read from environment variables, with defaults for local development:

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_DATABASE` | `spendify` | Database name |
| `DB_USERNAME` | `root` | Database user |
| `DB_PASSWORD` | *(empty)* | Database password |

## API reference

Base URL: `/api/expenses`

| Method | Endpoint | Description | Success |
|---|---|---|---|
| `GET` | `/api/expenses` | List expenses (optional `month` and `category` filters) | `200` |
| `GET` | `/api/expenses/summary` | Monthly summary (optional `month`, defaults to the current month) | `200` |
| `GET` | `/api/expenses/{id}` | Get an expense by id | `200` |
| `POST` | `/api/expenses` | Create an expense | `201` |
| `PUT` | `/api/expenses/{id}` | Update an expense | `200` |
| `DELETE` | `/api/expenses/{id}` | Delete an expense | `204` |

Query parameters:

- `month`: format `yyyy-MM`, e.g. `2026-10`
- `category`: one of `FOOD`, `TRANSPORT`, `HOUSING`, `HEALTH`, `ENTERTAINMENT`, `EDUCATION`, `OTHER`

A ready-to-use collection of requests is available in [`requests.http`](requests.http) (IntelliJ HTTP Client / VS Code REST Client).

### Create an expense

```http
POST /api/expenses
Content-Type: application/json

{
  "description": "Gym membership",
  "amount": 99.90,
  "date": "2026-10-15",
  "category": "HEALTH"
}
```

Response `201 Created`:

```json
{
  "id": 6,
  "description": "Gym membership",
  "amount": 99.90,
  "date": "2026-10-15",
  "category": "HEALTH"
}
```

Validation rules:

| Field | Rules |
|---|---|
| `description` | required, max 120 characters |
| `amount` | required, greater than zero, max 2 decimal places |
| `date` | required, format `yyyy-MM-dd`, not in the future |
| `category` | required, one of the categories above |

### Monthly summary

```http
GET /api/expenses/summary?month=2026-10
```

```json
{
  "month": "2026-10",
  "total": 2343.40,
  "count": 4,
  "byCategory": [
    { "category": "HOUSING", "total": 1800.00 },
    { "category": "FOOD", "total": 450.90 },
    { "category": "ENTERTAINMENT", "total": 60.00 },
    { "category": "TRANSPORT", "total": 32.50 }
  ]
}
```

### Error responses

All errors share the same structure:

```json
{
  "status": 422,
  "message": "Validation failed",
  "errors": {
    "amount": ["amount must be greater than zero"]
  },
  "timestamp": "2026-10-28T10:15:00"
}
```

| Status | When |
|---|---|
| `400` | Malformed JSON, invalid date format, unknown category or invalid parameter |
| `404` | Expense or route not found |
| `422` | Request body fails validation |
| `500` | Unexpected server error (details are logged, never exposed) |

## Running tests

```bash
./mvnw test
```

Tests use the `h2` profile, so no MySQL instance is required. Each test runs in a transaction that is rolled back afterwards.

## Project structure

```
src/
├── main/
│   ├── java/com/bruno/spendify/
│   │   ├── controller/      # REST endpoints
│   │   ├── service/         # Business logic
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── model/           # JPA entities and enums
│   │   ├── dto/             # Request/response records
│   │   └── exception/       # Custom exceptions and global handler
│   └── resources/
│       ├── application.properties
│       ├── application-h2.properties
│       └── db/migration/    # Flyway migrations
└── test/
    └── java/com/bruno/spendify/
```

## Roadmap

- [ ] Pagination and sorting
- [ ] Authentication with Spring Security and JWT
- [ ] Budgets per category with overspending alerts
- [ ] OpenAPI / Swagger documentation
- [ ] Unit tests for the service layer
- [ ] Containerized application image
