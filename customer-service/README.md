# Banking Backend - Microservices

Event-driven microservices implementing customer management, account operations, and transaction processing with hexagonal architecture.

## 🏛️ Architecture

### Hexagonal Architecture (Ports & Adapters)

```
┌─────────────────────────────────────────────┐
│         Presentation (REST API)             │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│          Application (Use Cases)            │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│         Domain (Entities & Logic)           │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│    Infrastructure (JPA, RabbitMQ, etc)      │
└─────────────────────────────────────────────┘
```

### Services

**customer-service (Port 8081)**
- Customer lifecycle management
- Person entity inheritance
- Password encryption with BCrypt
- Customer event publishing

**account-service (Port 8082)**
- Account and transaction management
- Balance tracking and validation
- Account statements (JSON/PDF)
- Customer event consumption

**contracts**
- Shared event definitions for inter-service communication

## 🚀 Technology Stack

- **Java 21** with Spring Boot 3.5.9
- **PostgreSQL 17.6** with Flyway migrations
- **RabbitMQ 3.13** for event streaming
- **Gradle** composite build
- **Docker** multi-stage builds

## 📋 Prerequisites

**Docker Deployment:**
- Docker Engine 20.10+
- Docker Compose 2.x

**Local Development:**
- JDK 21+
- PostgreSQL 17+ (or use Docker)
- RabbitMQ 3.13+ (or use Docker)

## ⚡ Quick Start

### Using Docker (Recommended)

```bash
# From project root
docker compose up --build -d

# Verify health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

### Local Development

#### 1. Start Infrastructure

```bash
# Using Docker for databases and RabbitMQ
docker compose up customer-db account-db rabbitmq -d
```

#### 2. Configure Services

Create `application-dev.yml` from the example template:

```bash
# Customer Service
cd backend/customer-service/src/main/resources
cp application-dev.yml.example application-dev.yml

# Account Service
cd backend/account-service/src/main/resources
cp application-dev.yml.example application-dev.yml
```

Edit connection settings if needed (defaults work with Docker Compose).

#### 3. Run Services

```bash
# Customer Service
cd backend/customer-service
./gradlew bootRun

# Account Service (in another terminal)
cd backend/account-service
./gradlew bootRun
```

## 🗂️ Project Structure

```
backend/
├── contracts/                   # Shared event contracts
│   ├── src/main/java/com/banking/contracts/events/
│   └── build.gradle
│
├── customer-service/            # Customer bounded context
│   ├── src/main/java/com/banking/customer/
│   │   ├── application/         # Use cases
│   │   ├── domain/              # Entities & business logic
│   │   ├── infrastructure/      # JPA, RabbitMQ, config
│   │   └── presentation/        # REST controllers & DTOs
│   ├── src/main/resources/
│   │   ├── db/migration/        # Flyway migrations
│   │   ├── application.yml
│   │   └── i18n/                # Validation messages
│   ├── build.gradle
│   └── Dockerfile
│
└── account-service/             # Account bounded context
    ├── src/main/java/com/banking/account/
    │   ├── application/
    │   ├── domain/
    │   ├── infrastructure/
    │   └── presentation/
    ├── src/main/resources/
    │   ├── db/migration/
    │   ├── application.yml
    │   └── i18n/
    ├── build.gradle
    └── Dockerfile
```

### Testing with Postman

Import `postman/Banking-Microservices.postman_collection.json` for complete test scenarios.

## 🔧 Gradle Composite Build

The project uses composite builds to share the contracts module:

**Each service's `settings.gradle`:**
```groovy
includeBuild('../contracts') {
    dependencySubstitution {
        substitute module("com.banking:contracts") using project(":")
    }
}
```

This allows services to use the latest local version of contracts without publishing to a repository.

## 🧪 Testing
```bash
# Run tests for specific service
./gradlew :customer-service:test
./gradlew :account-service:test

# Run tests with coverage report
./gradlew :customer-service:test jacocoTestReport
./gradlew :account-service:test jacocoTestReport

# Run all tests with coverage for both services
./gradlew test jacocoTestReport

# Run specific test class with coverage
./gradlew :customer-service:test --tests CustomerServiceTest
./gradlew :customer-service:jacocoTestReport
```

**Test Coverage Reports:**
- Customer Service: `customer-service/build/reports/jacoco/test/html/index.html`
- Account Service: `account-service/build/reports/jacoco/test/html/index.html`
```bash
# View coverage report (Linux/Mac)
open customer-service/build/reports/jacoco/test/html/index.html

# View coverage report (Windows)
start customer-service/build/reports/jacoco/test/html/index.html
```

## 🗄️ Database Migrations

Database schemas are managed with Flyway. Migrations run automatically on service startup.

**Migration files:** `src/main/resources/db/migration/V{version}__{description}.sql`

```bash
# View migration history
docker compose exec customer-db psql -U postgres -d customer_db \
  -c "SELECT * FROM core.schema_history ORDER BY installed_rank;"
```

## 🐳 Docker Operations

```bash
# Start services
docker compose up -d

# View logs
docker compose logs -f customer-service

# Restart service
docker compose restart customer-service

# Stop all
docker compose down

# Clean restart
docker compose down -v
docker compose up --build -d
```

## 🚨 Troubleshooting

### Port Conflicts

```bash
# Check port availability
lsof -i :8081,8082,5432,5433,5672

# Or using netstat
netstat -tuln | grep -E '8081|8082|5432|5433|5672'
```

### Database Connection Issues

```bash
# Verify database is ready
docker compose exec customer-db pg_isready -U postgres -d customer_db

# View service logs
docker compose logs customer-service
```

### RabbitMQ Connection Issues

```bash
# Check RabbitMQ health
docker compose exec rabbitmq rabbitmq-diagnostics ping

# Access management UI
open http://localhost:15672
```

## 📊 Monitoring

All services expose Spring Boot Actuator endpoints:

```bash
# Overall health
curl http://localhost:8081/actuator/health

# Database connectivity
curl http://localhost:8081/actuator/health/db

# RabbitMQ connectivity
curl http://localhost:8081/actuator/health/rabbit

# Application metrics
curl http://localhost:8081/actuator/metrics
```

---

**Built with ☕ Java 21 | 🍃 Spring Boot 3.5 | 🐘 PostgreSQL 17 | 🐰 RabbitMQ 3.13**