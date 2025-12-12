# Spring Boot Authentication Template

A production-ready Spring Boot 4.0 template with JWT authentication, role-based authorization, and best practices for building secure REST APIs.

## 🚀 Features

### Security & Authentication

- **JWT Authentication** with RSA public/private key pairs
- **Role-Based Access Control (RBAC)** with Spring Security
- **Password Encryption** using BCrypt
- **Stateless Sessions** for scalability

### Architecture & Design

- **Centralized Error Handling** with `@RestControllerAdvice`
- **Standardized API Responses** using `ApiResponse<T>` wrapper
- **Base Entity Pattern** with automatic auditing (createdAt, updatedAt)
- **Repository Pattern** with Spring Data JPA

### Database & Persistence

- **PostgreSQL** as primary database
- **Flyway Migrations** for version-controlled schema changes
- **H2 Console** for development/testing
- **UUID-based Primary Keys** for entities
- **Automatic Auditing** with JPA EntityListeners

### Developer Experience

- **Lombok** integration for cleaner code
- **Docker Compose** for easy database setup
- **Spring Boot DevTools** ready
- **Actuator** endpoints for monitoring

## 📋 Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose (for database)
- PostgreSQL (or use provided Docker setup)

## ⚡ Quick Start

### 1. Clone and Navigate

```bash
git clone <your-repo-url>
cd real_state_ai_backend
```

### 2. Generate RSA Keys for JWT

```bash
# Generate private key
openssl genrsa -out src/main/resources/app.key 2048

# Generate public key from private key
openssl rsa -in src/main/resources/app.key -pubout -out src/main/resources/app.pub
```

### 3. Start Database

```bash
docker-compose up -d
```

### 4. Run Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 Documentation

- **[Architecture Guide](docs/ARCHITECTURE.md)** - Project structure and conventions
- **[API Documentation](docs/API.md)** - Available endpoints and usage
- **[Setup Guide](docs/SETUP.md)** - Detailed setup and configuration
- **[Development Guide](docs/DEVELOPMENT.md)** - How to extend the template

## 🔑 Default Endpoints

### Public Endpoints (No Authentication Required)

- `POST /login` - User authentication
- `POST /register` - User registration

All other endpoints are protected by default (change in `SecurityConfig.PUBLIC_ENDPOINTS`).

## 🏗️ Project Structure

```
src/main/java/com/yuricosta/real_state_ai_backend/
├── security/              # Authentication & authorization
│   ├── controllers/       # Auth endpoints (login, register)
│   ├── dtos/             # Request/response DTOs
│   ├── AuthService.java  # JWT generation logic
│   └── SecurityConfig.java # Security configuration
├── user/                  # User domain
│   ├── User.java         # User entity
│   ├── UserRepository.java
│   └── UserService.java
├── roles/                 # Role management
│   ├── Role.java
│   └── RoleRepository.java
└── shared/               # Shared utilities
    ├── ApiResponse.java  # Standard response wrapper
    ├── StandardError.java # Error response format
    ├── BaseEntity.java   # Base class for entities
    ├── GlobalExceptionHandler.java # Centralized error handling
    └── errors/           # Custom exceptions
```

## 🎯 Core Conventions

### 1. API Response Format

All successful responses use `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2025-12-11T10:30:00"
}
```

### 2. Error Response Format

All errors use `StandardError`:

```json
{
  "timestamp": "2025-12-11T10:30:00.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "User not found",
  "path": "/api/users/123"
}
```

### 3. Entity Base Class

All entities extend `BaseEntity` for automatic:

- UUID-based primary keys
- `createdAt` timestamp
- `updatedAt` timestamp

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 4.0.0 | Framework |
| Java | 21 | Language |
| PostgreSQL | Latest | Database |
| Flyway | Latest | Migrations |
| Spring Security | 6.x | Security |
| OAuth2 Resource Server | 6.x | JWT handling |
| Lombok | Latest | Code generation |
| BCrypt | - | Password hashing |

## 🔒 Security Features

- **JWT Tokens**: 1-hour expiration (configurable)
- **Password Hashing**: BCrypt with default strength
- **Stateless Authentication**: No server-side sessions
- **RSA Encryption**: Public/private key pair for JWT signing
- **CSRF Protection**: Disabled for stateless API
- **CORS**: Configure in `SecurityConfig` as needed

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw verify
```

## 📦 Building for Production

```bash
# Create executable JAR
./mvnw clean package

# Run the JAR
java -jar target/real_state_ai_backend-0.0.1-SNAPSHOT.jar
```

## 🔧 Configuration

Key configuration in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5434/real_state_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT Keys
jwt.pub.key=classpath:app.pub
jwt.private.key=classpath:app.key

# Flyway
spring.flyway.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=validate
```

## 🚀 Using as Template

### 1. Update Package Names

Replace `com.yuricosta.real_state_ai_backend` with your package:

```bash
# Linux/Mac
find . -type f -name "*.java" -exec sed -i '' 's/com.yuricosta.real_state_ai_backend/com.yourcompany.yourproject/g' {} +

# Or manually via IDE refactoring
```

### 2. Update Project Metadata

Edit `pom.xml`:

- `<groupId>` - Your organization
- `<artifactId>` - Your project name
- `<name>` and `<description>`

### 3. Update Application Name

In `application.properties`:

```properties
spring.application.name=your-application-name
```

### 4. Configure Database

Update database settings in:

- `application.properties`
- `compose.yaml`

## 📖 Learning Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [Flyway Documentation](https://flywaydb.org/documentation/)

## 🤝 Contributing

This is a template repository. When using for your projects:

1. Fork or clone the repository
2. Customize for your needs
3. Remove this section and update README

## 📝 License

[Add your license here]

## 👤 Author

Yuri Costa

---

**Ready to build your next Spring Boot application!** 🎉

For detailed guides, see the `/docs` folder.
