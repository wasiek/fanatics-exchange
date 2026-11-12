# Fanatics Exchange Platform

High-availability trading exchange platform built with Spring Boot 3.4 and Java 21.

## Features

- **Trading Account Management** - Create accounts, deposit/withdraw funds
- **Order Management** - Place BUY/SELL MARKET orders with real-time execution
- **Trade Execution** - Simulated order filling with commission calculation
- **Balance Management** - Real-time balance updates with transactional integrity
- **Kafka Integration** - Event-driven architecture for trade events
- **REST API** - Clean, well-documented REST endpoints
- **Swagger UI** - Interactive API documentation at `/swagger-ui.html`
- **Docker** - Multi-stage Docker build + docker-compose (app + PostgreSQL + Kafka)
- **GraalVM Native Image** - Ready for native compilation
- **H2 / PostgreSQL** - H2 for dev, PostgreSQL for production
- **Validation** - Bean Validation with proper error handling
- **Global Exception Handling** - Clean error responses

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
| Native | GraalVM Native Image (ready) |

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker (optional, for full stack)

### Run with H2 (dev)
```bash
mvn spring-boot:run
```

API available at: `http://localhost:8096`
Swagger UI: `http://localhost:8096/swagger-ui.html`
H2 Console: `http://localhost:8096/h2-console`

### Run with Docker Compose (full stack)
```bash
docker compose up --build
```

This starts:
- App on port 8096
- PostgreSQL on port 5433
- Kafka on port 9093

### Run Tests
```bash
mvn test
```

### Build JAR
```bash
mvn clean package -DskipTests
```

### Build Native Image (requires GraalVM)
```bash
mvn native:compile
```

## API Endpoints

### Trading Accounts
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Create trading account |
| GET | `/api/v1/accounts` | List all accounts |
| GET | `/api/v1/accounts/{id}` | Get account by ID |
| POST | `/api/v1/accounts/{id}/deposit` | Deposit funds |
| POST | `/api/v1/accounts/{id}/withdraw` | Withdraw funds |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/orders` | Place order (BUY/SELL MARKET) |
| GET | `/api/v1/orders` | List all orders |
| GET | `/api/v1/orders/{id}` | Get order by ID |
| GET | `/api/v1/orders/account/{accountId}` | Get orders by account |

### Trades
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/trades` | List all trades |
| GET | `/api/v1/trades/account/{accountId}` | Get trades by account |

## Order Execution Logic

1. **Validation** - Check account exists, is ACTIVE, has sufficient balance
2. **Balance Update** - Deduct cost (BUY) or add proceeds (SELL) atomically
3. **Order Fill** - Execute at specified price (or default 100.00)
4. **Commission** - 0.1% of trade value
5. **Trade Record** - Create trade entry with execution details

### Example: BUY 10 shares of AAPL at $150
- Cost: 10 × $150 = $1,500
- Commission: $1,500 × 0.001 = $1.50
- Total deduction: $1,501.50
- Balance: $100,000 → $98,498.50

## Project Structure

```
src/main/java/com/github/dziki/fanatics/
├── FanaticsExchangeApplication.java      # Main class
├── config/
│   ├── JpaConfig.java                    # JPA configuration
│   └── OpenApiConfig.java                # Swagger/OpenAPI config
├── controller/
│   ├── TradingAccountController.java     # Account CRUD + deposit/withdraw
│   └── OrderController.java              # Order placement + queries
├── dto/
│   ├── TradingAccountRequest.java        # Create account request
│   ├── TradingAccountResponse.java       # Account response
│   ├── OrderRequest.java                 # Place order request
│   └── OrderResponse.java                # Order response
├── exception/
│   └── GlobalExceptionHandler.java       # Global error handling
├── model/
│   ├── TradingAccount.java               # Account entity
│   ├── Order.java                        # Order entity
│   └── Trade.java                        # Trade entity
├── repository/
│   ├── TradingAccountRepository.java
│   ├── OrderRepository.java
│   └── TradeRepository.java
└── service/
    ├── TradingAccountService.java        # Account + balance logic
    └── OrderService.java                 # Order + execution logic
```

## Configuration

| Profile | Database | Kafka | Port |
|---------|----------|-------|------|
| default (dev) | H2 in-memory | localhost:9092 | 8096 |
| prod | PostgreSQL | configurable | 8096 |

### Environment Variables (prod)
| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_NAME` | fanatics | Database name |
| `DB_USER` | fanatics | Database user |
| `DB_PASSWORD` | fanatics | Database password |
| `KAFKA_BOOTSTRAP_SERVERS` | kafka:29092 | Kafka bootstrap servers |

## License

MIT
