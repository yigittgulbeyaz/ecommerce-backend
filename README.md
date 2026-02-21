# 🛒 E-Commerce Platform

A **full-stack e-commerce application** built with Spring Boot and React. Features a modern, responsive storefront with JWT authentication, product catalog, shopping cart, order management, and a comprehensive admin dashboard.

## ✨ Key Features

- **JWT Authentication** — Access + Refresh token mechanism with role-based access control
- **Product Catalog** — Browse, search, and filter products with pagination
- **Shopping Cart** — Add/remove items, update quantities, real-time cart sync
- **Order Management** — Checkout flow with order tracking and status updates
- **Admin Dashboard** — Statistics, user management, product/order CRUD
- **Swagger UI / OpenAPI 3.0** — Interactive API documentation
- **Modern UI** — Animated dashboard with glassmorphism, dark gradients, and micro-interactions

---

## 🏗️ Architecture

```
┌────────────────────────────────────────────────────────────┐
│              Frontend (React + TypeScript)                  │
│   Vite · Zustand · Framer Motion · React Router            │
└──────────────────────────┬─────────────────────────────────┘
                           │ REST API (JSON)
                           ▼
┌────────────────────────────────────────────────────────────┐
│              Backend (Spring Boot 3.4)                      │
│   Security (JWT) → Controllers → Services → Repositories   │
└──────────────────────────┬─────────────────────────────────┘
                           │ JPA / Hibernate
                           ▼
┌────────────────────────────────────────────────────────────┐
│              PostgreSQL 17 (Docker)                         │
└────────────────────────────────────────────────────────────┘
```

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | React 18, TypeScript, Vite |
| **State Management** | Zustand |
| **Animations** | Framer Motion |
| **Routing** | React Router v6 |
| **Styling** | Vanilla CSS (custom design system) |
| **Backend** | Spring Boot 3.4, Java 17 |
| **Database** | PostgreSQL 17 |
| **ORM** | Spring Data JPA / Hibernate |
| **Security** | Spring Security + JWT (jjwt 0.11.5) |
| **API Docs** | SpringDoc OpenAPI 2.7.0 |
| **Testing** | JUnit 5, Mockito, Testcontainers |
| **Containerization** | Docker |

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+**
- **Node.js 18+** & **npm**
- **Maven**
- **Docker & Docker Compose**

### 1. Start the Database

```bash
docker compose up -d
```

### 2. Create Environment File

Create a `.env` file in the project root:
```properties
POSTGRES_USER=ecommerce
POSTGRES_PASSWORD=yourpassword
POSTGRES_DB=ecommerce
JWT_SECRET=your-super-secret-jwt-key-min-32-characters
```

### 3. Run the Backend

```bash
mvn spring-boot:run
```
Backend will start at `http://localhost:8080`

### 4. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```
Frontend will start at `http://localhost:5173`

### 5. Access the Application

| Resource | URL |
|----------|-----|
| **Frontend App** | http://localhost:5173 |
| **API Base URL** | http://localhost:8080/api/v1 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |

---

## 📂 Project Structure

```
ecommerce/
├── src/                          # Spring Boot Backend
│   ├── main/java/com/yigit/ecommerce/
│   │   ├── common/               # ApiResponse, utilities
│   │   ├── config/               # OpenAPI, DataInitializer
│   │   ├── controller/           # REST controllers
│   │   │   ├── admin/            # Admin-only endpoints
│   │   │   ├── auth/             # Authentication
│   │   │   ├── cart/             # Shopping cart
│   │   │   └── order/            # Order management
│   │   ├── dto/                  # Request/Response DTOs (Java Records)
│   │   ├── exception/            # Custom exceptions + GlobalHandler
│   │   ├── mapper/               # Entity ↔ DTO mappers
│   │   ├── model/                # JPA entities
│   │   ├── repository/           # Spring Data JPA repositories
│   │   ├── security/             # JWT, filters, auth context
│   │   └── service/              # Business logic
│   └── resources/
│       └── application.yaml
│
├── frontend/                     # React Frontend
│   ├── src/
│   │   ├── api/                  # API client & service modules
│   │   ├── components/           # Reusable UI components (Header, Layout, Icons)
│   │   ├── pages/                # Page components
│   │   │   ├── Dashboard.tsx     # User dashboard with hero, quick actions
│   │   │   ├── Products.tsx      # Product catalog with search & pagination
│   │   │   ├── Cart.tsx          # Shopping cart
│   │   │   ├── Checkout.tsx      # Checkout flow
│   │   │   ├── Orders.tsx        # Order history
│   │   │   ├── AdminDashboard.tsx # Admin panel
│   │   │   ├── Login.tsx         # Authentication
│   │   │   └── ...
│   │   ├── stores/               # Zustand state (auth, cart)
│   │   └── index.css             # Design system & global styles
│   ├── package.json
│   └── vite.config.ts
│
├── docker-compose.yml
├── API_DOCUMENTATION.md
└── README.md
```

---

## 🔐 Authentication & Authorization

### JWT Flow
1. **Register/Login** → Receive access + refresh tokens
2. **API Requests** → Send `Authorization: Bearer <token>` header
3. **Token Expired** → Use refresh token to get a new access token

### Authorization Levels

| Level | Description |
|-------|-------------|
| **Public** | No auth required (products, categories, auth endpoints) |
| **USER** | Valid JWT required (cart, orders, profile) |
| **ADMIN** | JWT with ADMIN role (user/product/order management) |

---

## 📡 API Endpoints

| Service | Endpoint | Method | Auth |
|---------|----------|--------|------|
| Auth | `/auth/register` | POST | Public |
| Auth | `/auth/login` | POST | Public |
| Auth | `/auth/refresh` | POST | Public |
| User | `/users/me` | GET | USER |
| Categories | `/categories` | GET | Public |
| Products | `/products` | GET | Public |
| Products | `/products/{id}` | GET | Public |
| Cart | `/cart` | GET | USER |
| Cart | `/cart/items` | POST | USER |
| Orders | `/orders` | POST | USER |
| Admin | `/admin/users` | GET | ADMIN |
| Admin | `/admin/products` | POST | ADMIN |
| Admin | `/admin/orders/{id}/status` | PATCH | ADMIN |

> Full API documentation: [`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md)

---

## 🧪 Testing

```bash
# Run all backend tests
mvn test

# Run specific test class
mvn test -Dtest=CartServiceTest

# Run integration tests
mvn test -Dtest=*IntegrationTest
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 👤 Author

**Yigit Gulbeyaz**
GitHub: [@yigittgulbeyaz](https://github.com/yigittgulbeyaz)

---

*Last Updated: February 2026*
