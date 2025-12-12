# Quick Reference Guide

Quick reference for common tasks and commands in this Spring Boot template.

## 🚀 Common Commands

### Project Setup

```bash
# Generate JWT keys
openssl genrsa -out src/main/resources/app.key 2048
openssl rsa -in src/main/resources/app.key -pubout -out src/main/resources/app.pub

# Start database
docker-compose up -d

# Run application
./mvnw spring-boot:run

# Build JAR
./mvnw clean package -DskipTests
```

### Development

```bash
# Run tests
./mvnw test

# Run specific test
./mvnw test -Dtest=UserServiceTest

# Format code (if configured)
./mvnw spring-javaformat:apply

# Check for updates
./mvnw versions:display-dependency-updates
```

### Database

```bash
# Access PostgreSQL
docker exec -it <container-id> psql -U postgres -d real_state_db

# View migrations
psql> SELECT * FROM flyway_schema_history;

# Stop database
docker-compose down

# Reset database (deletes all data)
docker-compose down -v
docker-compose up -d
```

## 📡 API Endpoints

### Authentication

```bash
# Register
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'

# Save token
TOKEN="eyJhbGci..."

# Use authenticated endpoint
curl http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer $TOKEN"
```

## 🏗️ Creating New Features

### 1. Create Entity

```java
@Entity
@Table(name = "products")
@Getter @Setter
public class Product extends BaseEntity {
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private BigDecimal price;
}
```

### 2. Create Repository

```java
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByNameContaining(String name);
}
```

### 3. Create Service

```java
@Service
public class ProductService {
    private final ProductRepository repository;
    
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    public Product findById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));
    }
}
```

### 4. Create DTOs

```java
public record CreateProductRequest(
    @NotBlank String name,
    @NotNull BigDecimal price
) {}

public record ProductResponse(
    UUID id,
    String name,
    BigDecimal price
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice()
        );
    }
}
```

### 5. Create Controller

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService service;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAll() {
        List<ProductResponse> products = service.findAll()
            .stream()
            .map(ProductResponse::from)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request) {
        Product product = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(ProductResponse.from(product)));
    }
}
```

### 6. Create Migration

**File**: `src/main/resources/db/migration/V2__CREATE_PRODUCTS_TABLE.sql`

```sql
CREATE TABLE products (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);
```

## 🔐 Security

### Add Public Endpoint

In `SecurityConfig.java`:

```java
public static final String[] PUBLIC_ENDPOINTS = {
    "/api/v1/auth/**",
    "/api/v1/products"  // Add new public endpoint
};
```

### Protect Endpoint with Role

```java
@GetMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<User>> getAllUsers() {
    // Only admins can access
}

@PostMapping("/products")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public ResponseEntity<Product> create(...) {
    // Admins or managers can access
}

@GetMapping("/profile")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<User> getProfile() {
    // Any authenticated user can access
}
```

## ❌ Exception Handling

### Create Custom Exception

```java
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
```

### Add Handler

In `GlobalExceptionHandler.java`:

```java
@ExceptionHandler(InsufficientStockException.class)
public ResponseEntity<StandardError> handleInsufficientStock(
        InsufficientStockException e,
        HttpServletRequest request) {
    
    StandardError error = new StandardError(
        Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Insufficient Stock",
        e.getMessage(),
        request.getRequestURI()
    );
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
}
```

### Use Exception

```java
if (product.getStock() < quantity) {
    throw new InsufficientStockException("Not enough stock available");
}
```

## 🗄️ Database Queries

### Simple Queries

```java
// Repository methods (Spring Data JPA)
List<Product> findByName(String name);
List<Product> findByPriceGreaterThan(BigDecimal price);
List<Product> findByNameContainingIgnoreCase(String name);
Optional<Product> findByNameAndActive(String name, boolean active);
boolean existsByName(String name);
long countByCategory(String category);
```

### Custom JPQL Query

```java
@Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
List<Product> findByPriceRange(
    @Param("min") BigDecimal min,
    @Param("max") BigDecimal max
);
```

### Native SQL Query

```java
@Query(value = "SELECT * FROM products WHERE status = ?1", nativeQuery = true)
List<Product> findByStatusNative(String status);
```

## 🧪 Testing

### Unit Test (Service)

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository repository;
    
    @InjectMocks
    private ProductService service;
    
    @Test
    void shouldFindProductById() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        when(repository.findById(id)).thenReturn(Optional.of(product));
        
        Product result = service.findById(id);
        
        assertNotNull(result);
        verify(repository).findById(id);
    }
}
```

