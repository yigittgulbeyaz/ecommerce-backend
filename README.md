# E-Commerce Backend

## Introduction

### Overview of E-Commerce Backend
E-Commerce Backend is a modern, secure, and scalable RESTful API built using **Spring Boot 3.4**. It provides a complete backend solution for e-commerce applications including user authentication, product catalog management, shopping cart, order processing with payment integration, and comprehensive admin operations.

### Why This Architecture?
This project follows a **layered architecture** pattern with clear separation of concerns. Each layer has a specific responsibility, allowing for independent development, testing, and maintenance. The stateless JWT-based authentication ensures scalability and security.

### Project Architecture Diagram
```
+-------------------------------------------------------------+
|                      Client (Web/Mobile)                     |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                    Spring Boot Application                   |
|  +-------------+  +-------------+  +---------------------+  |
|  |  Security   |  | Controllers |  |  GlobalException    |  |
|  |  (JWT Auth) |  |  (REST API) |  |     Handler         |  |
|  +-------------+  +-------------+  +---------------------+  |
|                              |                               |
|                              v                               |
|  +-----------------------------------------------------+    |
|  |            Service Layer (Business Logic)           |    |
|  +-----------------------------------------------------+    |
|                              |                               |
|                              v                               |
|  +-----------------------------------------------------+    |
|  |         Repository Layer (Spring Data JPA)          |    |
|  +-----------------------------------------------------+    |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                PostgreSQL Database (Docker)                  |
+-------------------------------------------------------------+
```

### Key Features
- **JWT Authentication**: Access + Refresh token mechanism for secure, stateless authentication
- **Role-Based Access Control**: USER and ADMIN roles with granular endpoint permissions
- **RESTful API Design**: Versioned endpoints following REST best practices
- **Swagger UI / OpenAPI 3.0**: Interactive API documentation at `/swagger-ui.html`
- **Standardized Responses**: Consistent API response format across all endpoints
- **Comprehensive Validation**: Request validation with meaningful error messages
- **Pagination Support**: Efficient data retrieval for list endpoints
- **Containerized Database**: Docker-based PostgreSQL for easy setup

---

## Getting Started

### Prerequisites
Before running E-Commerce Backend, ensure you have the following installed:
- **Java 17** or higher
- **Maven** (latest version)
- **Docker & Docker Compose**
- **Postman** (optional, for API testing)

### Running the Project

**1. Start the database using Docker:**
```bash
docker compose up -d
```

**2. Create environment file (.env) in project root:**
```properties
POSTGRES_USER=ecommerce
POSTGRES_PASSWORD=yourpassword
POSTGRES_DB=ecommerce
JWT_SECRET=your-super-secret-jwt-key-min-32-characters
```

**3. Run the application:**
```bash
mvn spring-boot:run
```

**4. Access the application:**
- **API Base URL**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Swagger UI
Swagger UI is integrated for interactive API documentation and testing.

**Access Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**To test authenticated endpoints:**
1. Execute `/auth/login` with valid credentials
2. Copy the `accessToken` from the response
3. Click the **Authorize** button (top right)
4. Enter: `Bearer <your-token>`
5. Click **Authorize** to apply

### Database Access
To access the PostgreSQL database:
```bash
psql -U ecommerce -d ecommerce -h localhost -p 5433
# Password: yourpassword (from .env)
```

---

## Project Architecture

### Technology Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 3.4.12 |
| Language | Java 17 |
| Database | PostgreSQL 17 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| API Docs | SpringDoc OpenAPI 2.7.0 |
| Validation | Jakarta Validation |
| Build Tool | Maven |
| Containerization | Docker |
| Testing | JUnit 5, Mockito, Testcontainers |

### Layered Architecture

| Layer | Responsibility |
|-------|----------------|
| **Controller** | HTTP request handling, routing, request/response mapping |
| **Service** | Business logic, transaction management, validation |
| **Repository** | Database access via Spring Data JPA |
| **Security** | JWT authentication, authorization, filters |
| **Exception** | Centralized error handling with GlobalExceptionHandler |
| **DTO** | Data transfer objects for request/response |
| **Mapper** | Entity to DTO conversion |

