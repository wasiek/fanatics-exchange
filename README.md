# Fanatics Exchange Platform

High-availability trading exchange platform built with Spring Boot 3.4 and Java 21.

## Features

- **Trading Account Management** — Create accounts, deposit/withdraw funds
- **Order Management** — Place, track, and cancel orders (MARKET, LIMIT, STOP)
- **Trade Execution** — Simulated order matching and trade records
- **Real-time Updates** — Kafka integration for async event processing
- **REST API** — Clean, versioned REST endpoints
- **GraalVM Native Image** — Optimized for fast startup and low memory footprint

## Tech Stack

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Data JPA**
- **Kafka**
- **PostgreSQL** (production) / **H2** (dev/test)
- **Lombok**
- **GraalVM Native Image**

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker (optional, for PostgreSQL and Kafka)

### Build

```bash
mvn clean install
```

### Run (development)

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8096` with an in-memory H2 database.

### Run (production)

```bash
# Set environment variables for PostgreSQL
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/fanatics
export SPRING_DATASOURCE_USERNAME=fanatics
export SPRING_DATASOURCE_PASSWORD=fanatics

mvn spring-boot:run
```

### Build Native Image (GraalVM)

```bash
# Install GraalVM Native Image
sudo apt install graalvm-native-image

# Build native executable
mvn native:compile

# Run native executable
./target/fanatics-exchange
```

## API Endpoints

### Trading Accounts

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Create a new trading account |
| GET | `/api/v1/accounts` | List all accounts |
| GET | `/api/v1/accounts/{id}` | Get account by ID |
| GET | `/api/v1/accounts/number/{accountNumber}` | Get account by number |
| POST | `/api/v1/accounts/{id}/deposit` | Deposit funds |
| POST | `/api/v1/accounts/{id}/withdraw` | Withdraw funds |

### Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/orders` | Place a new order |
| GET | `/api/v1/orders` | List all orders |
| GET | `/api/v1/orders/{id}` | Get order by ID |
| GET | `/api/v1/orders/account/{accountId}` | Get orders by account |
| GET | `/api/v1/orders/symbol/{symbol}/status/{status}` | Get orders by symbol and status |
| DELETE | `/api/v1/orders/{id}` | Cancel an order |

## Example Requests

### Create Trading Account

```bash
curl -X POST http://localhost:8096/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC123456789",
    "ownerName": "John Doe",
    "initialBalance": 100000.00
  }'
```

### Deposit Funds

```bash
curl -X POST http://localhost:8096/api/v1/accounts/1/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000.00
  }'
```

### Place Order

```bash
curl -X POST http://localhost:8096/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "symbol": "AAPL",
    "side": "BUY",
    "type": "MARKET",
    "quantity": 100.00,
    "price": 150.00
  }'
```

Response:
```json
{
  "id": 1,
  "accountId": 1,
  "symbol": "AAPL",
  "side": "BUY",
  "type": "MARKET",
  "quantity": 100.00,
  "price": 150.00,
  "status": "FILLED",
  "filledQuantity": 100.00,
  "averageFillPrice": 150.00,
  "createdAt": "2026-11-12T17:15:00Z",
  "filledAt": "2026-11-12T17:15:00Z"
}
```

### Get Orders by Account

```bash
curl http://localhost:8096/api/v1/orders/account/1
```

## Project Structure

```
src/main/java/com/github/dziki/fanatics/
├── FanaticsExchangeApplication.java          # Main application class
├── config/
│   └── JpaConfig.java                        # JPA auditing configuration
├── controller/
│   ├── TradingAccountController.java         # REST API for accounts
│   └── OrderController.java                  # REST API for orders
├── dto/
│   ├── TradingAccountRequest.java            # Request DTO
│   ├── TradingAccountResponse.java           # Response DTO
│   ├── OrderRequest.java                     # Request DTO
│   └── OrderResponse.java                    # Response DTO
├── exception/
│   └── GlobalExceptionHandler.java           # Global exception handling
├── model/
│   ├── TradingAccount.java                   # JPA entity
│   ├── Order.java                            # JPA entity
│   └── Trade.java                            # JPA entity
├── repository/
│   ├── TradingAccountRepository.java         # Spring Data JPA
│   ├── OrderRepository.java                  # Spring Data JPA
│   └── TradeRepository.java                  # Spring Data JPA
└── service/
    ├── TradingAccountService.java            # Business logic
    └── OrderService.java                     # Business logic
```

## License

MIT
