# Loan Module API

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.java.com/)
[![Spring Boot 3.5.13](https://img.shields.io/badge/Spring%20Boot-3.5.13-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9.x-blue.svg)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Production-Ready Loan Management System**

A comprehensive RESTful API for managing loan applications with built-in eligibility checking, credit scoring, EMI calculation, and approval workflows.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
  - [Development](#development)
  - [Production](#production)
  - [Docker](#docker)
- [Configuration](#configuration)
  - [Environment Variables](#environment-variables)
  - [Profiles](#profiles)
- [API Documentation](#api-documentation)
- [API Endpoints](#api-endpoints)
- [Request/Response Examples](#requestresponse-examples)
- [Validation Rules](#validation-rules)
- [Error Handling](#error-handling)
- [Database](#database)
- [Testing](#testing)
- [Monitoring](#monitoring)
- [Security Considerations](#security-considerations)
- [Deployment](#deployment)
- [Postman Collection](#postman-collection)
- [Project Structure](#project-structure)
- [Technologies](#technologies)
- [Contributing](#contributing)
- [License](#license)

---

## Features

| Feature | Description |
|---------|-------------|
| **Loan Application Management** | Create, retrieve, update, and delete loan applications |
| **Multiple Loan Types** | Support for HOME_LOAN, PERSONAL_LOAN, CAR_LOAN, EDUCATION_LOAN, GOLD_LOAN |
| **Gold Loan Support** | Secured loans against gold with weight, carat, and item description validation |
| **Credit Score Integration** | CIBIL score validation with configurable thresholds |
| **Eligibility Calculation** | Automatic eligibility determination based on income, CIBIL, and loan type |
| **EMI Calculator** | Monthly EMI calculation with rate of interest |
| **Processing Fee Calculation** | Loan type-specific processing fees |
| **API Versioning** | Versioned REST endpoints for backward compatibility |
| **Comprehensive Validation** | Input validation with detailed error messages |
| **Audit Trail** | Automatic createdAt, updatedAt timestamps |
| **Swagger Documentation** | Interactive API documentation |
| **Actuator Endpoints** | Health checks, metrics, and monitoring |
| **H2 Database Console** | Web-based database management (dev only) |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Applications                        │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Loan Module API                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────────┐ │
│  │  Controller │ Industries    │  │             Service Layer    │ │
│  │   (REST)    │──────────────▶│  │  ┌─────────────────────────┐ │ │
│  └─────────────┘  │              │  │  │ LoanApplicationService    │ │ │
│                   │              │  │  │ LoanEligibilityService     │ │ │
│  ┌─────────────┐  │              │  │  │ EmiCalculatorService       │ │ │
│  │   Validation│◀──────────────┘  │  │  │ CreditBureauService        │ │ │
│  │   Layer     │                 │  │  └─────────────────────────┘ │ │
│  └─────────────┘                 │  └─────────────────────────────┘ │
│                                      │                              │
│                                      ▼                              │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    Data Access Layer                         │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌───────────────────────┐  │ │
│  │  │  Repository │  │   Mapper     │  │    Entity              │  │ │
│  │  │   (Spring  │  │  (MapStruct)  │  │   (JPA/Hibernate)      │  │ │
│  │  │    Data)    │  │              │  │                       │  │ │
│  │  └─────────────┘  └─────────────┘  └───────────────────────┘  │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                │                                      │
│                                ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                        H2 Database                           │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## Prerequisites

| Component | Version | Download |
|-----------|---------|----------|
| Java | 21+ | [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) / [OpenJDK](https://adoptium.net/) |
| Maven | 3.9.x+ | [Maven](https://maven.apache.org/download.cgi) |
| Memory | 512MB+ | - |
| Disk Space | 200MB+ | - |

---

## Quick Start

### Development

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-organization/loan-module.git
   cd loan-module
   ```

2. **Run the application:**
   ```bash
   # Using Maven Wrapper (recommended)
   ./mvnw spring-boot:run
   
   # Or using Maven directly
   mvn spring-boot:run
   ```

3. **Access the application:**
   - API: `http://localhost:8080/api/v1/loans`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:loandb`)

### Production

1. **Build the application:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. **Run the JAR file:**
   ```bash
   # Development mode
   java -jar target/loan-module-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
   
   # Production mode
   java -jar target/loan-module-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   ```

3. **With custom port and database password:**
   ```bash
   java -jar target/loan-module-0.0.1-SNAPSHOT.jar \
     --server.port=9090 \
     --spring.profiles.active=prod \
     --DB_PASSWORD=mysecretpassword
   ```

### Docker

1. **Build the Docker image:**
   ```bash
   docker build -t loan-module:latest .
   ```

2. **Run the container:**
   ```bash
   # Development
   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev loan-module:latest
   
   # Production
   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod -e DB_PASSWORD=mysecretpassword loan-module:latest
   ```

3. **Docker Compose (recommended for production):**
   ```yaml
   version: '3.8'
   services:
     loan-module:
       image: loan-module:latest
       ports:
         - "8080:8080"
       environment:
         - SPRING_PROFILES_ACTIVE=prod
         - DB_PASSWORD=${DB_PASSWORD}
         - SERVER_PORT=8080
       volumes:
         - ./loan-db:/root/loan-db
       restart: unless-stopped
   ```

---

## Configuration

### Environment Variables

| Variable | Default | Description | Required |
|----------|---------|-------------|----------|
| `SERVER_PORT` | 8080 | Application server port | No |
| `SPRING_PROFILES_ACTIVE` | dev | Active Spring profile | No |
| `DB_PASSWORD` | (empty) | H2 database password (prod) | No |
| `JPA_SHOW_SQL` | false | Show SQL queries in logs | No |
| `HIBERNATE_FORMAT_SQL` | true | Format SQL queries | No |
| `H2_CONSOLE_ENABLED` | true | Enable H2 web console | No |
| `LOG_LEVEL` | INFO | Root logging level | No |
| `APP_LOG_LEVEL` | DEBUG | Application logging level | No |
| `SWAGGER_ENABLED` | true | Enable Swagger UI | No |

### Profiles

| Profile | Description | Database | H2 Console |
|---------|-------------|----------|------------|
| `dev` | Development environment | In-memory H2 | Enabled |
| `prod` | Production environment | File-based H2 | Disabled |
| `test` | Testing environment | In-memory H2 | Disabled |

---

## API Documentation

### Swagger/OpenAPI

Interactive API documentation is available at:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`

### Redoc

Alternative documentation:
- **Redoc:** `http://localhost:8080/api-docs/swagger-ui.html`

---

## API Endpoints

### Base URL
```
http://localhost:8080/api/v1/loans
```

### Loan Applications

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/apply` | Create a new loan application | No |
| GET | `/{id}` | Get loan application by ID | No |
| GET | `/pan/{panNumber}` | Get all applications by PAN | No |
| GET | `/status/{status}` | Get applications by status | No |
| GET | ` `/ | Get all loan applications | No |
| PUT | `/{id}` | Update loan application | No |
| DELETE | `/{id}` | Delete loan application | No |

### Actuator Endpoints (Production Monitoring)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Health check |
| GET | `/actuator/info` | Application info |
| GET | `/actuator/metrics` | Metrics |
| GET | `/actuator/env` | Environment variables |
| GET | `/actuator/loggers` | Logger configuration |

---

## Request/Response Examples

### Create Loan Application

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/loans/apply \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "panNumber": "ABCDE1234F",
    "monthlyIncome": 150000.00,
    "requestedAmount": 5000000.00,
    "loanType": "HOME_LOAN",
    "tenureInMonths": 240,
    "propertyValue": 7500000.00,
    "propertyAddress": "123 Main Street, City, Country",
    "isSelfOccupied": true,
    "employmentType": "SALARIED",
    "companyName": "Tech Corp",
    "yearsOfService": 10
  }'
```

**Response (Success):**
```json
{
  "success": true,
  "data": {
    "applicationId": 1,
    "applicationReferenceNumber": "LOAN-2024-00001",
    "fullName": "John Doe",
    "panNumber": "ABCDE1234F",
    "monthlyIncome": 150000.00,
    "requestedAmount": 5000000.00,
    "loanType": "HOME_LOAN",
    "tenureInMonths": 240,
    "cibilScore": 780,
    "eligibleAmount": 6000000.00,
    "rateOfInterest": 7.5,
    "status": "APPROVED",
    "sanctionedAmount": 5000000.00,
    "processingFee": 50000.00,
    "monthlyEmi": 38194.16,
    "propertyValue": 7500000.00,
    "propertyAddress": "123 Main Street, City, Country",
    "isSelfOccupied": true,
    "createdAt": "2024-04-18T10:00:00"
  },
  "message": "Loan application submitted successfully",
  "timestamp": "2024-04-18T10:00:00.000Z"
}
```

**Response (Error):**
```json
{
  "success": false,
  "error": {
    "message": "CIBIL score 550 is below the minimum required score of 650",
    "code": "LOAN_ELIGIBILITY_001",
    "details": "Minimum CIBIL score requirement not met"
  },
  "message": "Loan eligibility check failed",
  "timestamp": "2024-04-18T10:00:00.000Z"
}
```

---

## Validation Rules

### Field Validations

| Field | Required | Min | Max | Pattern | Notes |
|-------|----------|-----|-----|---------|-------|
| `fullName` | Yes | 2 | 100 | Alphanumeric + spaces | - |
| `panNumber` | Yes | 10 | 10 | `[A-Z]{5}[0-9]{4}[A-Z]{1}` | Unique |
| `monthlyIncome` | Yes | 10000 | - | - | Configured via `loan.application.min-income` |
| `requestedAmount` | Yes | 1000 | 10000000 | - | Configured via `loan.application.min-amount`, `loan.application.max-amount` |
| `tenureInMonths` | Yes | 1 | 360 | - | Configured via `loan.application.max-tenure-months` |
| `loanType` | Yes | - | - | Enum | HOME_LOAN, PERSONAL_LOAN, CAR_LOAN, EDUCATION_LOAN, GOLD_LOAN |

### Eligibility Rules

```
1. CIBIL Score >= 650 (configurable via loan.cibil.min-eligible-score)
2. Monthly Income >= 10,000 (configurable)
3. Loan Amount within Min/Max limits
4. Tenure within Max allowed months
5. Home Loan: propertyValue >= requestedAmount * 1.5
6. Gold Loan: goldCarat >= 18, goldWeightInGrams > 0, tenureInMonths <= 60
7. Gold Loan: loan amount per gram must be within LTV ratio (max 80% of gold value)
```

---

## Error Handling

### Error Response Structure

```json
{
  "success": false,
  "error": {
    "message": "Error description",
    "code": "ERROR_CODE",
    "details": "Additional context"
  },
  "timestamp": "ISO timestamp"
}
```

### Common Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `LOAN_ELIGIBILITY_001` | 400 | CIBIL score below minimum |
| `LOAN_ELIGIBILITY_002` | 400 | Monthly income below minimum |
| `LOAN_ELIGIBILITY_003` | 400 | Requested amount exceeds maximum |
| `LOAN_ELIGIBILITY_004` | 400 | Requested amount below minimum |
| `LOAN_ELIGIBILITY_005` | 400 | Property value insufficient for home loan |
| `LOAN_REQUEST_001` | 400 | Invalid PAN number format |
| `LOAN_REQUEST_002` | 400 | PAN number already exists |
| `RESOURCE_NOT_FOUND` | 404 | Loan application not found |
| `INVALID_LOAN_REQUEST` | 400 | General validation failure |
| `LOAN_PROCESSING_001` | 500 | Internal processing error |

---

## Database

### H2 Database Configuration

- **Development:** In-memory database (data lost on restart)
- **Production:** File-based database (persistent storage)
- **Test:** In-memory database (isolated for tests)

### Database Location (Production)
```
~/loan-db/loandb.mv.db    # Database file
~/loan-db/loandb.trace.db  # Transaction log
```

### Schema

The application uses JPA/Hibernate with automatic schema generation:
- `ddl-auto: update` in dev (updates schema on startup)
- `ddl-auto: create-drop` in test (creates and drops on startup)

### Backup

For production, regularly backup the `~/loan-db` directory.

---

## Testing

### Run Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=LoanModuleApplicationTests

# Run with coverage
./mvnw test -Pcoverage
```

### Test Coverage

- Unit tests for service layer
- Integration tests for controller endpoints
- Mock tests for external dependencies

---

## Monitoring

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application metadata |
| `/actuator/metrics` | Application metrics |
| `/actuator/health/readiness` | Readiness probe |
| `/actuator/health/liveness` | Liveness probe |

### Logging

- **File:** `logs/loan-module.log`
- **Format:** `%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n`
- **Levels:** TRACE, DEBUG, INFO, WARN, ERROR

### Configure Logging

```bash
# Set log level via environment
java -jar loan-module.jar --APP_LOG_LEVEL=TRACE

# Dynamic log level change (via actuator)
POST /actuator/loggers/com.demo.loan_module
{
  "configuredLevel": "DEBUG"
}
```

---

## Security Considerations

### Production Recommendations

1. **Disable Swagger in Production:**
   ```bash
   java -jar loan-module.jar --SWAGGER_ENABLED=false
   ```

2. **Disable H2 Console in Production:**
   ```yaml
   # Already disabled in prod profile
   spring:
     h2:
       console:
         enabled: false
   ```

3. **Use HTTPS:**
   ```yaml
   server:
     ssl:
       enabled: true
       key-store: classpath:keystore.p12
       key-store-password: ${SSL_PASSWORD}
       key-store-type: PKCS12
   ```

4. **Add Authentication:**
   - Integrate with Spring Security
   - Use JWT or OAuth2 for API authentication

5. **Database Security:**
   - Set strong DB_PASSWORD in production
   - Restrict file system permissions on database files

6. **Network Security:**
   - Run behind a reverse proxy (Nginx, Apache)
   - Enable firewall rules
   - Use Docker network isolation

---

## Deployment

### Production Checklist

- [ ] Set `SPRING_PROFILES_ACTIVE=prod`
- [ ] Configure `DB_PASSWORD`
- [ ] Disable Swagger (`SWAGGER_ENABLED=false`)
- [ ] Configure proper logging level
- [ ] Set up HTTPS/TLS
- [ ] Configure authentication/authorization
- [ ] Set up monitoring and alerts
- [ ] Configure health checks
- [ ] Set up database backup
- [ ] Configure reverse proxy
- [ ] Set up CI/CD pipeline

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: loan-module
spec:
  replicas: 2
  selector:
    matchLabels:
      app: loan-module
  template:
    metadata:
      labels:
        app: loan-module
    spec:
      containers:
      - name: loan-module
        image: loan-module:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: loan-db-secrets
              key: password
        - name: SERVER_PORT
          value: "8080"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        volumeMounts:
        - name: loan-db
          mountPath: /root/loan-db
      volumes:
      - name: loan-db
        persistentVolumeClaim:
          claimName: loan-db-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: loan-module
spec:
  selector:
    app: loan-module
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

---

## Postman Collection

A Postman collection is included for testing the API:
- **Location:** `src/main/resources/postman-collections/Loan Module.postman_collection.json`

### Import Instructions

1. Open Postman
2. Click "Import"
3. Select the JSON file
4. Import the collection

### Collection Includes

- Create Loan Application (Home Loan, Personal Loan, Car Loan, Education Loan, Gold Loan)
- Get Loan Application by ID
- Get All Loan Applications
- Get Applications by PAN
- Get Applications by Status
- Update Loan Application
- Delete Loan Application
- Error scenarios and edge cases
- Gold Loan specific test cases with gold weight, carat, and item description

---

## Project Structure

```
loan-module/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── demo/
│   │   │           └── loan_module/
│   │   │               ├── controller/           # REST Controllers
│   │   │               ├── dto/                   # Data Transfer Objects
│   │   │               │   ├── request/           # Request DTOs
│   │   │               │   └── response/          # Response DTOs
│   │   │               ├── entity/                # JPA Entities
│   │   │               ├── enums/                 # Enumerations
│   │   │               ├── exception/             # Custom Exceptions
│   │   │               ├── mapper/                # MapStruct Mappers
│   │   │               ├── model/                 # Domain Models
│   │   │               ├── repository/            # Spring Data Repositories
│   │   │               ├── service/               # Service Interfaces
│   │   │               │   └── impl/               # Service Implementations
│   │   │               ├── validation/            # Validation Classes
│   │   │               └── LoanModuleApplication.java  # Main class
│   │   └── resources/
│   │       ├── application.yml           # Configuration
│   │       ├── postman-collections/      # Postman collections
│   │       ├── static/                   # Static resources
│   │       └── templates/                # Thymeleaf templates
│   └── test/
│       └── java/
│           └── com/
│               └── demo/
│                   └── loan_module/
│                       └── LoanModuleApplicationTests.java
├── pom.xml                              # Maven configuration
├── mvnw                                # Maven Wrapper
├── mvnw.cmd                            # Maven Wrapper (Windows)
├── .gitignore
├── .gitattributes
└── README.md
```

---

## Technologies

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Runtime** | Java | 21 | Core language |
| **Framework** | Spring Boot | 3.5.13 | Application framework |
| **Build** | Maven | 3.9.x | Dependency management |
| **Database** | H2 Database | 2.x | Embedded database |
| **ORM** | Spring Data JPA + Hibernate | 6.x | Object-relational mapping |
| **Validation** | Hibernate Validator | 8.x | Input validation |
| **API Docs** | SpringDoc OpenAPI | 2.5.0 | API documentation |
| **Logging** | SLF4J + Logback | 2.x | Logging framework |
| **Testing** | JUnit 5 + Mockito | 5.x | Unit testing |
| **Code Quality** | Lombok | 1.18.x | Boilerplate reduction |
| **Mapping** | MapStruct | 1.5.5.Final | Object mapping |

---

## Contributing

### Getting Started

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow Spring Boot conventions
- Use Lombok annotations where appropriate
- Add Javadoc for public methods
- Write unit tests for new features
- Keep methods focused and single-purpose

### Git Commit Messages

```
type(scope): subject

body

footer
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Demo Organization

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Support

For issues, questions, or feature requests:

- **Issues:** [GitHub Issues](https://github.com/your-organization/loan-module/issues)
- **Discussions:** [GitHub Discussions](https://github.com/your-organization/loan-module/discussions)
- **Email:** support@demo.com

---

*Built with Spring Boot and Java 21*

*Documentation last updated: April 18, 2026*