### Integration Test (Controller)

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService service;
    
    @Test
    void shouldReturnAllProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
}
```

## 🔧 Configuration

### Change Server Port

`application.properties`:

```properties
server.port=8081
```

### Change Token Expiration

In `AuthService.java`:

```java
var expiresIn = 7200L; // 2 hours (in seconds)
```

### Change Database

`application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/my_database
spring.datasource.username=myuser
spring.datasource.password=mypassword
```

### Enable SQL Logging

`application.properties`:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## 📊 Useful SQL Commands

```sql
-- View all tables
\dt

-- Describe table
\d users

-- View migrations
SELECT * FROM flyway_schema_history;

-- Count records
SELECT COUNT(*) FROM users;

-- Insert test user (with BCrypt hash)
INSERT INTO users (id, created_at, updated_at, name, email, password)
VALUES (
    gen_random_uuid(),
    NOW(),
    NOW(),
    'Test User',
    'test@example.com',
    '$2a$10$hash...' -- Use bcrypt online generator
);

-- View user roles
SELECT u.name, r.authority
FROM users u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;
```

## 🐛 Troubleshooting

### Port in Use

```bash
# Find process
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Database Connection Failed

```bash
# Check if running
docker ps

# Check logs
docker logs <container-id>

# Restart
docker-compose restart
```

### Flyway Error

```bash
# Repair
./mvnw flyway:repair

# Or reset (dev only)
docker-compose down -v
docker-compose up -d
```

### Lombok Not Working

**IntelliJ**:

1. Settings → Plugins → Install "Lombok"
2. Settings → Build → Compiler → Annotation Processors → Enable

**Eclipse**:

1. Download lombok.jar
2. Run: `java -jar lombok.jar`

## 📝 Response Templates

### Success Response

```java
return ResponseEntity.ok(
    ApiResponse.success(data, "Custom message")
);

// Or with default message
return ResponseEntity.ok(
    ApiResponse.success(data)
);

// Or message only
return ResponseEntity.ok(
    ApiResponse.success("Operation completed")
);
```

### Created Response

```java
return ResponseEntity.status(HttpStatus.CREATED)
    .body(ApiResponse.success(data, "Resource created"));
```

### Error Response (Throw Exception)

```java
throw new NotFoundException("Resource not found");
throw new BadCredentialsException("Invalid credentials");
throw new CustomException("Custom error message");
```

## 🔑 JWT Decode (Testing)

### Online Tools

- [jwt.io](https://jwt.io)
- [jwt.ms](https://jwt.ms)

### Command Line

```bash
# Decode JWT (doesn't verify signature)
echo "eyJhbGci..." | cut -d'.' -f2 | base64 -d | jq
```

### JavaScript

```javascript
function parseJwt(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  return JSON.parse(atob(base64));
}

const decoded = parseJwt(token);
console.log(decoded);
```

## 📚 Documentation Links

- **Full Docs**: `/docs/` folder
- **Architecture**: [ARCHITECTURE.md](ARCHITECTURE.md)
- **API Reference**: [API.md](API.md)
- **Setup Guide**: [SETUP.md](SETUP.md)
- **Development Guide**: [DEVELOPMENT.md](DEVELOPMENT.md)
- **Conventions**: [CONVENTIONS.md](CONVENTIONS.md)

## 🎯 Shortcuts

### Create Everything for New Entity

```bash
# 1. Create package
mkdir -p src/main/java/com/yourcompany/yourproject/product

# 2. Create files:
# - Product.java (entity)
# - ProductRepository.java
# - ProductService.java
# - ProductController.java
# - dtos/CreateProductRequest.java
# - dtos/ProductResponse.java

# 3. Create migration
# src/main/resources/db/migration/V{N}__CREATE_{TABLE}_TABLE.sql

# 4. Run application (auto-migrates)
./mvnw spring-boot:run
```

### Quick Test Workflow

```bash
# Test endpoint
curl http://localhost:8080/api/v1/products

# Check logs
tail -f logs/application.log

# Run tests
./mvnw test -Dtest=ProductServiceTest
```

---

**Tip**: Bookmark this page for quick access! 🔖

For detailed explanations, see the [full documentation](../README.md).
