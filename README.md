# E-Commerce Backend (Spring Boot)

This is a backend project I’m building to learn **Spring Boot**, **JWT Authentication**, and **Dockerized PostgreSQL**.

The project includes basic features like authentication, secure API structure, product & category management, and clean architecture.

---

## 🚀 Technologies
- Java 17
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL (Docker)
- Maven

---

## 📌 Status
The project now runs with:
- Docker-based PostgreSQL
- Environment variables via `.env`
- Completed JWT Auth (Register + Login + Refresh Token)

More features will be added as development continues.

---

## 🛠️ Running the project

### 1. Clone the repo
git clone https://github.com/yigittgulbeyaz/ecommerce-backend.git

### 2. Start PostgreSQL using Docker
docker compose up -d

### 3. Create a `.env` file in the project root
DB_PASSWORD=yourpassword  
JWT_SECRET=yourjwtsecret

### 4. Run the application
mvn spring-boot:run

---

## 👤 Author
Yiğit Gülbeyaz
