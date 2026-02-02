# Banking Microservices

Microservices-based banking system implementing Hexagonal Architecture with event-driven communication.

## Architecture

**Pattern**: Hexagonal Architecture (Ports & Adapters) + Event-Driven Architecture (EDA)


```
┌─────────────┐     ┌──────────────┐     ┌────────────┐
│  Frontend   │────►│   Customer   │────►│ PostgreSQL │
│  (Angular)  │     │   Service    │     │ (Port 5432)│
└─────────────┘     │  (Port 8081) │     └────────────┘
      │             └──────┬───────┘
      │                    │
      │               ┌────▼─────┐
      │               │ RabbitMQ │
      │               │  Events  │
      │               └────┬─────┘
      │                    │
      │             ┌──────▼───────┐     ┌────────────┐
      └────────────►│   Account    │────►│ PostgreSQL │
                    │   Service    │     │ (Port 5433)│
                    │  (Port 8082) │     └────────────┘
                    └──────────────┘
```

## Tech Stack

- **Java 21** + Spring Boot 3.5.9
- **PostgreSQL 17.6** (separate DBs per service)
- **RabbitMQ 3.13** (async messaging)
- **Flyway** (schema migrations)
- **Gradle** (composite build)

## Quick Start

```bash
# Clone and start
git clone https://github.com/jonant7/banking-microservices
cd banking-microservices
docker compose up --build

# Verify
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

**Access Points**:
- Customer API: http://localhost:8081
- Account API: http://localhost:8082
- RabbitMQ UI: http://localhost:15672 (guest/guest)

## API Endpoints

### Customer Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/customers` | Create customer |
| GET | `/api/v1/customers` | List customers (paginated, filterable) |
| GET | `/api/v1/customers/{id}` | Get customer by ID |
| PUT | `/api/v1/customers/{id}` | Full update |
| PATCH | `/api/v1/customers/{id}` | Partial update |
| PATCH | `/api/v1/customers/{id}/activate` | Activate customer |
| PATCH | `/api/v1/customers/{id}/deactivate` | Deactivate customer |

### Account Service (Port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Create account |
| GET | `/api/v1/accounts` | List accounts (paginated, filterable) |
| GET | `/api/v1/accounts/{id}` | Get account by ID |
| GET | `/api/v1/accounts/number/{accountNumber}` | Get by account number |
| PATCH | `/api/v1/accounts/{id}/activate` | Activate account |
| PATCH | `/api/v1/accounts/{id}/deactivate` | Deactivate account |
| POST | `/api/v1/accounts/{accountId}/transactions` | Execute transaction |
| GET | `/api/v1/accounts/transactions/{transactionId}` | Get transaction by ID |
| GET | `/api/v1/accounts/{accountId}/transactions` | List account transactions |
| GET | `/api/v1/accounts/{accountId}/transactions/report` | Transactions by date range |

### Reports Service (Port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/reports?customerId={id}&startDate={date}&endDate={date}` | Account statement report |

## Project Structure

```
banking-microservices/
├── customer-service/
│   └── src/main/java/com/banking/customer/
│       ├── application/          # Use cases, DTOs, ports
│       ├── domain/               # Entities, events, exceptions
│       ├── infrastructure/       # JPA, RabbitMQ, config
│       └── presentation/         # REST controllers
│
├── account-service/              # Same structure
│
├── compose.yaml                  # Docker orchestration
└── build.gradle                  # Gradle composite build
```

## Domain Events

**Published by Customer Service**:
- `CustomerCreatedEvent` → Account Service creates default account
- `CustomerUpdatedEvent` → Account Service updates customer info
- `CustomerStatusChangedEvent` → Account Service activates/deactivates accounts

**Event Flow**:
```
Customer Service → RabbitMQ (customer.events.exchange) → Account Service
```

## Database Schema

Both services use Flyway for automatic migrations on startup.

**customer_db** (Port 5432):
- Schema: `core`
- Tables: `customers`, `persons`

**account_db** (Port 5433):
- Schema: `core`
- Tables: `accounts`, `transactions`, `movements`

## Development

### Build & Test

```bash
# Build all
./gradlew buildAll

# Run tests
./gradlew testAll

# Build specific service
cd customer-service && ./gradlew build
```

### Run Locally

```bash
# Start infrastructure only
docker compose up customer-db account-db rabbitmq

# Run service
cd customer-service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Configuration Profiles

- `default`: Production settings
- `dev`: Local development
- `docker`: Container environment

## API Examples

### Create Customer

```bash
curl -X POST http://localhost:8081/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "person": {
      "name": "John Doe",
      "gender": "MALE",
      "dateOfBirth": "1990-01-15",
      "identification": "1234567890",
      "address": "123 Main St",
      "phoneNumber": "+593987654321"
    },
    "password": "securePass123"
  }'
```

### Create Account

```bash
curl -X POST http://localhost:8082/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "uuid-from-customer-creation",
    "accountNumber": "ACC001",
    "accountType": "SAVINGS",
    "initialBalance": 1000.00
  }'
```

### Execute Transaction

```bash
curl -X POST http://localhost:8082/api/v1/accounts/{accountId}/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "type": "DEPOSIT",
    "amount": 500.00,
    "description": "Deposit"
  }'
```

### Generate Statement

```bash
curl "http://localhost:8082/api/v1/reports?customerId={id}&startDate=2026-01-01T00:00:00&endDate=2026-02-01T23:59:59"
```

## Response Format

**Success**:
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully",
  "timestamp": "2026-02-02T10:30:00Z"
}
```

**Error**:
```json
{
  "success": false,
  "error": {
    "code": "CUSTOMER_NOT_FOUND",
    "message": "Customer not found with ID: xyz",
    "timestamp": "2026-02-02T10:30:00Z"
  }
}
```

## Testing

Import `Banking-Microservices.postman_collection.json` for complete API testing.

## Troubleshooting

```bash
# View logs
docker compose logs -f customer-service
docker compose logs -f account-service

# Restart services
docker compose restart

# Clean restart (removes data)
docker compose down -v
docker compose up --build
```

## Key Design Decisions

1. **Database per Service**: Each microservice owns its database, ensuring loose coupling
2. **Event-Driven Communication**: Async events via RabbitMQ for inter-service communication
3. **Hexagonal Architecture**: Clear separation between domain logic and infrastructure
4. **Aggregate Root Pattern**: Customer and Account as aggregate roots with domain events
5. **Optimistic Locking**: Version control for concurrent updates
6. **Soft Deletes**: Status-based deactivation instead of hard deletes

## Technical Highlights

- **DDD**: Rich domain models with business invariants
- **CQRS**: Separate read/write models where appropriate
- **Repository Pattern**: Abstraction over data access
- **Mapper Pattern**: Clean DTO transformations
- **Builder Pattern**: Test data creation
- **Specification Pattern**: Dynamic query building

---

**Made with Spring Boot + Hexagonal Architecture**
