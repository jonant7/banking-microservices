# Banking Microservices

Banking system with microservices architecture.

## Architecture

- **customer-service**: Customer management (Port: 8081)
- **account-service**: Account and transaction management (Port: 8082)
- **Infrastructure**: PostgreSQL (2 databases), RabbitMQ

**Pattern**: Hexagonal Architecture + Event-Driven Architecture

## Tech Stack

- Java 21
- Spring Boot 3.5.9
- PostgreSQL 17.6
- RabbitMQ 3.13
- Flyway (Database migrations)
- Gradle (Composite Build)
- Docker & Docker Compose

## Prerequisites

- Docker & Docker Compose
- Java 21+ (only for local development without Docker)

## Quick Start

```bash
# Clone and start the entire system
git clone https://github.com/jonant7/banking-microservices
cd banking-microservices
docker compose up --build
```

**Available services:**
- Customer API: http://localhost:8081
- Account API: http://localhost:8082
- RabbitMQ Management: http://localhost:15672 (guest/guest)

## API Testing

### Health Checks

```bash
# Customer Service
curl http://localhost:8081/actuator/health

# Account Service
curl http://localhost:8082/actuator/health
```

### Postman Collection

Import `Banking-Microservices.postman_collection.json` into Postman to test all endpoints.

**Main endpoints:**
- Health Checks: `GET /actuator/health`
- Customer API: `GET/POST /api/customers`
- Account API: `GET/POST /api/accounts`
- Reports: `GET /api/reports/accounts/{id}/statement`

## Local Development

### Build all services

```bash
./gradlew buildAll
```

### Run tests

```bash
./gradlew testAll
```

### Build specific service

```bash
cd customer-service
./gradlew build
```

### Run specific service locally

```bash
cd customer-service
./gradlew bootRun
```

## Project Structure

```
banking-microservices/
├── customer-service/          # Customer management microservice
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       └── db/migration/    # Flyway migrations
│   │   └── test/
│   ├── build.gradle
│   └── Dockerfile
│
├── account-service/           # Account management microservice
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       └── db/migration/    # Flyway migrations
│   │   └── test/
│   ├── build.gradle
│   └── Dockerfile
│
├── compose.yaml              # Docker Compose orchestration
├── settings.gradle           # Composite build configuration
├── build.gradle              # Root build tasks
└── README.md
```

## Database Migrations

Database migrations are managed automatically with Flyway.

**No manual SQL script execution is required.**

When services start, Flyway:
1. Detects the database state
2. Executes pending migrations
3. Records versioning in `schema_history` table

Location: `src/main/resources/db/migration/`

## Services Configuration

### Customer Service
- **Port**: 8081
- **Database**: customer_db (PostgreSQL)
- **Schema**: core
- **Message Broker**: RabbitMQ

### Account Service
- **Port**: 8082
- **Database**: account_db (PostgreSQL)
- **Schema**: core
- **Message Broker**: RabbitMQ

## Infrastructure

### PostgreSQL Databases

- **customer-db**: Port 5432
- **account-db**: Port 5433

Both use the `core` schema for business logic.

### RabbitMQ

- **AMQP Port**: 5672
- **Management UI**: http://localhost:15672
- **Credentials**: guest/guest

## Stopping Services

```bash
# Stop services
docker compose down

# Stop and remove volumes (clean database)
docker compose down -v
```

## Development Notes

- Database schemas are created automatically by Flyway
- All microservices are independent Spring Boot applications
- Composite build allows managing both services from root
- Docker multi-stage builds optimize image size

### Port conflicts

Ensure ports 5432, 5433, 5672, 8081, 8082, and 15672 are not in use by other applications.