---

## Authentication & Authorization

### JWT Authentication Flow
```
1. User Registration
   POST /api/v1/auth/register
   -> Returns: User data + JWT tokens

2. User Login
   POST /api/v1/auth/login
   -> Returns: Access token + Refresh token

3. API Access
   Authorization: Bearer <access_token>
   -> Validates token, extracts user principal

4. Token Refresh
   POST /api/v1/auth/refresh?refreshToken=<token>
   -> Returns: New access token
```

### Authorization Levels

| Level | Description |
|-------|-------------|
| **Public** | No authentication required (products, categories, auth) |
| **USER** | Requires valid JWT token (cart, orders, addresses, profile) |
| **ADMIN** | Requires JWT token with ADMIN role (user/product/order management) |

---

## Endpoint Examples

All API endpoints are structured under `http://localhost:8080/api/v1/`

| Service | Endpoint | Method | Auth | Description |
|---------|----------|--------|------|-------------|
| Auth | `/auth/register` | POST | Public | User registration |
| Auth | `/auth/login` | POST | Public | User authentication |
| Auth | `/auth/refresh` | POST | Public | Refresh access token |
| User | `/users/me` | GET | USER | Get current user profile |
| Categories | `/categories` | GET | Public | List all categories |
| Products | `/products` | GET | Public | List products (paginated) |
| Products | `/products/{id}` | GET | Public | Get product details |
| Cart | `/cart` | GET | USER | Get user's cart |
| Cart | `/cart/items` | POST | USER | Add item to cart |
| Addresses | `/addresses` | GET | USER | List user's addresses |
| Orders | `/orders` | POST | USER | Checkout (create order) |
| Admin | `/admin/users` | GET | ADMIN | List all users |
| Admin | `/admin/products` | POST | ADMIN | Create product |
| Admin | `/admin/orders/{id}/status` | PATCH | ADMIN | Update order status |

**Full API documentation available in [`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md)**

---

## Standardized API Response

All API responses follow a uniform structure:

**Success Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Operation completed successfully",
  "data": { ... },
  "errors": null,
  "timestamp": "2026-01-26T12:00:00.000Z"
}
```

**Error Response:**
```json
{
  "success": false,
  "status": 422,
  "message": "Validation failed",
  "data": null,
  "errors": {
    "email": "Email format is invalid"
  },
  "timestamp": "2026-01-26T12:00:00.000Z"
}
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/yigit/ecommerce/
│   │   ├── common/          # ApiResponse, utilities
│   │   ├── config/          # OpenAPI configuration
│   │   ├── controller/      # REST controllers
│   │   │   ├── admin/       # Admin-only endpoints
│   │   │   ├── auth/        # Authentication
│   │   │   ├── cart/        # Shopping cart
│   │   │   ├── order/       # Order management
│   │   │   └── ...
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── exception/       # Custom exceptions + GlobalHandler
│   │   ├── mapper/          # Entity <-> DTO mappers
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── security/        # JWT, filters, auth context
│   │   └── service/         # Business logic
│   └── resources/
│       ├── application.yaml
│       └── application-env.yml
└── test/
    └── java/com/yigit/ecommerce/
        ├── controller/      # Controller tests
        ├── integration/     # Integration tests
        └── service/         # Unit tests
```

---

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CartServiceTest

# Run integration tests only
mvn test -Dtest=*IntegrationTest
```

### Test Coverage
- Unit tests for services (Mockito)
- Controller tests (MockMvc)
- Integration tests (Testcontainers + PostgreSQL)

---

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

For any questions, open an issue or reach out to the project maintainer.

---

## Author

**Yigit Gulbeyaz**  
GitHub: [@yigittgulbeyaz](https://github.com/yigittgulbeyaz)

---

*Last Updated: January 2026*
