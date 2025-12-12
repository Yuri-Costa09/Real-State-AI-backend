# API Documentation

Complete API reference for the Spring Boot Authentication Template.

## 📋 Base URL

```
http://localhost:8080
```

## 🔑 Authentication

Most endpoints require JWT authentication. Include the token in the `Authorization` header:

```http
Authorization: Bearer <your-jwt-token>
```

### Public Endpoints

The following endpoints do not require authentication:

- `POST /login`
- `POST /register`

## 📡 Endpoints

---

### Authentication

#### Register New User

Creates a new user account with encrypted password.

**Endpoint**: `POST /register`

**Authentication**: Not required

**Request Body**:

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123!"
}
```

**Success Response** (201 Created):

```json
{
  "success": true,
  "message": "Usuário registrado com sucesso.",
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "John Doe",
    "email": "john.doe@example.com"
  },
  "timestamp": "2025-12-11T10:30:00"
}
```

**Error Response** (400 Bad Request):

```json
{
  "timestamp": "2025-12-11T10:30:00.123Z",
  "status": 400,
  "error": "Bad Credentials",
  "message": "Usuário já registrado com este e-mail.",
  "path": "/register"
}
```

**Validation Rules**:

- `name`: Required, non-empty
- `email`: Required, valid email format, unique
- `password`: Required, minimum 6 characters (customize in code)

**Example cURL**:

```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "SecurePassword123!"
  }'
```

---

#### User Login

Authenticates user and returns JWT token.

**Endpoint**: `POST /login`

**Authentication**: Not required

**Request Body**:

```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123!"
}
```

**Success Response** (200 OK):

```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxMjNlNDU2Ny1lODliLTEyZDMtYTQ1Ni00MjY2MTQxNzQwMDAiLCJlbWFpbCI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwibmFtZSI6IkpvaG4gRG9lIiwicm9sZXMiOlt7ImlkIjoxLCJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImlzcyI6InJlYWwtc3RhdGUtYWktYmFja2VuZCIsImlhdCI6MTcwMjMwMDgwMCwiZXhwIjoxNzAyMzA0NDAwfQ...",
  "expiresIn": 1702304400
}
```

**Error Response** (401 Unauthorized):

```json
{
  "timestamp": "2025-12-11T10:30:00.123Z",
  "status": 401,
  "error": "Bad Credentials",
  "message": "Email ou senha inválidos.",
  "path": "/login"
}
```

**Token Information**:

- **Algorithm**: RSA256
- **Expiration**: 3600 seconds (1 hour)
- **Claims**: sub, email, name, roles, iss, iat, exp

**Example cURL**:

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePassword123!"
  }'
```

**Using the Token**:

```bash
# Save token to variable
TOKEN="eyJhbGciOiJSUzI1NiJ9..."

# Use in authenticated requests
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔒 Protected Endpoints (Examples)

These endpoints require authentication. Add them to your application as needed.

### Get Current User

**Endpoint**: `GET /api/v1/users/me`

**Authentication**: Required

**Headers**:

```
Authorization: Bearer <token>
```

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "roles": [
      {
        "id": 1,
        "authority": "ROLE_USER"
      }
    ],
    "createdAt": "2025-12-01T10:00:00Z",
    "updatedAt": "2025-12-11T10:30:00Z"
  },
  "timestamp": "2025-12-11T10:30:00"
}
```

**Example Implementation**:

```java
@GetMapping("/api/v1/users/me")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<User>> getCurrentUser(Authentication auth) {
    UUID userId = UUID.fromString(auth.getName());
    User user = userService.findById(userId);
    return ResponseEntity.ok(ApiResponse.success(user));
}
```

---

### List All Users (Admin Only)

**Endpoint**: `GET /api/v1/users`

**Authentication**: Required (ROLE_ADMIN)

**Success Response** (200 OK):

```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "roles": ["ROLE_USER"]
    },
    {
      "id": "223e4567-e89b-12d3-a456-426614174001",
      "name": "Jane Smith",
      "email": "jane.smith@example.com",
      "roles": ["ROLE_USER", "ROLE_ADMIN"]
    }
  ],
  "timestamp": "2025-12-11T10:30:00"
}
```

