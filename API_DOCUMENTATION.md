# E-Commerce Platform — API Documentation v2.0

**Developed by Yigit Gulbeyaz — 2026**

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Getting Started](#2-getting-started)
3. [Swagger UI / OpenAPI](#3-swagger-ui--openapi)
4. [Project Architecture](#4-project-architecture)
5. [Authentication & Security](#5-authentication--security)
6. [Endpoints Overview](#6-endpoints-overview)
7. [Standardized API Response Format](#7-standardized-api-response-format)
8. [API Endpoints](#8-api-endpoints)
   - [8.1 Authentication](#81-authentication)
   - [8.2 User Management](#82-user-management)
   - [8.3 Categories](#83-categories)
   - [8.4 Products](#84-products)
   - [8.5 Cart](#85-cart)
   - [8.6 Address](#86-address)
   - [8.7 Orders](#87-orders)
   - [8.8 Admin — Users](#88-admin--users)
   - [8.9 Admin — Categories](#89-admin--categories)
   - [8.10 Admin — Products](#810-admin--products)
   - [8.11 Admin — Orders](#811-admin--orders)
   - [8.12 Admin — Carts](#812-admin--carts)
   - [8.13 Admin — Dashboard Stats](#813-admin--dashboard-stats)
9. [Error Codes Reference](#9-error-codes-reference)

---

## 1. Introduction

### 1.1 Overview

This project is a fully functional **E-Commerce Platform API** built with **Spring Boot 3.4**. It provides a complete set of RESTful endpoints for:

- User authentication and authorization (JWT)
- Product and category management
- Shopping cart functionality
- Order processing with payment integration
- Address management
- Admin dashboard with revenue analytics
- Admin cart moderation
- Role-based access control (USER, ADMIN)

### 1.2 Purpose

This documentation provides comprehensive guidance for:

- **Frontend developers** integrating with the API
- **Backend developers** understanding the architecture
- **QA engineers** testing the endpoints

### 1.3 Key Features

- JWT-based authentication (Access + Refresh tokens)
- Role-based access control (USER, ADMIN)
- RESTful API design with versioned endpoints (`/api/v1`)
- **Swagger UI / OpenAPI 3.0** for interactive API documentation
- Standardized API response format (`ApiResponse<T>`)
- Comprehensive validation with meaningful error messages
- Pagination & sorting support for list endpoints
- Deployed on **Railway** (Backend + PostgreSQL) and **Vercel** (Frontend)

---

## 2. Getting Started

### 2.1 Prerequisites

- **Java 17** or higher
- **Maven**
- **Docker** and **Docker Compose** (for local PostgreSQL)
- **Postman** or similar REST client (optional)

### 2.2 Base URL

| Environment | Base URL |
|-------------|----------|
| **Production** | `https://ecommerce-backend-production-b148.up.railway.app/api/v1` |
| **Local** | `http://localhost:8080/api/v1` |

### 2.3 Running Locally

```bash
# 1. Clone the repository
git clone https://github.com/yigittgulbeyaz/ecommerce-backend.git
cd ecommerce-backend

# 2. Start PostgreSQL using Docker
docker compose up -d

# 3. Create a .env file from the template
cp .env.example .env
# Edit .env and set DB_PASSWORD and JWT_SECRET

# 4. Run the application
mvn spring-boot:run
```

---

## 3. Swagger UI / OpenAPI

This project includes **SpringDoc OpenAPI 2.7.0** integration, providing an interactive API documentation interface.

### 3.1 Accessing Swagger UI

| Resource | Production | Local |
|----------|-----------|-------|
| **Swagger UI** | [Railway /swagger-ui.html](https://ecommerce-backend-production-b148.up.railway.app/swagger-ui.html) | [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| **OpenAPI JSON** | [Railway /v3/api-docs](https://ecommerce-backend-production-b148.up.railway.app/v3/api-docs) | [localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |

### 3.2 Authentication in Swagger UI

1. Execute the `/auth/login` endpoint with valid credentials
2. Copy the `accessToken` from the response
3. Click the **"Authorize"** button at the top right of Swagger UI
4. Enter the token in the format: `Bearer eyJhbGciOiJIUzI1NiIs...`
5. Click **"Authorize"** to apply the token to all subsequent requests

### 3.3 Swagger UI Features

- **Try it out** — Execute API requests directly from the browser
- **Request Examples** — Pre-filled request body examples
- **Response Schemas** — Detailed response structure documentation
- **JWT Authentication** — Built-in Bearer token support
- **Download Spec** — Export OpenAPI specification as JSON/YAML

---

## 4. Project Architecture

### 4.1 Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│           Client (Web — React SPA on Vercel)                 │
└───────────────────────────┬──────────────────────────────────┘
                            │ HTTPS
                            ▼
┌──────────────────────────────────────────────────────────────┐
│              Spring Boot Application (Railway)               │
│                                                              │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────────┐  │
│  │  Security    │  │ Controllers  │  │ GlobalException    │  │
│  │  (JWT Auth)  │  │  (REST API)  │  │    Handler         │  │
│  └─────────────┘  └──────┬───────┘  └────────────────────┘  │
│                          │                                   │
│                          ▼                                   │
│  ┌──────────────────────────────────────────────────────┐    │
│  │           Service Layer (Business Logic)             │    │
│  └──────────────────────────────────────────────────────┘    │
│                          │                                   │
│                          ▼                                   │
│  ┌──────────────────────────────────────────────────────┐    │
│  │        Repository Layer (Spring Data JPA)            │    │
│  └──────────────────────────────────────────────────────┘    │
└───────────────────────────┬──────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────┐
│               PostgreSQL 17 (Railway Managed)                │
└──────────────────────────────────────────────────────────────┘
```

### 4.2 Technology Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.4.12 |
| Language | Java 17 |
| Database | PostgreSQL 17 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| API Docs | SpringDoc OpenAPI 2.7.0 |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Backend Hosting | Railway |
| Frontend Hosting | Vercel |
| Testing | JUnit 5, Mockito, Testcontainers 1.20.4 |

### 4.3 Layered Architecture

| Layer | Responsibility |
|-------|---------------|
| **Controller** | HTTP request handling, routing, input validation |
| **Service** | Business logic, transaction management |
| **Repository** | Database access via Spring Data JPA |
| **Security** | Authentication, authorization, JWT processing |
| **Exception** | Centralized error handling (`GlobalExceptionHandler`) |
| **DTO** | Data transfer objects — Java Records for request/response |
| **Mapper** | Entity ↔ DTO conversion |

---

## 5. Authentication & Security

### 5.1 JWT Authentication Flow

```
1. User Registration
   POST /api/v1/auth/register
   → Returns: User data + Access token + Refresh token

2. User Login
   POST /api/v1/auth/login
   → Returns: Access token + Refresh token

3. API Access
   Authorization: Bearer <access_token>
   → Validates token, extracts user principal

4. Token Refresh
   POST /api/v1/auth/refresh?refreshToken=<token>
   → Returns: New access token

5. Token Expiry Defaults
   Access token:  15 minutes (900 000 ms)
   Refresh token: 7 days (604 800 000 ms)
```

### 5.2 Authorization Levels

| Level | Description |
|-------|-------------|
| **Public** | No authentication required |
| **Authenticated (USER)** | Requires valid JWT token |
| **ADMIN** | Requires JWT with ADMIN role |

### 5.3 Security Configuration

```
Public Endpoints (No Auth Required):
  POST  /api/v1/auth/register
  POST  /api/v1/auth/login
  POST  /api/v1/auth/refresh
  GET   /api/v1/categories
  GET   /api/v1/categories/{id}
  GET   /api/v1/products
  GET   /api/v1/products/{id}
  GET   /swagger-ui.html, /v3/api-docs

Authenticated Endpoints (USER or ADMIN):
  /api/v1/cart/**
  /api/v1/addresses/**
  /api/v1/users/**
  /api/v1/orders/**

Admin Only Endpoints:
  /api/v1/admin/**
```

---

## 6. Endpoints Overview

### Quick Reference Table

| Function | Method | Endpoint | Auth |
|----------|--------|----------|------|
| **Authentication** ||||
| Register | POST | `/auth/register` | Public |
| Login | POST | `/auth/login` | Public |
| Refresh Token | POST | `/auth/refresh` | Public |
| **User** ||||
| Get My Profile | GET | `/users/me` | USER |
| Update My Profile | PATCH | `/users/me` | USER |
| Change Password | POST | `/users/me/change-password` | USER |
| **Categories** ||||
| Get All Categories | GET | `/categories` | Public |
| Get Category by ID | GET | `/categories/{id}` | Public |
| **Products** ||||
| Get All Products | GET | `/products` | Public |
| Get Product by ID | GET | `/products/{id}` | Public |
| **Cart** ||||
| Get My Cart | GET | `/cart` | USER |
| Add Item to Cart | POST | `/cart/items` | USER |
| Update Item Quantity | PATCH | `/cart/items/{productId}` | USER |
| Remove Item | DELETE | `/cart/items/{productId}` | USER |
| Clear Cart | DELETE | `/cart/clear` | USER |
| **Addresses** ||||
| Create Address | POST | `/addresses` | USER |
| Get My Addresses | GET | `/addresses` | USER |
| Get Address by ID | GET | `/addresses/{id}` | USER |
| Update Address | PUT | `/addresses/{id}` | USER |
| Delete Address | DELETE | `/addresses/{id}` | USER |
| **Orders** ||||
| Checkout | POST | `/orders` | USER |
| Get My Orders | GET | `/orders` | USER |
| Get Order by ID | GET | `/orders/{id}` | USER |
| **Admin — Users** ||||
| Get All Users | GET | `/admin/users` | ADMIN |
| Get User by ID | GET | `/admin/users/{id}` | ADMIN |
| Update User Role | PATCH | `/admin/users/{id}/role` | ADMIN |
| Delete User | DELETE | `/admin/users/{id}` | ADMIN |
| **Admin — Categories** ||||
| Create Category | POST | `/admin/categories` | ADMIN |
| Update Category | PUT | `/admin/categories/{id}` | ADMIN |
| Delete Category | DELETE | `/admin/categories/{id}` | ADMIN |
| **Admin — Products** ||||
| Create Product | POST | `/admin/products` | ADMIN |
| Update Product | PUT | `/admin/products/{id}` | ADMIN |
| Delete Product | DELETE | `/admin/products/{id}` | ADMIN |
| **Admin — Orders** ||||
| Get All Orders | GET | `/admin/orders` | ADMIN |
| Get Order by ID | GET | `/admin/orders/{id}` | ADMIN |
| Update Order Status | PATCH | `/admin/orders/{id}/status` | ADMIN |
| **Admin — Carts** ||||
| Get User's Cart | GET | `/admin/carts/users/{userId}` | ADMIN |
| Clear User's Cart | DELETE | `/admin/carts/users/{userId}/clear` | ADMIN |
| Delete User's Cart | DELETE | `/admin/carts/users/{userId}` | ADMIN |
| **Admin — Stats** ||||
| Get Dashboard Stats | GET | `/admin/stats` | ADMIN |

---

## 7. Standardized API Response Format

### 7.1 Success Response Structure

```json
{
  "success": true,
  "status": 200,
  "message": "Operation completed successfully",
  "data": { ... },
  "errors": null,
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

| Field | Type | Description |
|-------|------|-------------|
| success | boolean | Indicates operation success |
| status | int | HTTP status code |
| message | string | Human-readable message |
| data | T | Response payload (generic) |
| errors | Map | Validation errors (`null` if none) |
| timestamp | string | ISO 8601 timestamp |

### 7.2 Error Response Structure

```json
{
  "success": false,
  "status": 422,
  "message": "Validation failed",
  "data": null,
  "errors": {
    "email": "Email format is invalid",
    "password": "Password must be at least 6 characters"
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

### 7.3 HTTP Status Codes

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, PUT, PATCH, DELETE |
| 201 | Created | Successful POST (resource created) |
| 204 | No Content | Successful operation with no response body |
| 400 | Bad Request | Invalid request syntax |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Business rule violation (e.g., empty cart) |
| 422 | Unprocessable Entity | Validation errors |
| 500 | Internal Server Error | Unexpected server error |

---

## 8. API Endpoints

### 8.1 Authentication

#### 8.1.1 Register User

`POST /api/v1/auth/register` — **Public**

Creates a new user account and returns JWT tokens.

**Request Body:**

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| name | string | Yes | 2–50 characters |
| email | string | Yes | Valid email format |
| password | string | Yes | Min 6 characters |

**Example Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Example Response (201 Created):**
```json
{
  "success": true,
  "status": 201,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

---

#### 8.1.2 Login User

`POST /api/v1/auth/login` — **Public**

Authenticates a user and returns JWT tokens.

**Request Body:**

| Field | Type | Required |
|-------|------|----------|
| email | string | Yes |
| password | string | Yes |

**Example Request:**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

**Example Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

---

#### 8.1.3 Refresh Token

`POST /api/v1/auth/refresh` — **Public**

Refreshes an expired access token using a valid refresh token.

**Query Parameters:**

| Parameter | Type | Required |
|-----------|------|----------|
| refreshToken | string | Yes |

**Example Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Token refreshed",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs..."
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

---

### 8.2 User Management

#### 8.2.1 Get My Profile

`GET /api/v1/users/me` — **Authenticated**

**Headers:** `Authorization: Bearer {accessToken}`

**Example Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "User profile fetched",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER"
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

---

#### 8.2.2 Update My Profile

`PATCH /api/v1/users/me` — **Authenticated**

**Request Body:**

| Field | Type | Required |
|-------|------|----------|
| name | string | No |

**Example Request:**
```json
{
  "name": "John Updated"
}
```

---

#### 8.2.3 Change Password

`POST /api/v1/users/me/change-password` — **Authenticated**

**Request Body:**

| Field | Type | Required |
|-------|------|----------|
| currentPassword | string | Yes |
| newPassword | string | Yes |

**Example Request:**
```json
{
  "currentPassword": "secret123",
  "newPassword": "newSecret456"
}
```

---

### 8.3 Categories

#### 8.3.1 Get All Categories

`GET /api/v1/categories` — **Public**

**Example Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Categories fetched",
  "data": [
    { "id": 1, "name": "Electronics" },
    { "id": 2, "name": "Clothing" },
    { "id": 3, "name": "Books" }
  ],
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

---

#### 8.3.2 Get Category by ID

`GET /api/v1/categories/{id}` — **Public**

| Path Parameter | Type | Required |
|----------------|------|----------|
| id | Long | Yes |

---

### 8.4 Products

#### 8.4.1 Get All Products

`GET /api/v1/products` — **Public**

Returns a paginated list of products with optional filtering.

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| categoryId | Long | No | Filter by category |
| search | string | No | Search by product name |
| page | int | No | Page number (0-indexed, default: 0) |
| size | int | No | Page size (default: 20) |
| sort | string | No | Sort field (e.g., `price,asc`) |

**Example Request:**
```
GET /api/v1/products?categoryId=1&search=phone&page=0&size=10&sort=price,asc
```

**Example Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Products listed",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "iPhone 15 Pro",
        "description": "Latest Apple smartphone",
        "price": 999.99,
        "categoryId": 1,
        "categoryName": "Electronics"
      }
    ],
    "page": {
      "size": 10,
      "number": 0,
      "totalElements": 1,
      "totalPages": 1
    }
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

---

#### 8.4.2 Get Product by ID

`GET /api/v1/products/{id}` — **Public**

| Path Parameter | Type | Required |
|----------------|------|----------|
| id | Long | Yes |

---

### 8.5 Cart

#### 8.5.1 Get My Cart

`GET /api/v1/cart` — **Authenticated**

**Example Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Cart fetched",
  "data": {
    "items": [
      {
        "productId": 1,
        "productName": "iPhone 15 Pro",
        "quantity": 2,
        "unitPrice": 999.99,
        "totalPrice": 1999.98
      }
    ],
    "totalPrice": 1999.98,
    "itemCount": 2
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

---

#### 8.5.2 Add Item to Cart

`POST /api/v1/cart/items` — **Authenticated**

Adds a product to the cart or increases quantity if already exists.

**Request Body:**

| Field | Type | Required |
|-------|------|----------|
| productId | Long | Yes |
| quantity | int | Yes |

**Example Request:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

---

#### 8.5.3 Update Item Quantity

`PATCH /api/v1/cart/items/{productId}` — **Authenticated**

| Path Parameter | Type | Required |
|----------------|------|----------|
| productId | Long | Yes |

**Request Body:**

| Field | Type | Required |
|-------|------|----------|
| quantity | int | Yes |

---

#### 8.5.4 Remove Item from Cart

`DELETE /api/v1/cart/items/{productId}` — **Authenticated**

Removes a specific item from the cart.

---

#### 8.5.5 Clear Cart

`DELETE /api/v1/cart/clear` — **Authenticated**

Removes all items from the cart.

---

### 8.6 Address

#### 8.6.1 Create Address

`POST /api/v1/addresses` — **Authenticated**

**Request Body:**

| Field | Type | Required |
|-------|------|----------|
| title | string | Yes |
| addressLine | string | Yes |
| city | string | Yes |
| district | string | Yes |
| zipCode | string | Yes |
| country | string | Yes |

**Example Request:**
```json
{
  "title": "Home",
  "addressLine": "123 Main Street",
  "city": "Istanbul",
  "district": "Kadikoy",
  "zipCode": "34000",
  "country": "TR"
}
```

---

#### 8.6.2 Get My Addresses

`GET /api/v1/addresses` — **Authenticated**

Returns all addresses for the current user.

---

#### 8.6.3 Get Address by ID

`GET /api/v1/addresses/{id}` — **Authenticated**

Returns a specific address (only if owned by the current user).

---

#### 8.6.4 Update Address

`PUT /api/v1/addresses/{id}` — **Authenticated**

Updates an existing address.

---

#### 8.6.5 Delete Address

`DELETE /api/v1/addresses/{id}` — **Authenticated**

Deletes an address.

---

### 8.7 Orders

#### 8.7.1 Checkout (Create Order)

`POST /api/v1/orders` — **Authenticated**

Creates a new order from the current cart with payment processing.

**Request Body:**

| Field | Type | Required |
|-------|------|----------|
| addressId | Long | Yes |
| paymentRequest | object | Yes |

**Payment Request Fields:**

| Field | Type | Required |
|-------|------|----------|
| amount | BigDecimal | Yes |
| cardNumber | string | Yes |
| cardHolder | string | Yes |
| cvv | string | Yes |
| expireDate | string | Yes |

**Example Request:**
```json
{
  "addressId": 1,
  "paymentRequest": {
    "amount": 1999.98,
    "cardNumber": "4111111111111111",
    "cardHolder": "John Doe",
    "cvv": "123",
    "expireDate": "12/30"
  }
}
```

**Example Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Order created",
  "data": {
    "id": 1,
    "totalPrice": 1999.98,
    "status": "PAID",
    "createdAt": "2026-03-02T14:00:00",
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "iPhone 15 Pro",
        "price": 999.99,
        "quantity": 2,
        "lineTotal": 1999.98
      }
    ]
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

**Order Status Values:**

| Status | Description |
|--------|-------------|
| PENDING | Order created, awaiting payment |
| PAID | Payment successful |
| SHIPPED | Order shipped |
| DELIVERED | Order delivered |
| CANCELLED | Order cancelled |

---

#### 8.7.2 Get My Orders

`GET /api/v1/orders` — **Authenticated**

Returns all orders for the current user.

---

#### 8.7.3 Get Order by ID

`GET /api/v1/orders/{id}` — **Authenticated**

Returns a specific order (only if owned by the current user).

---

### 8.8 Admin — Users

> **Note:** All admin endpoints require the **ADMIN** role.

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/users` | Get all users |
| GET | `/admin/users/{id}` | Get user by ID |
| PATCH | `/admin/users/{id}/role` | Update user role |
| DELETE | `/admin/users/{id}` | Delete user |

**Update Role Request:**
```json
{
  "role": "ADMIN"
}
```

---

### 8.9 Admin — Categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/admin/categories` | Create category |
| PUT | `/admin/categories/{id}` | Update category |
| DELETE | `/admin/categories/{id}` | Delete category |

**Create / Update Request:**
```json
{
  "name": "New Category"
}
```

---

### 8.10 Admin — Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/admin/products` | Create product |
| PUT | `/admin/products/{id}` | Update product |
| DELETE | `/admin/products/{id}` | Delete product |

**Create / Update Request:**
```json
{
  "name": "New Product",
  "description": "Product description",
  "price": 99.99,
  "categoryId": 1
}
```

---

### 8.11 Admin — Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/orders` | Get all orders |
| GET | `/admin/orders/{id}` | Get order by ID |
| PATCH | `/admin/orders/{id}/status` | Update order status |

**Update Status Request:**
```json
{
  "status": "SHIPPED"
}
```

---

### 8.12 Admin — Carts

Allows admins to view, clear, or delete a user's shopping cart.

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/carts/users/{userId}` | Get a user's cart |
| DELETE | `/admin/carts/users/{userId}/clear` | Clear a user's cart (remove all items) |
| DELETE | `/admin/carts/users/{userId}` | Delete a user's cart entirely |

**Example — Get User's Cart (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Cart fetched",
  "data": {
    "items": [
      {
        "productId": 3,
        "productName": "Wireless Earbuds",
        "quantity": 1,
        "unitPrice": 49.99,
        "totalPrice": 49.99
      }
    ],
    "totalPrice": 49.99,
    "itemCount": 1
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

---

### 8.13 Admin — Dashboard Stats

Returns aggregate dashboard statistics for the admin panel.

`GET /api/v1/admin/stats` — **ADMIN**

**Example Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Dashboard stats fetched",
  "data": {
    "totalProducts": 42,
    "totalOrders": 156,
    "totalCategories": 8,
    "totalUsers": 234,
    "revenue": 45890.50
  },
  "timestamp": "2026-03-02T14:00:00.000Z"
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| totalProducts | long | Total number of products in catalog |
| totalOrders | long | Total number of orders placed |
| totalCategories | long | Total number of product categories |
| totalUsers | long | Total registered users |
| revenue | BigDecimal | Total revenue from all paid orders |

---

## 9. Error Codes Reference

| HTTP Code | Error Type | Example Message |
|-----------|-----------|-----------------|
| 400 | BadRequestException | Invalid request parameters |
| 401 | UnauthorizedException | Authentication required |
| 403 | ForbiddenException | Access denied |
| 404 | NotFoundException | Product not found |
| 409 | ConflictException | Cart is empty |
| 422 | ValidationException | Validation failed |
| 500 | PaymentFailedException | Payment processing failed |

---

## Author

**Yigit Gulbeyaz**
GitHub: [@yigittgulbeyaz](https://github.com/yigittgulbeyaz)

---

*Last Updated: March 2026*
