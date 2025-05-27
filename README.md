# Freelance Platform Microservices

A microservices-based freelance platform built with Spring Boot, featuring project management, bidding system, user management, and security services.

## 🏗 Architecture

The platform consists of the following microservices:

- **User Service** (Port: 8080)
  - User management
  - Profile handling
  - User authentication data

- **Project Service** (Port: 8081)
  - Project management
  - Project status tracking
  - Project categories and tags

- **Bid Service** (Port: 8083)
  - Bid management
  - Bid status tracking
  - Bid notifications

- **Security Service** (Port: 8084)
  - JWT-based authentication
  - Authorization
  - Token management

- **Common Module**
  - Shared DTOs
  - Common utilities
  - Custom exceptions and a exception handler

## 🛠 Technology Stack

### Core Technologies
- Java 23
- Spring Boot 3.4.3
- Spring Security
- Spring Data JPA
- Spring Kafka
- PostgreSQL
- Docker & Docker Compose

### Database
- PostgreSQL (Multiple instances)

### Security
- JWT (JSON Web Tokens)
- Spring Security
- BCrypt Password Encoding

### Monitoring & Management
- Spring Boot Actuator
- Health Checks
- Metrics

### Build Tools
- Maven
- Docker
- Docker Compose

### Testing
- JUnit 5
- Mockito
- Spring Boot Test

### Additional Features
- Liquibase and Flyway for database migrations
- MapStruct for object mapping
- Lombok for reducing boilerplate
- CORS configuration
- Environment-specific configurations (local/docker)
- Outbox pattern for distributed transactions

## 🚀 Getting Started

### Prerequisites
- Java 23
- Docker & Docker Compose
- Maven
- PostgreSQL (for local development)
