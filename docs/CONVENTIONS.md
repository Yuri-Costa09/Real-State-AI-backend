# Coding Conventions & Standards

This document outlines the coding conventions, naming standards, and best practices used throughout this Spring Boot template.

## 📦 Package Structure

### Naming Convention

- **Base package**: `com.{company}.{project}`
- **Domain packages**: Feature-based organization
- **Shared utilities**: In `shared` package

```
com.yourcompany.yourproject/
├── security/           # Authentication & authorization
├── user/              # User domain
├── roles/             # Role domain
├── product/           # Product domain (example)
└── shared/            # Cross-cutting concerns
```

### Package Organization Rules

1. **One domain per package**: All related classes in same package
2. **No layer-based packages**: Avoid `/controllers`, `/services`, `/repositories` at root
3. **Exceptions**: Domain-specific in domain package, shared in `shared/errors`

## 🏗️ Class Naming

### Entities

- **Format**: Singular noun, PascalCase
- **Examples**: `User`, `Product`, `Order`, `Category`

```java
@Entity
@Table(name = "products")  // Table name plural
public class Product extends BaseEntity {
    // ...
}
```

### Repositories

- **Format**: `{Entity}Repository`
- **Example**: `UserRepository`, `ProductRepository`

```java
public interface UserRepository extends JpaRepository<User, UUID> {
    // Custom queries
}
```

### Services

- **Format**: `{Entity}Service` or `{Domain}Service`
- **Examples**: `UserService`, `AuthService`, `OrderService`

```java
@Service
public class UserService {
    // Business logic
}
```

### Controllers

- **Format**: `{Entity}Controller` or `{Domain}Controller`
- **Examples**: `UserController`, `AuthController`, `ProductController`

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    // Endpoints
}
```

### DTOs (Data Transfer Objects)

- **Request DTOs**: `{Action}{Entity}Request`
  - `CreateUserRequest`
  - `UpdateProductRequest`
  - `LoginRequest`

- **Response DTOs**: `{Entity}Response`
  - `UserResponse`
  - `ProductResponse`
  - `LoginResponse`

```java
// Request
public record CreateProductRequest(
    String name,
    BigDecimal price
) {}

// Response
public record ProductResponse(
    UUID id,
    String name,
    BigDecimal price
) {}
```

### Exceptions

- **Format**: `{Reason}Exception`
- **Examples**:
  - `NotFoundException`
  - `BadCredentialsException` (from Spring Security)
  - `InsufficientStockException`
  - `DuplicateResourceException`

```java
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
```

## 📝 Method Naming

### Repository Methods

Follow Spring Data JPA conventions:

```java
// Find operations
List<User> findByEmail(String email);
List<User> findByNameContainingIgnoreCase(String name);
Optional<User> findByEmailAndActive(String email, boolean active);

// Exists checks
boolean existsByEmail(String email);

// Count operations
long countByStatus(Status status);

// Delete operations
void deleteByEmail(String email);
```

### Service Methods

- Use business domain language
- Start with verb

```java
// CRUD operations
public User create(User user)
public User findById(UUID id)
public List<User> findAll()
public User update(UUID id, User user)
public void delete(UUID id)

// Business operations
public Order processOrder(OrderRequest request)
public void sendNotification(UUID userId)
public BigDecimal calculateTotal(UUID orderId)
public boolean validateStock(UUID productId, Integer quantity)
```

### Controller Methods

Match HTTP methods:

```java
@GetMapping          // findAll(), findById()
@PostMapping         // create()
@PutMapping          // update()
@DeleteMapping       // delete()
@PatchMapping        // partialUpdate()
```

## 🔧 Variable Naming

### General Rules

- **camelCase** for variables and methods
- **PascalCase** for classes
- **UPPER_SNAKE_CASE** for constants
- **Descriptive names** over abbreviations

### Examples

```java
// Good
private final UserRepository userRepository;
private static final int MAX_LOGIN_ATTEMPTS = 3;
public List<Product> findActiveProducts()

// Bad
private final UserRepository ur;
private static final int MAX = 3;
public List<Product> getProds()
```

### Common Prefixes

```java
// Boolean variables
boolean isActive;
boolean hasPermission;
boolean canEdit;

