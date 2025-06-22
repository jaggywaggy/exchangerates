# Exchange Rates API

A robust Spring Boot microservice that aggregates exchange rates from multiple external APIs, providing averaged rates with comprehensive metrics and caching capabilities.

## 🚀 Features

- **Multi-Source Rate Aggregation**: Fetches live exchange rates from multiple reliable sources:
  - [Frankfurter.app](https://frankfurter.app/) - European Central Bank rates
  - [Fawazahmed0/currency-api](https://github.com/fawazahmed0/currency-api) - Community-driven rates
- **Intelligent Rate Averaging**: Combines rates from multiple sources for improved accuracy
- **In-Memory Caching**: Concurrent HashMap-based caching for performance optimization
- **Comprehensive Metrics**: Real-time monitoring of API usage, requests, and responses
- **RESTful API**: Clean, REST-compliant endpoints with proper error handling
- **Health Monitoring**: Built-in health checks and application metrics
- **Thread-Safe Design**: Concurrent operations with proper synchronization

## 🛠️ Technology Stack

- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.4.5** - Latest stable version with enhanced performance
- **Gradle** - Modern build system with dependency management
- **JUnit 5** - Comprehensive testing framework
- **Mockito** - Mocking framework for unit testing
- **Jackson** - JSON processing library

## 📋 Prerequisites

- Java 21 or higher
- Gradle 8.0 or higher (or use the included Gradle wrapper)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd exchangerates
```

### 2. Build the Application
```bash
./gradlew build
```

### 3. Run the Application
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Get Exchange Rates
```
GET /exchangeRates/{base}?symbols=USD,EUR,GBP
```

**Parameters:**
- `base` (path): Base currency code (e.g., USD, EUR, GBP)
- `symbols` (query): Comma-separated list of target currencies

**Example Request:**
```bash
curl "http://localhost:8080/exchangeRates/USD?symbols=EUR,GBP,JPY"
```

**Example Response:**
```json
{
  "base": "USD",
  "rates": {
    "EUR": 0.85,
    "GBP": 0.73,
    "JPY": 110.25
  }
}
```

### Get Application Metrics
```
GET /metrics
```

**Example Response:**
```json
{
  "totalQueries": 150,
  "apis": [
    {
      "name": "frankfurter",
      "metrics": {
        "totalRequests": 75,
        "totalResponses": 75
      }
    },
    {
      "name": "fawaz",
      "metrics": {
        "totalRequests": 75,
        "totalResponses": 75
      }
    }
  ]
}
```

### Health Check
```
GET /actuator/health
```

## 🏗️ Architecture

### Design Patterns
- **Service Layer Pattern**: Business logic separated from controllers
- **Repository Pattern**: Data access abstraction (in-memory cache)
- **Strategy Pattern**: Multiple API providers with common interface
- **Observer Pattern**: Metrics collection across services

### Key Components
- **Controllers**: REST API endpoints with input validation
- **Services**: Business logic implementation with caching
- **Models**: Data transfer objects and domain models
- **Interfaces**: Contract definitions for loose coupling

### Caching Strategy
- **Cache Key**: `{base}|{sorted_symbols}` (e.g., "USD|EUR,GBP,JPY")
- **Cache Implementation**: Thread-safe ConcurrentHashMap
- **Cache Scope**: Application lifetime (in-memory)

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Test Coverage
- **Unit Tests**: Service layer business logic
- **Integration Tests**: API endpoint testing
- **Validation Tests**: Input parameter validation

### Test Structure
```
src/test/java/com/cj/exchangerates/
├── service/
│   ├── ExchangeRateServiceTests.java
│   └── MetricsServiceTests.java
└── ExchangeratesApplicationTests.java
```

## 🔧 Configuration

### Application Properties
```properties
# Application name
spring.application.name=exchangerates

# Server configuration
server.port=8080

# Logging
logging.level.com.cj.exchangerates=INFO
logging.level.org.springframework.web=INFO
```

### Environment Variables
- `SERVER_PORT`: Custom server port (default: 8080)
- `SPRING_PROFILES_ACTIVE`: Active profile (dev, prod, test)

## 📊 Monitoring and Observability

### Metrics Endpoints
- `/metrics` - Custom application metrics
- `/actuator/health` - Application health status
- `/actuator/info` - Application information

### Logging
- Structured logging with Spring Boot defaults
- Request/response logging for debugging
- Error logging with stack traces

## 🚀 Deployment

### Docker (Recommended)
```bash
# Build Docker image
docker build -t exchangerates .

# Run container
docker run -p 8080:8080 exchangerates
```

### Traditional Deployment
```bash
# Build JAR
./gradlew bootJar

# Run JAR
java -jar build/libs/exchangerates-0.0.1-SNAPSHOT.jar
```

## 🔄 Future Enhancements

### Planned Improvements
- [ ] **Database Integration**: Persistent storage for historical rates
- [ ] **Redis Caching**: Distributed caching for scalability
- [ ] **Rate Limiting**: API rate limiting and throttling
- [ ] **Circuit Breaker**: Resilience patterns for external APIs
- [ ] **Swagger Documentation**: Interactive API documentation
- [ ] **Docker Compose**: Multi-service development environment
- [ ] **CI/CD Pipeline**: Automated testing and deployment
- [ ] **Monitoring**: Prometheus metrics and Grafana dashboards

### Technical Debt
- [ ] **External API DTOs**: Proper deserialization objects
- [ ] **Cache Expiration**: Time-based cache invalidation
- [ ] **Error Handling**: Comprehensive exception handling
- [ ] **Validation**: Enhanced input validation
- [ ] **Security**: API authentication and authorization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.



---

**Note**: This project demonstrates modern Spring Boot development practices, including microservice architecture, caching strategies, comprehensive testing, and API design principles suitable for production environments.