**Example Implementation**:

```java
@GetMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
    List<User> users = userService.listAllUsers();
    return ResponseEntity.ok(ApiResponse.success(users));
}
```

---

## 📝 Response Format Standards

### Success Response

All successful API responses follow this structure:

```typescript
{
  success: boolean,        // Always true for success
  message: string,         // Human-readable message
  data: T | null,         // Response payload (generic type)
  timestamp: string       // ISO 8601 timestamp
}
```

### Error Response

All error responses follow this structure:

```typescript
{
  timestamp: string,      // ISO 8601 timestamp
  status: number,         // HTTP status code
  error: string,          // Error type (e.g., "Not Found")
  message: string,        // Detailed error message
  path: string           // Request path that caused error
}
```

## 🚫 Error Codes

### 400 Bad Request

- Invalid request body
- Validation failures
- User already exists (registration)

### 401 Unauthorized

- Invalid credentials
- Missing JWT token
- Expired JWT token
- Invalid JWT signature

### 403 Forbidden

- Insufficient permissions
- Valid token but lacking required role

### 404 Not Found

- Resource not found
- User not found

### 500 Internal Server Error

- Unexpected server error
- Database connection issues

## 🔐 Security Headers

### Required Headers for Protected Endpoints

```http
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

### Optional Headers

```http
Accept: application/json
User-Agent: YourApp/1.0
```

## 🎯 Working with JWT Tokens

### Token Lifecycle

1. **Obtain Token**: Call `POST /login` with credentials
2. **Use Token**: Include in `Authorization` header for protected endpoints
3. **Token Expires**: After 1 hour (3600 seconds)
4. **Refresh**: Login again to get new token

### Extracting Token Information

The JWT token contains:

```json
{
  "sub": "user-uuid",              // User ID
  "email": "user@example.com",     // User email
  "name": "John Doe",              // User name
  "roles": [                       // User roles
    {
      "id": 1,
      "authority": "ROLE_USER"
    }
  ],
  "iss": "real-state-ai-backend",  // Issuer
  "iat": 1702300800,               // Issued at
  "exp": 1702304400                // Expires at
}
```

### Decoding JWT (Client-Side)

**JavaScript Example**:

```javascript
function parseJwt(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split('')
      .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
      .join('')
  );
  return JSON.parse(jsonPayload);
}

const token = "eyJhbGci...";
const decoded = parseJwt(token);
console.log(decoded.email); // user@example.com
```

**Note**: This only decodes the token. Signature verification happens server-side.

## 📊 Pagination (Future Implementation)

For endpoints returning lists, consider implementing pagination:

**Request**:

```http
GET /api/v1/users?page=0&size=20&sort=name,asc
```

**Response**:

```json
{
  "success": true,
  "data": {
    "content": [...],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 150,
    "totalPages": 8
  },
  "timestamp": "2025-12-11T10:30:00"
}
```

## 🔍 Filtering (Future Implementation)

## 🧪 Testing Endpoints

### Using cURL

```bash
# Register
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"password123"}'

# Login
TOKEN=$(curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}' \
  | jq -r '.token')

# Use token
curl http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer $TOKEN"
```

### Using Postman

1. **Register User**:
   - Method: POST
   - URL: `http://localhost:8080/register`
   - Body (JSON):

     ```json
     {
       "name": "Test User",
       "email": "test@example.com",
       "password": "password123"
     }
     ```

2. **Login**:
   - Method: POST
   - URL: `http://localhost:8080/login`
   - Body (JSON):

     ```json
     {
       "email": "test@example.com",
       "password": "password123"
     }
     ```

   - Copy the `token` from response

3. **Protected Endpoint**:
   - Method: GET
   - URL: `http://localhost:8080/api/v1/users/me`
   - Headers:
     - Key: `Authorization`
     - Value: `Bearer <paste-token-here>`

## 📚 Additional Resources

- [HTTP Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- [JWT.io](https://jwt.io/) - Decode and verify JWT tokens
- [REST API Best Practices](https://restfulapi.net/)

---

**Need to add a new endpoint?** See [Development Guide](DEVELOPMENT.md) for step-by-step instructions.
