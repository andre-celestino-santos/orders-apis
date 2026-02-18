[![Build and Tests](https://github.com/andre-celestino-santos/orders-apis/actions/workflows/ci.yml/badge.svg)](https://github.com/andre-celestino-santos/orders-apis/actions/workflows/ci.yml)

# 🗳 Orders API

## 📌 Overview

REST API for managing **products**, **orders**, and **stock**.

---

## 📦 Features

### Products
- Create product
- Update product
- Delete product
- List products by category with pagination

### Orders
- Create order with items
- Add item to order
- Remove item from order
- Cancel order
- Stock control
- Get order by id

---

## 🧱 Architecture

### Layers

Controller  →  Mapper  →  Service  →  Repository  →  Database

### Tools and Libraries

- Java 17
- Spring Boot 4
- Spring Data JPA
- Hibernate
- H2 / MySQL
- Maven
- Lombok
- OpenAPI (Swagger)
- Unit and integration tests with JUnit and Mockito

---

## 📑 API Documentation

The API endpoints documentation is available via Swagger (OpenAPI), allowing interactive visualization, testing, and API consumption.

After starting the application, the documentation can be accessed at:

http://localhost:8080/swagger-ui.html

---

## 🚀 How to run the project

### Prerequisites
- **Apache Maven**: 3.8+
- **Java JDK**: 17+
- **Docker**: 27.5+ (optional)
- **Operating System:** any OS compatible with the versions above of Maven, JDK, and Docker

---

### Run locally

#### 1. Configure the database

**NOTE**: Docker is used here as an example to run a MySQL container, but you can create the database using any approach you prefer.

```bash
docker run --name orders-apis-mysql-db \
  -e MYSQL_USER=orders-apis-user \
  -e MYSQL_PASSWORD=orders-apis-pass \
  -e MYSQL_ROOT_PASSWORD=orders-apis-root-pass \
  -e MYSQL_DATABASE=orders-apis-db \
  -p 3306:3306 \
  -d mysql:8
```

2. Configure environment variables

```bash
export SPRING_DATASOURCE_PASSWORD="orders-apis-pass" \
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/orders-apis-db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
export SPRING_DATASOURCE_USERNAME="orders-apis-user"
```

3. Run the application

```bash
mvn spring-boot:run
```