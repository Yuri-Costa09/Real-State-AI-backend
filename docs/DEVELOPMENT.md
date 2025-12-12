# Development Guide

Learn how to extend and customize the Spring Boot Authentication Template for your specific needs.

## 📚 Table of Contents

- [Adding New Entities](#adding-new-entities)
- [Creating Custom Exceptions](#creating-custom-exceptions)
- [Implementing Role-Based Access](#implementing-role-based-access)
- [Adding API Endpoints](#adding-api-endpoints)
- [Working with DTOs](#working-with-dtos)
- [Database Migrations](#database-migrations)
- [Testing](#testing)
- [Best Practices](#best-practices)

## 🆕 Adding New Entities

### Step 1: Create the Entity

**Location**: `src/main/java/com/yourcompany/yourproject/[domain]/`

**Example**: Creating a `Product` entity

```java
package com.yourcompany.yourproject.product;

import com.yourcompany.yourproject.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter @Setter
public class Product extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;
    
    // Constructors
    protected Product() {}
    
    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }
}
```

**Enum for Status**:

```java
public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    OUT_OF_STOCK,
    DISCONTINUED
}
```

### Step 2: Create the Repository

```java
package com.yourcompany.yourproject.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    // Custom query methods
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByStatus(ProductStatus status);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByStockGreaterThan(Integer stock);
    
    // Custom JPQL query
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.stock > 0")
    List<Product> findAvailableProducts(@Param("status") ProductStatus status);
}
```

### Step 3: Create the Service

```java
package com.yourcompany.yourproject.product;

import com.yourcompany.yourproject.shared.errors.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

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
            .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }
    
    public List<Product> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }
    
    @Transactional
    public Product create(Product product) {
        return repository.save(product);
    }
    
    @Transactional
    public Product update(UUID id, Product updatedProduct) {
        Product existing = findById(id);
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStock(updatedProduct.getStock());
        return repository.save(existing);
    }
    
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
```

### Step 4: Create DTOs

```java
package com.yourcompany.yourproject.product.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    String name,
    
    @Size(max = 1000)
    String description,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    BigDecimal price,
    
    @Min(value = 0, message = "Stock cannot be negative")
    Integer stock
) {}

public record ProductResponse(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String status,
    String createdAt,
    String updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getStatus().toString(),
            product.getCreatedAt().toString(),
            product.getUpdatedAt().toString()
        );
    }
}
```

### Step 5: Create the Controller

```java
package com.yourcompany.yourproject.product;

import com.yourcompany.yourproject.product.dtos.*;
import com.yourcompany.yourproject.shared.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final ProductService service;
    
    public ProductController(ProductService service) {
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAll() {
        List<ProductResponse> products = service.findAll()
            .stream()
            .map(ProductResponse::from)
            .toList();
        
        return ResponseEntity.ok(
            ApiResponse.success(products, "Products retrieved successfully")
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable UUID id) {
        Product product = service.findById(id);
        return ResponseEntity.ok(
            ApiResponse.success(ProductResponse.from(product))
        );
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> search(
            @RequestParam String name) {
        List<ProductResponse> products = service.searchByName(name)
            .stream()
            .map(ProductResponse::from)
            .toList();
        
        return ResponseEntity.ok(
            ApiResponse.success(products)
        );
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request) {
        
        Product product = new Product(request.name(), request.price());
        product.setDescription(request.description());
        product.setStock(request.stock());
        
        Product saved = service.create(product);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                ProductResponse.from(saved),
                "Product created successfully"
            ));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductRequest request) {
        
        Product product = new Product(request.name(), request.price());
        product.setDescription(request.description());
        product.setStock(request.stock());
        
        Product updated = service.update(id, product);
        
        return ResponseEntity.ok(
            ApiResponse.success(
                ProductResponse.from(updated),
                "Product updated successfully"
            )
        );
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success("Product deleted successfully")
        );
    }
}
```

### Step 6: Create Database Migration

**File**: `src/main/resources/db/migration/V2__CREATE_PRODUCTS_TABLE.sql`

```sql
CREATE TABLE products (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT chk_price_positive CHECK (price >= 0),
    CONSTRAINT chk_stock_non_negative CHECK (stock >= 0)
);

CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_price ON products(price);
```

## ❌ Creating Custom Exceptions

### Step 1: Create Exception Class

**Location**: `src/main/java/com/yourcompany/yourproject/shared/errors/`

```java
package com.yourcompany.yourproject.shared.errors;

public class InsufficientStockException extends RuntimeException {
    private final Integer requested;
    private final Integer available;
    
    public InsufficientStockException(Integer requested, Integer available) {
        super(String.format(
            "Insufficient stock. Requested: %d, Available: %d", 
            requested, 
            available
        ));
        this.requested = requested;
        this.available = available;
    }
    
    public Integer getRequested() {
        return requested;
    }
    
    public Integer getAvailable() {
        return available;
    }
}
```

### Step 2: Add Handler to GlobalExceptionHandler

```java
@ExceptionHandler(InsufficientStockException.class)
public ResponseEntity<StandardError> handleInsufficientStock(
        InsufficientStockException e,
        HttpServletRequest request) {
    
    HttpStatus status = HttpStatus.BAD_REQUEST;
    
    StandardError err = new StandardError(
        Instant.now(),
        status.value(),
        "Insufficient Stock",
        e.getMessage(),
        request.getRequestURI()
    );
    
    return ResponseEntity.status(status).body(err);
}
```

### Step 3: Use in Service

```java
@Transactional
public Order createOrder(UUID productId, Integer quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Product not found"));
    
    if (product.getStock() < quantity) {
        throw new InsufficientStockException(quantity, product.getStock());
    }
    
    // Process order...
}
```

## 🔐 Implementing Role-Based Access

### Step 1: Add Roles to Database

**Migration**: `V3__ADD_DEFAULT_ROLES.sql`

```sql
INSERT INTO roles (id, authority) VALUES 
    (1, 'ROLE_USER'),
    (2, 'ROLE_ADMIN'),
    (3, 'ROLE_MANAGER');
```

### Step 2: Assign Roles During Registration

```java
@Service
public class UserService {
    
    private final RoleRepository roleRepository;
    
    @Transactional
    public User saveUser(String name, String email, String password) {
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User newUser = new User(name, email, encodedPassword);
        
        // Assign default role
        Role userRole = roleRepository.findByAuthority("ROLE_USER")
            .orElseThrow(() -> new NotFoundException("Role not found"));
        
        newUser.getRoles().add(userRole);
        
        return userRepository.save(newUser);
    }
}
```

### Step 3: Protect Endpoints

**Method-Level Security**:

```java
@GetMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<User>> getAllUsers() {
    // Only accessible by ADMIN role
}

@PostMapping("/products")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    // Accessible by ADMIN or MANAGER
}

@GetMapping("/profile")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<User> getProfile() {
    // Accessible by any authenticated user
}
```

**Controller-Level Security**:

```java
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    // All methods require ADMIN role
}
```

**Custom Security Expressions**:

```java
@GetMapping("/users/{id}")
@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
public ResponseEntity<User> getUser(@PathVariable UUID id) {
    // User can access their own data, or admin can access any
}
```

## 🔄 Working with DTOs

### Why Use DTOs?

1. **Separation of Concerns**: API contract vs domain model
2. **Security**: Avoid exposing sensitive fields
3. **Versioning**: Change API without changing entities
4. **Validation**: Different rules for create/update

### Pattern: Entity ↔ DTO Conversion

**Using Records (Java 16+)**:

```java
// Request DTO
public record CreateUserRequest(
    @NotBlank String name,
    @Email String email,
    @Size(min = 6) String password
) {}

// Response DTO
public record UserResponse(
    UUID id,
    String name,
    String email,
    List<String> roles
) {
    // Factory method
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRoles().stream()
                .map(Role::getAuthority)
                .toList()
        );
    }
}
```

**Using Mapper Service**:

```java
@Component
public class ProductMapper {
    
    public Product toEntity(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        return product;
    }
    
    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice()
        );
    }
    
    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
            .map(this::toResponse)
            .toList();
    }
}
```

## 🗄️ Database Migrations

### Best Practices

1. **Never modify existing migrations** - create new ones
2. **Always test migrations** on dev database first
3. **Include rollback scripts** for critical changes
4. **Use descriptive names**

### Complex Migration Example

**File**: `V4__ADD_CATEGORIES_AND_RELATIONSHIPS.sql`

```sql
-- Create categories table
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add category relationship to products
ALTER TABLE products
ADD COLUMN category_id UUID,
ADD CONSTRAINT fk_products_category 
    FOREIGN KEY (category_id) 
    REFERENCES categories(id) 
    ON DELETE SET NULL;

-- Insert default categories
INSERT INTO categories (id, name, description) VALUES
    (gen_random_uuid(), 'Electronics', 'Electronic devices'),
    (gen_random_uuid(), 'Clothing', 'Apparel and accessories'),
    (gen_random_uuid(), 'Books', 'Books and publications');

-- Create index for faster lookups
CREATE INDEX idx_products_category ON products(category_id);
```

### Data Migration Example

**File**: `V5__MIGRATE_LEGACY_DATA.sql`

```sql
-- Update existing products with default category
UPDATE products
SET category_id = (
    SELECT id FROM categories WHERE name = 'Electronics' LIMIT 1
)
WHERE category_id IS NULL;
```

## 🧪 Testing

### Unit Testing Services

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository repository;
    
    @InjectMocks
    private ProductService service;
    
    @Test
    void shouldFindProductById() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product("Test", new BigDecimal("99.99"));
        when(repository.findById(id)).thenReturn(Optional.of(product));
        
        // When
        Product result = service.findById(id);
        
        // Then
        assertNotNull(result);
        assertEquals("Test", result.getName());
        verify(repository).findById(id);
    }
    
    @Test
    void shouldThrowNotFoundWhenProductDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(NotFoundException.class, () -> service.findById(id));
    }
}
```

### Integration Testing Controllers

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService service;
    
    @Test
    void shouldReturnAllProducts() throws Exception {
        // Given
        List<Product> products = List.of(
            new Product("Product 1", new BigDecimal("10.00")),
            new Product("Product 2", new BigDecimal("20.00"))
        );
        when(service.findAll()).thenReturn(products);
        
        // When & Then
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(2));
    }
}
```

### Repository Testing

```java
@DataJpaTest
class ProductRepositoryTest {
    
    @Autowired
    private ProductRepository repository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void shouldFindProductsByName() {
        // Given
        Product product = new Product("Gaming Laptop", new BigDecimal("1200.00"));
        entityManager.persist(product);
        entityManager.flush();
        
        // When
        List<Product> found = repository.findByNameContainingIgnoreCase("laptop");
        
        // Then
        assertFalse(found.isEmpty());
        assertEquals("Gaming Laptop", found.get(0).getName());
    }
}
```

## 🎯 Best Practices

### 1. **Use Constructor Injection**

✅ Good:

```java
@Service
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

❌ Bad:

```java
@Service
public class UserService {
    @Autowired
    private UserRepository repository;
}
```

### 2. **Validate at Controller Level**

```java
@PostMapping
public ResponseEntity<ApiResponse<User>> create(
        @Valid @RequestBody CreateUserRequest request) {
    // Validation happens automatically
}
```

### 3. **Use Transactions for Write Operations**

```java
@Transactional
public Order processOrder(OrderRequest request) {
    // Multiple database operations in one transaction
    product.decreaseStock(request.quantity());
    Order order = orderRepository.save(new Order(request));
    notificationService.sendConfirmation(order);
    return order;
}
```

### 4. **Handle Exceptions Consistently**

Always throw custom exceptions, let `GlobalExceptionHandler` handle them.

### 5. **Use Meaningful Names**

```java
// Good
public Product findActiveProductsInCategory(UUID categoryId)

// Bad
public Product get(UUID id)
```

### 6. **Keep Controllers Thin**

Controllers should only handle HTTP concerns, delegate to services.

### 7. **Document Complex Logic**

```java
/**
 * Calculates discounted price based on user tier and promotion.
 * 
 * @param basePrice Original price before discounts
 * @param userTier Customer tier (BRONZE, SILVER, GOLD)
 * @param promotionCode Optional promotion code
 * @return Final price after all discounts applied
 */
public BigDecimal calculatePrice(BigDecimal basePrice, UserTier userTier, String promotionCode) {
    // Implementation
}
```

## 🔄 Common Patterns

### Soft Delete

```java
@Entity
public class Product extends BaseEntity {
    private boolean deleted = false;
    
    @PreRemove
    public void preRemove() {
        this.deleted = true;
    }
}

// Repository
List<Product> findByDeletedFalse();
```

### Auditing (Already Included)

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
```

### Pagination

```java
@GetMapping
public ResponseEntity<Page<Product>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Product> products = productService.findAll(pageable);
    
    return ResponseEntity.ok(products);
}
```

## 📚 Additional Resources

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Flyway Documentation](https://flywaydb.org/documentation/)

---

**Happy coding!** 🚀 You now have everything you need to extend this template for your projects.
