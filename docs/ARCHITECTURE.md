# Architecture Guide

This document explains the architecture, design patterns, and conventions used in this Spring Boot template.

## 📐 Architecture Overview

This template follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────┐
│      Controllers Layer              │  ← REST endpoints, request handling
├─────────────────────────────────────┤
│      Service Layer                  │  ← Business logic, orchestration
├─────────────────────────────────────┤
│      Repository Layer               │  ← Data access, JPA repositories
├─────────────────────────────────────┤
│      Domain/Entity Layer            │  ← Domain models, entities
└─────────────────────────────────────┘
```

## 🗂️ Package Structure

### Domain-Driven Design Organization

The project is organized by **feature/domain** rather than by layer:

```
src/main/java/com/yourcompany/yourproject/
│
├── security/                    # Security & Authentication Domain
│   ├── controllers/
│   │   └── AuthController.java
│   ├── dtos/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── RegisterRequest.java
│   │   └── RegisterResponse.java
│   ├── AuthService.java
│   ├── SecurityConfig.java
│   ├── UserDetailsImpl.java
│   └── UserDetailsService.java
│
├── user/                        # User Domain
│   ├── User.java               # Entity
│   ├── UserRepository.java     # Data access
│   └── UserService.java        # Business logic
│
├── roles/                       # Role Domain
│   ├── Role.java
│   └── RoleRepository.java
│
└── shared/                      # Cross-cutting concerns
    ├── ApiResponse.java
    ├── StandardError.java
    ├── BaseEntity.java
    ├── GlobalExceptionHandler.java
    └── errors/
        └── NotFoundException.java
```

### Benefits of This Structure

1. **High Cohesion**: Related code stays together
2. **Easy Navigation**: Find all user-related code in `/user`
3. **Scalability**: Add new domains without affecting existing ones
4. **Clear Boundaries**: Each domain can evolve independently

## 🏛️ Design Patterns

### 1. Repository Pattern

**Purpose**: Abstract data access logic

```java
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

**Benefits**:

- Decouples business logic from data access
- Easy to mock for testing
- Provides CRUD operations out-of-the-box

### 2. Service Layer Pattern

**Purpose**: Encapsulate business logic

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    // Business logic methods
    public User saveUser(String name, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.save(new User(name, email, encodedPassword));
    }
}
```

**Benefits**:

- Single responsibility
- Reusable business logic
- Transaction management

### 3. Factory Method Pattern

**Purpose**: Create standardized API responses

```java
// Usage
return ApiResponse.success(data, "User created");
return ApiResponse.success(data); // with default message
return ApiResponse.success("Operation completed"); // no data
```

**Benefits**:

- Consistent response structure
- Type-safe response creation
- Fluent API

### 4. Template Method Pattern

**Purpose**: Base entity with common fields

```java
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
```

**Usage**:

```java
@Entity
public class User extends BaseEntity {
    // Only domain-specific fields needed
    private String name;
    private String email;
}
```

**Benefits**:

- DRY principle
- Consistent entity structure
- Automatic auditing

## 🔐 Security Architecture

### JWT Authentication Flow

```
1. User Login
   ┌──────────┐     POST /login      ┌──────────────┐
   │  Client  │ ───────────────────> │AuthController│
   └──────────┘   email + password    └──────────────┘
                                             │
                                             ▼
                                      ┌──────────────┐
                                      │ AuthService  │
                                      └──────────────┘
                                             │
                                             ├─> Validate credentials
                                             ├─> Generate JWT token
                                             └─> Return token + expiry
                                             
2. Authenticated Requests
   ┌──────────┐   GET /api/resource   ┌──────────────┐
   │  Client  │ ───────────────────> │SecurityFilter│
   └──────────┘   Header:              └──────────────┘
                  Authorization:              │
                  Bearer <token>              ▼
                                      ┌──────────────┐
                                      │ JwtDecoder   │
                                      └──────────────┘
                                             │
                                             ├─> Verify signature
                                             ├─> Validate expiry
                                             ├─> Extract claims
                                             └─> Proceed to controller
```

### Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize, @PostAuthorize
public class SecurityConfig {
    
    // Public endpoints (no authentication)
    public static final String[] PUBLIC_ENDPOINTS = {
        "/api/v1/auth/**"
    };
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())  // Disabled for stateless API
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> 
                oauth2.jwt(Customizer.withDefaults()));
        
        return http.build();
    }
}
```

### JWT Token Structure

```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "name": "John Doe",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "iss": "your_issuer",
  "iat": 1702300800,
  "exp": 1702304400
}
```

## 🛡️ Error Handling Architecture