// Collections
List<User> users;          // Plural
Set<Role> roles;
Map<String, User> userMap;

// Counts
int userCount;
long totalOrders;
```

## 🗄️ Database Conventions

### Table Names

- **Plural, lowercase, snake_case**
- Examples: `users`, `products`, `order_items`

```sql
CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
```

### Column Names

- **Lowercase, snake_case**
- **Descriptive names**

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(50),
    email_address VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Foreign Keys

- **Format**: `{referenced_table}_id`
- Examples: `user_id`, `product_id`, `category_id`

```sql
ALTER TABLE orders
ADD COLUMN user_id UUID,
ADD CONSTRAINT fk_orders_user 
    FOREIGN KEY (user_id) 
    REFERENCES users(id);
```

### Junction Tables (Many-to-Many)

- **Format**: `tb_{table1}_{table2}` or `{table1}_{table2}`
- Example: `tb_user_role` or `user_roles`

```sql
CREATE TABLE tb_user_role (
    user_id UUID NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);
```

### Indexes

- **Format**: `idx_{table}_{column(s)}`
- Examples: `idx_users_email`, `idx_products_name_status`

```sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_products_category ON products(category_id);
```

### Constraints

```sql
-- Primary key
CONSTRAINT pk_users PRIMARY KEY (id)

-- Foreign key
CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)

-- Unique
CONSTRAINT uc_users_email UNIQUE (email)

-- Check
CONSTRAINT chk_price_positive CHECK (price >= 0)
```

## 🔄 API Conventions

### URL Structure

```
/api/{version}/{resource}
/api/{version}/{resource}/{id}
/api/{version}/{resource}/{id}/{sub-resource}
```

**Examples**:

```
GET    /api/v1/products
GET    /api/v1/products/{id}
GET    /api/v1/products/{id}/reviews
POST   /api/v1/products
PUT    /api/v1/products/{id}
DELETE /api/v1/products/{id}
```

### HTTP Methods

- **GET**: Retrieve resources (safe, idempotent)
- **POST**: Create resources
- **PUT**: Full update (idempotent)
- **PATCH**: Partial update
- **DELETE**: Remove resources (idempotent)

### Status Codes

```java
// Success
200 OK           // Successful GET, PUT, PATCH, DELETE
201 Created      // Successful POST
204 No Content   // Successful DELETE (no body)

// Client Errors
400 Bad Request        // Validation failed
401 Unauthorized       // Not authenticated
403 Forbidden         // Not authorized
404 Not Found         // Resource not found
409 Conflict          // Duplicate resource

// Server Errors
500 Internal Server Error  // Unexpected error
```

### Response Format

**Success**:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { /* payload */ },
  "timestamp": "2025-12-11T10:30:00"
}
```

**Error**:

```json
{
  "timestamp": "2025-12-11T10:30:00.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 123",
  "path": "/api/v1/users/123"
}
```

## 📋 Annotation Usage

### Entity Annotations

```java
@Entity
@Table(name = "products")
@Getter @Setter  // Lombok
public class Product extends BaseEntity {
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;
}
```

### Service Annotations

```java
@Service
public class UserService {
    
    @Transactional(readOnly = true)
    public User findById(UUID id) {
        // Read operation
    }
    
    @Transactional
    public User create(User user) {
        // Write operation
    }
}
```

### Controller Annotations

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> findById(@PathVariable UUID id) {
        // ...
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> create(
            @Valid @RequestBody CreateUserRequest request) {
        // ...
    }
}
```

### Validation Annotations

```java
public record CreateProductRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    String name,
    
    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    BigDecimal price,
    
    @Min(0)
    Integer stock,
    
    @Email
    String contactEmail
) {}
```

## 🧪 Testing Conventions

### Test Class Naming

- **Format**: `{ClassUnderTest}Test`
- Examples: `UserServiceTest`, `ProductControllerTest`

### Test Method Naming

**Format**: `should{ExpectedBehavior}When{Condition}`

```java
@Test
void shouldReturnUserWhenValidIdProvided() {
    // ...
}

@Test
void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
    // ...
}

