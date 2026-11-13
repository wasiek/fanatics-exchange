# Fanatics Exchange Platform

High-availability trading exchange backend built with Spring Boot 3.4 and Java 21.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                          REST API (8096)                            │
│  /api/v1/accounts  /api/v1/orders  /api/v1/trades  /swagger-ui     │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────────┐
│                        Controller Layer                              │
│  Thin controllers → delegate to services, return DTOs               │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────────┐
│                         Service Layer                                │
│  Business logic, @Transactional, concurrency control                │
│                                                                     │
│  ┌─────────────────────┐  ┌────────────────────┐  ┌──────────────┐ │
│  │TradingAccount       │  │Order               │  │Trade         │ │
│  │Service              │  │Service             │  │Service       │ │
│  │• deposit/withdraw   │  │• placeOrder        │  │• query       │ │
│  │  (PESSIMISTIC_WRITE)│  │  (PESSIMISTIC_WRITE│  │              │ │
│  │                     │  │   on account)      │  │              │ │
│  └─────────┬───────────┘  └────────┬───────────┘  └──────┬───────┘ │
└────────────┼───────────────────────┼─────────────────────┼─────────┘
             │                       │                     │
┌────────────▼───────────────────────▼─────────────────────▼─────────┐
│                       Repository Layer                               │
│  Spring Data JPA, @Lock(PESSIMISTIC_WRITE), derived queries         │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
┌─────────────▼──────┐  ┌─────▼──────┐  ┌──────▼────────────────────┐
│  PostgreSQL (prod) │  │H2 (dev)    │  │  Apache Kafka             │
│                    │  │in-memory   │  │  topic: trade-events      │
└────────────────────┘  └────────────┘  │  (afterCommit publish)    │
                                        └───────────────────────────┘
```

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| **`PESSIMISTIC_WRITE` on account** (order placement) | Prevents race condition: two concurrent orders on same account can't both read stale balance. `SELECT ... FOR UPDATE` holds row lock until commit. |
| **Kafka publish after commit** (`TransactionSynchronization.afterCommit`) | Trade events only published after DB transaction succeeds. No orphan messages. |
| **`@Version` on Account and Order** | Optimistic locking for non-pessimistic paths. `OptimisticLockException` → 409. |
| **No `@Version` on Trade** | Trades are read-only (immutable after creation). No concurrent updates possible. |
| **`IllegalStateException` → 409** | Business rule violations (insufficient balance, invalid order state) are conflicts, not server errors. |
| **`ResourceNotFoundException` → 404** | Proper REST semantics for missing accounts/orders/trades. |
| **`DataIntegrityViolationException` → 409** | TOCTOU race on unique constraints (e.g., duplicate account number). |
| **Pagination on all list endpoints** (`Pageable`, default 50) | Prevents OOM. Orders/trades grow unboundedly. |
| **`POST /orders/{id}/cancel`** (not DELETE) | Cancellation is a state transition, not resource deletion. Order remains in history. |
| **DTO separation** (Request/Response) | API contract decoupled from JPA entities. No lazy-proxy issues. |
| **`@JsonIgnore` on lazy relations** | Prevents `LazyInitializationException` during serialization. |
| **`create-drop` in dev** | H2 in-memory is throwaway. |

## Features

- **Trading Account Management** - Create accounts, deposit/withdraw (pessimistic locking)
- **Order Management** - Place BUY/SELL MARKET orders, cancel pending orders
- **Trade Execution** - Simulated order filling with commission calculation
- **Balance Management** - Transactional integrity, race-condition safe
- **Kafka Integration** - Trade events published after commit
- **REST API** - Clean, paginated, well-documented endpoints
- **Swagger UI** - Interactive API documentation
- **Docker** - Multi-stage build + docker-compose (app + PostgreSQL + Kafka)
- **Validation** - Bean Validation (400, 404, 409)
- **Global Exception Handling** - Granular error responses

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.4.1 |
| Database | H2 (dev) / PostgreSQL (prod) |
| ORM | Spring Data JPA / Hibernate |
| Messaging | Apache Kafka |
| API Docs | SpringDoc OpenAPI (Swagger) |
| Build | Maven |
| Container | Docker (multi-stage) |
| CI/CD | GitHub Actions |
| Testing | JUnit 5, MockMvc, PIT (mutation) |

## Testing

| Metric | Value |
|--------|-------|
| Integration tests | 17 |
| Line coverage | 88% |
| Mutation kill rate (PIT) | 63% (43/68) |
| Test strength | 78% |

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker (optional)

### Run with H2 (dev)
```bash
mvn spring-boot:run
```

API: `http://localhost:8096`  
Swagger: `http://localhost:8096/swagger-ui.html`

### Run with Docker Compose
```bash
docker compose up --build
```

### Run Tests
```bash
mvn test
```

### Mutation Testing (PIT)
```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

## API Endpoints

### Trading Accounts
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Create account |
| GET | `/api/v1/accounts` | List (paginated) |
| GET | `/api/v1/accounts/{id}` | Get by ID |
| GET | `/api/v1/accounts/number/{number}` | Get by account number |
| POST | `/api/v1/accounts/{id}/deposit` | Deposit funds |
| POST | `/api/v1/accounts/{id}/withdraw` | Withdraw funds |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/orders` | Place order (BUY/SELL MARKET) |
| GET | `/api/v1/orders` | List (paginated) |
| GET | `/api/v1/orders/{id}` | Get by ID |
| GET | `/api/v1/orders/account/{id}` | By account |
| GET | `/api/v1/orders/symbol/{sym}/status/{st}` | By symbol + status |
| POST | `/api/v1/orders/{id}/cancel` | Cancel pending order |

### Trades
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/trades` | List (paginated) |
| GET | `/api/v1/trades/{id}` | Get by ID |
| GET | `/api/v1/trades/order/{id}` | By order |
| GET | `/api/v1/trades/account/{id}` | By account |

## Project Structure

```
src/main/java/com/github/dziki/fanatics/
├── FanaticsExchangeApplication.java
├── config/
│   ├── JpaConfig.java
│   └── OpenApiConfig.java
├── controller/
│   ├── TradingAccountController.java
│   ├── OrderController.java
│   └── TradeController.java
├── dto/
│   ├── TradingAccountRequest.java
│   ├── TradingAccountResponse.java
│   ├── DepositRequest.java
│   ├── WithdrawRequest.java
│   ├── OrderRequest.java
│   ├── OrderResponse.java
│   └── TradeResponse.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── kafka/
│   └── TradeKafkaProducer.java
├── model/
│   ├── TradingAccount.java
│   ├── Order.java
│   └── Trade.java
├── repository/
│   ├── TradingAccountRepository.java
│   ├── OrderRepository.java
│   └── TradeRepository.java
└── service/
    ├── TradingAccountService.java
    ├── OrderService.java
    └── TradeService.java
```

## License

MIT