### Centralized Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardError> handleNotFound(
            NotFoundException e, 
            HttpServletRequest request) {
        
        StandardError error = new StandardError(
            Instant.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            e.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

### Error Handling Flow

```
Request → Controller
            │
            ├─> Exception Thrown
            │
            ▼
    GlobalExceptionHandler
            │
            ├─> Match Exception Type
            ├─> Create StandardError
            └─> Return ResponseEntity<StandardError>
```

### Adding New Exception Handlers

1. Create custom exception:

```java
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
```

2. Add handler to `GlobalExceptionHandler`:

```java
@ExceptionHandler(DuplicateResourceException.class)
public ResponseEntity<StandardError> handleDuplicate(
        DuplicateResourceException e,
        HttpServletRequest request) {
    
    StandardError error = new StandardError(
        Instant.now(),
        HttpStatus.CONFLICT.value(),
        "Duplicate Resource",
        e.getMessage(),
        request.getRequestURI()
    );
    
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
}
```

## 📦 Response Conventions

### Success Responses

Use `ApiResponse<T>` for all successful operations:

```java
// With data and custom message
return ResponseEntity.ok(
    ApiResponse.success(user, "User retrieved successfully")
);

// With data only (default message)
return ResponseEntity.ok(
    ApiResponse.success(users)
);

// Message only (no data)
return ResponseEntity.ok(
    ApiResponse.success("Operation completed")
);
```

**Response Structure**:

```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "John Doe",
    "email": "john@example.com"
  },
  "timestamp": "2025-12-11T10:30:00"
}
```

### Error Responses

Handled automatically by `GlobalExceptionHandler`:

```json
{
  "timestamp": "2025-12-11T10:30:00.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "User with ID 123 not found",
  "path": "/api/users/123"
}
```

## 🗄️ Database Architecture

### Entity Relationships

```
┌─────────────┐          ┌──────────────┐          ┌─────────────┐
│    User     │          │tb_user_role  │          │    Role     │
├─────────────┤          ├──────────────┤          ├─────────────┤
│ id (UUID)   │◄─────────┤ user_id (FK) │          │ id (LONG)   │
│ name        │          │ role_id (FK) │─────────►│ authority   │
│ email       │          └──────────────┘          └─────────────┘
│ password    │               Many-to-Many
│ createdAt   │
│ updatedAt   │
└─────────────┘
```

### Flyway Migrations

**Convention**: `V{version}__{description}.sql`

Example: `V1__CREATE_USER_AND_ROLES_TABLES.sql`

```sql
-- Version 1: Initial schema
CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Version 2: Add new field
-- File: V2__ADD_PHONE_TO_USERS.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

**Benefits**:

- Version-controlled schema
- Repeatable migrations
- Automatic execution on startup
- Rollback capabilities

## 🔄 Request Lifecycle

### Complete Flow Example

```
1. HTTP Request
   │
   ▼
2. Security Filter Chain
   ├─> Is endpoint public? → YES → Skip to (4)
   ├─> Is JWT valid?
   │   ├─> NO → Return 401 Unauthorized
   │   └─> YES → Extract user info
   │
   ▼
3. Authorization
   ├─> @PreAuthorize checks
   ├─> Role validation
   │
   ▼
4. Controller
   ├─> Request validation (@Valid)
   ├─> Call service method
   │
   ▼
5. Service Layer
   ├─> Business logic
   ├─> Call repository
   │
   ▼
6. Repository Layer
   ├─> Database query
   ├─> Return entity
   │
   ▼
7. Response
   ├─> Wrap in ApiResponse
   ├─> Return ResponseEntity
   │
   ▼
8. Client receives JSON response
```

## 🎯 Best Practices Implemented

### 1. **Separation of Concerns**

- Controllers handle HTTP
- Services handle business logic
- Repositories handle data access

### 2. **Single Responsibility Principle**

- Each class has one reason to change
- DTOs separate from entities

### 3. **Dependency Injection**

- Constructor injection (recommended)
- Promotes testability

### 4. **Immutability**

- DTOs use records (Java 16+)
- Reduces bugs

### 5. **Fail Fast**

- Validation at entry points
- Clear error messages

### 6. **Don't Repeat Yourself (DRY)**

- BaseEntity for common fields
- ApiResponse factory methods
- Centralized error handling

## 📊 Adding New Features

### Step-by-Step Guide

#### 1. Create Domain Package

```bash
mkdir -p src/main/java/com/yourcompany/yourproject/product
```

#### 2. Define Entity

```java
@Entity
@Table(name = "products")
@Getter @Setter
public class Product extends BaseEntity {
    private String name;
    private BigDecimal price;
    private Integer stock;
}
```

#### 3. Create Repository

```java
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceLessThan(BigDecimal price);
}
```

#### 4. Implement Service

```java
@Service
public class ProductService {
    private final ProductRepository repository;
    
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    public List<Product> findAll() {
        return repository.findAll();
    }
    
    public Product findById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));
    }
}
```

#### 5. Create Controller

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService service;
    
    public ProductController(ProductService service) {
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> findAll() {
        return ResponseEntity.ok(
            ApiResponse.success(service.findAll())
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(service.findById(id))
        );
    }
}
```

#### 6. Create Migration

```sql
-- V2__CREATE_PRODUCTS_TABLE.sql
CREATE TABLE products (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL
);
```

## 🧪 Testing Architecture

### Test Structure

```
src/test/java/
├── integration/           # Integration tests
├── unit/                 # Unit tests
│   ├── service/         # Service layer tests
│   └── controller/      # Controller tests (with @WebMvcTest)
└── repository/          # Repository tests (with @DataJpaTest)
```

### Example Unit Test

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository repository;
    
    @Mock
    private BCryptPasswordEncoder encoder;
    
    @InjectMocks
    private UserService service;
    
    @Test
    void shouldSaveUser() {
        // Given
        when(encoder.encode("password")).thenReturn("encoded");
        when(repository.save(any())).thenReturn(new User());
        
        // When
        User result = service.saveUser("John", "john@test.com", "password");
        
        // Then
        assertNotNull(result);
        verify(repository).save(any(User.class));
    }
}
```

## 🔑 Key Takeaways

1. **Layered Architecture**: Clear separation between layers
2. **Domain-Driven Design**: Organize by feature, not by layer type
3. **Consistent Responses**: Use `ApiResponse` and `StandardError`
4. **Base Entity Pattern**: Inherit common fields
5. **Centralized Error Handling**: One place for all exceptions
6. **JWT Security**: Stateless, scalable authentication
7. **Flyway Migrations**: Version-controlled database schema
8. **Constructor Injection**: Better testability

---

This architecture provides a solid foundation for building secure, scalable REST APIs with Spring Boot.