@Test
void shouldCreateOrderWhenStockIsAvailable() {
    // ...
}
```

### Test Structure (AAA Pattern)

```java
@Test
void shouldCalculateDiscountCorrectly() {
    // Arrange (Given)
    BigDecimal price = new BigDecimal("100.00");
    BigDecimal discount = new BigDecimal("0.10");
    
    // Act (When)
    BigDecimal result = priceService.applyDiscount(price, discount);
    
    // Assert (Then)
    assertEquals(new BigDecimal("90.00"), result);
}
```

## 📝 Comments & Documentation

### When to Comment

✅ **Good reasons**:

- Complex business logic
- Non-obvious algorithms
- Public API methods
- Workarounds or hacks

❌ **Bad reasons**:

- Explaining obvious code
- Redundant information
- Commented-out code

### JavaDoc for Public APIs

```java
/**
 * Calculates the total price for an order including taxes and discounts.
 * 
 * @param orderId The unique identifier of the order
 * @param includeShipping Whether to include shipping costs
 * @return The calculated total price
 * @throws NotFoundException if order doesn't exist
 * @throws InvalidOrderStateException if order is already paid
 */
public BigDecimal calculateTotal(UUID orderId, boolean includeShipping) {
    // Implementation
}
```

### Inline Comments

```java
// Good: Explains WHY
// Using BCrypt strength 12 for better security without impacting performance
private static final int BCRYPT_STRENGTH = 12;

// Bad: Explains WHAT (obvious from code)
// Set the name variable to the user's name
String name = user.getName();
```

## 🎨 Code Style

### Formatting

```java
// Braces on same line
if (condition) {
    doSomething();
}

// Space after keywords
if (x > 0) {
    // ...
}

// No space after method names
public void calculate() {
    // ...
}

// Space around operators
int result = a + b * c;

// One statement per line
User user = new User();
user.setName("John");
```

### Imports

- No wildcard imports
- Organize by: java.*, javax.*, third-party, project
- Remove unused imports

```java
// Good
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.yourcompany.project.User;

// Bad
import java.util.*;
import com.yourcompany.project.*;
```

### Blank Lines

```java
public class UserService {
    
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    
    public User findById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }
    
    public List<User> findAll() {
        return repository.findAll();
    }
}
```

## 🔒 Security Conventions

### Password Handling

```java
// Always hash passwords
String hashed = passwordEncoder.encode(plainPassword);

// Never log sensitive data
log.info("User login attempt: {}", email); // OK
log.info("Password: {}", password); // NEVER!
```

### SQL Injection Prevention

```java
// Use parameterized queries (JPA does this automatically)
@Query("SELECT u FROM User u WHERE u.email = :email")
User findByEmail(@Param("email") String email);

// For native queries, use parameters
@Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
User findByEmailNative(String email);
```

### Authorization

```java
// Check permissions before operations
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
public User updateUser(UUID userId, UpdateUserRequest request) {
    // ...
}
```

## 📊 Logging Conventions

### Log Levels

```java
// ERROR: Serious problems
log.error("Failed to process payment for order {}", orderId, exception);

// WARN: Potentially harmful situations
log.warn("Product {} is low on stock: {}", productId, stock);

// INFO: Informational messages
log.info("User {} logged in successfully", userId);

// DEBUG: Debugging information (dev only)
log.debug("Fetching user with email: {}", email);

// TRACE: Very detailed information (dev only)
log.trace("Method parameters: name={}, age={}", name, age);
```

### Logger Declaration

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    // Or use Lombok
    // @Slf4j
    // private static final Logger log; // Generated automatically
}
```

## ✅ Checklist for New Features

Before creating a PR:

- [ ] Code follows naming conventions
- [ ] All methods have descriptive names
- [ ] DTOs created for API requests/responses
- [ ] Validation added to request DTOs
- [ ] Database migration created
- [ ] Service methods use `@Transactional` appropriately
- [ ] Exceptions handled in `GlobalExceptionHandler`
- [ ] Unit tests written
- [ ] API documented in `API.md`
- [ ] No sensitive data logged
- [ ] Authorization checks added
- [ ] Code formatted consistently
- [ ] No unused imports
- [ ] Comments added for complex logic

---

**Consistency is key!** Following these conventions ensures maintainable, readable code across the entire project.
