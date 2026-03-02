# 🛒 E-Commerce Platform

A **full-stack e-commerce application** built with **Spring Boot** and **React**. Features a modern, responsive storefront with JWT authentication, product catalog, shopping cart, order management, and a comprehensive admin dashboard.

> **🌐 Live Demo**
> - **Frontend** → [e-commerce-platform-seven-eta.vercel.app](https://e-commerce-platform-seven-eta.vercel.app)
> - **Backend API** → [ecommerce-backend-production-b148.up.railway.app](https://ecommerce-backend-production-b148.up.railway.app/api/v1)
> - **Swagger UI** → [ecommerce-backend-production-b148.up.railway.app/swagger-ui.html](https://ecommerce-backend-production-b148.up.railway.app/swagger-ui.html)

---

## ✨ Key Features

- **JWT Authentication** — Access + Refresh token mechanism with role-based access control
- **Product Catalog** — Browse, search, and filter products with pagination & sorting
- **Shopping Cart** — Add/remove items, update quantities, real-time cart sync
- **Order Management** — Checkout flow with payment processing & order tracking
- **Address Management** — CRUD for shipping addresses
- **Admin Dashboard** — Revenue stats, user management, product/order/category CRUD, cart moderation
- **Swagger UI / OpenAPI 3.0** — Interactive API documentation
- **Modern UI** — Animated dashboard with glassmorphism, dark gradients, and micro-interactions

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────┐
│           Frontend (React 18 + TypeScript + Vite)            │
│   Zustand · Framer Motion · React Router · Axios             │
│                  Deployed on Vercel                          │
└───────────────────────────┬──────────────────────────────────┘
                            │ REST API (JSON)
                            ▼
┌──────────────────────────────────────────────────────────────┐
│             Backend (Spring Boot 3.4.12 + Java 17)           │
│   Security (JWT) → Controllers → Services → Repositories    │
│                  Deployed on Railway                         │
└───────────────────────────┬──────────────────────────────────┘
                            │ JPA / Hibernate
                            ▼
┌──────────────────────────────────────────────────────────────┐
│                   PostgreSQL 17 (Railway)                     │
└──────────────────────────────────────────────────────────────┘
```

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | React 18, TypeScript, Vite 5 |
| **State Management** | Zustand 4 |
| **HTTP Client** | Axios |
| **Animations** | Framer Motion 11 |
| **Routing** | React Router v6 |
| **Styling** | Vanilla CSS (custom design system) |
| **Backend** | Spring Boot 3.4.12, Java 17 |
| **Database** | PostgreSQL 17 |
| **ORM** | Spring Data JPA / Hibernate |
| **Security** | Spring Security + JWT (jjwt 0.11.5) |
| **Validation** | Jakarta Bean Validation |
| **API Docs** | SpringDoc OpenAPI 2.7.0 |
| **Testing** | JUnit 5, Mockito, Testcontainers 1.20.4 |
| **Deployment** | Railway (Backend + DB), Vercel (Frontend) |

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+**
- **Node.js 18+** & **npm**
- **Maven**
- **Docker & Docker Compose** (for local PostgreSQL)

### 1. Clone the Repository

```bash
git clone https://github.com/yigittgulbeyaz/ecommerce-backend.git
cd ecommerce-backend
```

### 2. Start the Database

```bash
docker compose up -d
```

### 3. Create Environment File

Copy `.env.example` to `.env` and fill in your local secrets:
```properties
DB_PASSWORD=your_local_db_password
JWT_SECRET=your_jwt_secret_key
```

### 4. Run the Backend

```bash
mvn spring-boot:run
```
Backend will start at `http://localhost:8080`

### 5. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```
Frontend will start at `http://localhost:5173`

### 6. Access the Application

| Resource | Local URL |
|----------|-----------|
| **Frontend App** | http://localhost:5173 |
| **API Base URL** | http://localhost:8080/api/v1 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |

---

## ☁️ Deployment

### Backend — Railway

The backend is deployed on **[Railway](https://railway.app)** with a managed PostgreSQL instance.

**Required environment variables on Railway:**

| Variable | Description |
|----------|-------------|
| `DATABASE_URL` | Auto-set by Railway's PostgreSQL plugin |
| `JWT_SECRET` | Secret key for JWT signing (min 32 chars) |
| `CORS_ALLOWED_ORIGINS` | Frontend URL (e.g. `https://e-commerce-platform-seven-eta.vercel.app`) |
| `PORT` | Auto-set by Railway |

The `DataSourceConfig` class automatically parses the `DATABASE_URL` environment variable when present, extracting the JDBC URL, username, and password for HikariCP.

### Frontend — Vercel

The frontend is deployed on **[Vercel](https://vercel.com)** as a static SPA with client-side routing.

**Required environment variables on Vercel:**

| Variable | Description |
|----------|-------------|
| `VITE_API_URL` | Backend API base URL (e.g. `https://ecommerce-backend-production-b148.up.railway.app/api/v1`) |

A `vercel.json` is included to rewrite all routes to `index.html` for SPA support.

---

## 📂 Project Structure

```
ecommerce/
├── src/                          # Spring Boot Backend
│   ├── main/java/com/yigit/ecommerce/
│   │   ├── common/               # ApiResponse wrapper, utilities
│   │   ├── config/               # OpenAPI, DataInitializer, DataSourceConfig, CORS
│   │   ├── controller/           # REST controllers
│   │   │   ├── admin/            # Admin endpoints (users, products, orders, categories, carts, stats)
│   │   │   ├── auth/             # Authentication (register, login, refresh)
│   │   │   ├── cart/             # Shopping cart
│   │   │   ├── category/         # Public category browsing
│   │   │   ├── order/            # Order management
│   │   │   ├── product/          # Public product catalog
│   │   │   ├── address/          # Address management
│   │   │   └── user/             # User profile
│   │   ├── dto/                  # Request/Response DTOs (Java Records)
│   │   ├── exception/            # Custom exceptions + GlobalExceptionHandler
│   │   ├── mapper/               # Entity ↔ DTO mappers
│   │   ├── model/                # JPA entities
│   │   ├── repository/           # Spring Data JPA repositories
│   │   ├── security/             # JWT filter, provider, auth context
│   │   └── service/              # Business logic + admin services
│   └── resources/
│       ├── application.yaml      # Main config (env-driven)
│       └── application-dev.yaml  # Local dev overrides
│
├── frontend/                     # React Frontend
│   ├── src/
│   │   ├── api/                  # Axios client + service modules
│   │   ├── components/           # Reusable UI components (Header, Layout, Icons)
│   │   ├── pages/                # Page components
│   │   │   ├── LandingPage.tsx   # Public landing page
│   │   │   ├── Dashboard.tsx     # User dashboard
│   │   │   ├── Products.tsx      # Product catalog with search & pagination
│   │   │   ├── ProductDetail.tsx # Single product view
│   │   │   ├── Cart.tsx          # Shopping cart
│   │   │   ├── Checkout.tsx      # Checkout flow with payment
│   │   │   ├── Orders.tsx        # Order history
│   │   │   ├── Profile.tsx       # User profile management
│   │   │   ├── AdminDashboard.tsx# Admin panel with stats
│   │   │   ├── AdminUsers.tsx    # Admin user management
│   │   │   └── Login / Register  # Authentication pages
│   │   ├── stores/               # Zustand state (auth, cart)
│   │   └── index.css             # Design system & global styles
│   ├── vercel.json               # Vercel SPA rewrite config
│   ├── package.json
│   └── vite.config.ts
│
├── docker-compose.yml            # Local PostgreSQL
├── .env.example                  # Environment variable template
├── API_DOCUMENTATION.md          # Full API reference
└── README.md
```

---

## 🔐 Authentication & Authorization

### JWT Flow
1. **Register / Login** → Receive access token + refresh token
2. **API Requests** → Send `Authorization: Bearer <token>` header
3. **Token Expired** → `POST /auth/refresh?refreshToken=<token>` → New access token
4. **Refresh Failed** → Auto-redirect to login

### Authorization Levels

| Level | Description |
|-------|-------------|
| **Public** | No auth required (products, categories, auth endpoints) |
| **USER** | Valid JWT required (cart, orders, addresses, profile) |
| **ADMIN** | JWT with ADMIN role (user/product/order/category/cart management, stats) |

---

## 📡 API Endpoints

All endpoints are prefixed with `/api/v1`.

| Service | Endpoint | Method | Auth |
|---------|----------|--------|------|
| **Authentication** ||||
| Register | `/auth/register` | POST | Public |
| Login | `/auth/login` | POST | Public |
| Refresh Token | `/auth/refresh` | POST | Public |
| **User** ||||
| Get My Profile | `/users/me` | GET | USER |
| Update My Profile | `/users/me` | PATCH | USER |
| Change Password | `/users/me/change-password` | POST | USER |
| **Categories** ||||
| Get All Categories | `/categories` | GET | Public |
| Get Category by ID | `/categories/{id}` | GET | Public |
| **Products** ||||
| Get All Products | `/products` | GET | Public |
| Get Product by ID | `/products/{id}` | GET | Public |
| **Cart** ||||
| Get My Cart | `/cart` | GET | USER |
| Add Item to Cart | `/cart/items` | POST | USER |
| Update Item Quantity | `/cart/items/{productId}` | PATCH | USER |
| Remove Item | `/cart/items/{productId}` | DELETE | USER |
| Clear Cart | `/cart/clear` | DELETE | USER |
| **Addresses** ||||
| Create Address | `/addresses` | POST | USER |
| Get My Addresses | `/addresses` | GET | USER |
| Get Address by ID | `/addresses/{id}` | GET | USER |
| Update Address | `/addresses/{id}` | PUT | USER |
| Delete Address | `/addresses/{id}` | DELETE | USER |
| **Orders** ||||
| Checkout (Create Order) | `/orders` | POST | USER |
| Get My Orders | `/orders` | GET | USER |
| Get Order by ID | `/orders/{id}` | GET | USER |
| **Admin — Users** ||||
| Get All Users | `/admin/users` | GET | ADMIN |
| Get User by ID | `/admin/users/{id}` | GET | ADMIN |
| Update User Role | `/admin/users/{id}/role` | PATCH | ADMIN |
| Delete User | `/admin/users/{id}` | DELETE | ADMIN |
| **Admin — Categories** ||||
| Create Category | `/admin/categories` | POST | ADMIN |
| Update Category | `/admin/categories/{id}` | PUT | ADMIN |
| Delete Category | `/admin/categories/{id}` | DELETE | ADMIN |
| **Admin — Products** ||||
| Create Product | `/admin/products` | POST | ADMIN |
| Update Product | `/admin/products/{id}` | PUT | ADMIN |
| Delete Product | `/admin/products/{id}` | DELETE | ADMIN |
| **Admin — Orders** ||||
| Get All Orders | `/admin/orders` | GET | ADMIN |
| Get Order by ID | `/admin/orders/{id}` | GET | ADMIN |
| Update Order Status | `/admin/orders/{id}/status` | PATCH | ADMIN |
| **Admin — Carts** ||||
| Get User's Cart | `/admin/carts/users/{userId}` | GET | ADMIN |
| Clear User's Cart | `/admin/carts/users/{userId}/clear` | DELETE | ADMIN |
| Delete User's Cart | `/admin/carts/users/{userId}` | DELETE | ADMIN |
| **Admin — Stats** ||||
| Get Dashboard Stats | `/admin/stats` | GET | ADMIN |

> 📄 Full API reference with request/response examples: [`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md)

---

## 🧪 Testing

```bash
# Run all backend tests
mvn test

# Run a specific test class
mvn test -Dtest=CartServiceTest

# Run integration tests (requires Docker for Testcontainers)
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

*Last Updated: March 2026*
