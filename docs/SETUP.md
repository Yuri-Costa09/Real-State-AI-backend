# Setup Guide

Complete guide for setting up and running the Spring Boot Authentication Template.

## 📋 Prerequisites

### Required Software

| Software | Version | Download Link |
|----------|---------|---------------|
| Java JDK | 21+ | [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/) |
| Maven | 3.8+ | [Maven](https://maven.apache.org/download.cgi) |
| Docker | Latest | [Docker Desktop](https://www.docker.com/products/docker-desktop) |
| PostgreSQL | 15+ | [PostgreSQL](https://www.postgresql.org/download/) (or use Docker) |

### Recommended Tools

- **IDE**: IntelliJ IDEA, VS Code, or Eclipse
- **API Testing**: Postman, Insomnia, or cURL
- **Database Client**: DBeaver, pgAdmin, or DataGrip

## 🚀 Quick Setup

### 1. Clone Repository

```bash
git clone <your-repository-url>
cd template_project_spring_boot
```

### 2. Generate RSA Keys for JWT

The application uses RSA public/private key pairs for JWT signing.

**Option A: Using OpenSSL (Recommended)**

```bash
# Navigate to resources directory
cd src/main/resources

# Generate 2048-bit RSA private key
openssl genrsa -out app.key 2048

# Generate public key from private key
openssl rsa -in app.key -pubout -out app.pub

# Verify files were created
ls -l app.key app.pub
```

**Option B: Using Online Tool**

1. Visit [cryptotools.net/rsagen](https://cryptotools.net/rsagen)
2. Generate 2048-bit key pair
3. Save private key as `src/main/resources/app.key`
4. Save public key as `src/main/resources/app.pub`

**Security Note**:

- Keep `app.key` private and never commit it to version control
- Add to `.gitignore`:

  ```
  src/main/resources/app.key
  ```

### 3. Start Database

**Option A: Using Docker Compose (Recommended)**

```bash
# Start PostgreSQL in background
docker-compose up -d

# Verify it's running
docker ps
```

**Option B: Using Local PostgreSQL**

If you have PostgreSQL installed locally:

```sql
CREATE DATABASE real_state_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE real_state_db TO postgres;
```

Update `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/real_state_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 4. Run Application

**Option A: Using Maven**

```bash
./mvnw spring-boot:run
```

**Option B: Using IDE**

1. Import project as Maven project
2. Wait for dependencies to download
3. Run `RealStateAiBackendApplication.java`

**Option C: Build and Run JAR**

```bash
# Build
./mvnw clean package -DskipTests

# Run
java -jar target/real_state_ai_backend-0.0.1-SNAPSHOT.jar
```

### 5. Verify Setup

```bash
# Health check (if actuator is enabled)
curl http://localhost:8080/actuator/health

# Should return: {"status":"UP"}
```

## 🔧 Configuration

### Database Configuration

**File**: `src/main/resources/application.properties`

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5434/real_state_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

### JWT Configuration

```properties
# JWT Key Paths
jwt.pub.key=classpath:app.pub
jwt.private.key=classpath:app.key
```

**To change token expiration**, edit `AuthService.java`:

```java
var expiresIn = 3600L; // Change from 1 hour to desired seconds
```

### Port Configuration

```properties
# Change application port (default: 8080)
server.port=8080
```

### Database Ports in Docker

**File**: `compose.yaml`

```yaml
services:
  postgres:
    ports:
      - '5434:5432'  # Host:Container
```

## 🗄️ Database Management

### Accessing PostgreSQL

**Via Docker**:

```bash
docker exec -it <container-id> psql -U postgres -d real_state_db
```

**Via pgAdmin/DBeaver**:

- Host: `localhost`
- Port: `5434`
- Database: `real_state_db`
- Username: `postgres`
- Password: `postgres`

### Flyway Migrations

Migrations are in `src/main/resources/db/migration/`

**Creating New Migration**:

1. Create file: `V2__DESCRIPTION.sql`

   ```sql
   -- V2__ADD_PHONE_TO_USERS.sql
   ALTER TABLE users ADD COLUMN phone VARCHAR(20);
   ```

2. Run application - migration executes automatically

**Migration Naming Convention**:

- `V{version}__{description}.sql`
- Version: Sequential number (1, 2, 3...)
- Double underscore before description
- Description: UPPERCASE_WITH_UNDERSCORES

**Check Migration Status**:

```sql
SELECT * FROM flyway_schema_history;
```

### Resetting Database

```bash
# Stop containers
docker-compose down

# Remove volumes (deletes all data)
docker-compose down -v

# Start fresh
docker-compose up -d
```

## 🔐 Security Setup

### Generate Stronger RSA Keys (Production)

For production, use 4096-bit keys:

```bash
openssl genrsa -out app.key 4096
openssl rsa -in app.key -pubout -out app.pub
```

### Environment Variables (Production)

Instead of `application.properties`, use environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-host:5432/prod_db
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export JWT_PRIVATE_KEY=/secure/path/to/app.key
export JWT_PUBLIC_KEY=/secure/path/to/app.pub
```

**File**: `application.properties`

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
jwt.private.key=${JWT_PRIVATE_KEY}
jwt.pub.key=${JWT_PUBLIC_KEY}
```

### Securing Sensitive Files

Add to `.gitignore`:

```
# Sensitive files
src/main/resources/app.key
src/main/resources/app.pub
application-prod.properties

# Environment files
.env
.env.local
```

## 🧪 Testing Setup

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run with coverage
./mvnw verify

# Skip tests during build
./mvnw package -DskipTests
```

### H2 In-Memory Database for Tests

For testing, configure H2:

**File**: `src/test/resources/application-test.properties`

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.flyway.enabled=false
```

**Run tests with test profile**:

```bash
./mvnw test -Dspring.profiles.active=test
```

## 🐳 Docker Setup

### Running Application in Docker

**Create Dockerfile**:

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
COPY src/main/resources/app.key /app/app.key
COPY src/main/resources/app.pub /app/app.pub
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build and Run**:

```bash
# Build JAR
./mvnw clean package -DskipTests

# Build Docker image
docker build -t spring-auth-template .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5434/real_state_db \
  spring-auth-template
```

### Docker Compose with App

**File**: `compose.yaml`

```yaml
services:
  postgres:
    image: 'postgres:latest'
    environment:
      - 'POSTGRES_DB=real_state_db'
      - 'POSTGRES_PASSWORD=postgres'
      - 'POSTGRES_USER=postgres'
    ports:
      - '5434:5432'
    volumes:
      - postgres_data:/var/lib/postgresql/data
  
  app:
    build: .
    ports:
      - '8080:8080'
    depends_on:
      - postgres
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/real_state_db

volumes:
  postgres_data:
```

**Run everything**:

```bash
docker-compose up --build
```

## 🎯 IDE Setup

### IntelliJ IDEA

1. **Import Project**:
   - File → Open → Select `pom.xml`
   - Import as Maven project

2. **Enable Annotation Processing**:
   - Settings → Build → Compiler → Annotation Processors
   - ✓ Enable annotation processing

3. **Install Lombok Plugin**:
   - Settings → Plugins → Marketplace
   - Search "Lombok" → Install

4. **Set Java Version**:
   - File → Project Structure → Project
   - SDK: Java 21
   - Language Level: 21

### VS Code

1. **Install Extensions**:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support

2. **Open Folder**:
   - File → Open Folder → Select project root

3. **Maven Auto-Detection**:
   - VS Code will automatically detect `pom.xml`

### Eclipse

1. **Import Maven Project**:
   - File → Import → Maven → Existing Maven Projects
   - Select project root

2. **Install Lombok**:
   - Download lombok.jar
   - Run: `java -jar lombok.jar`
   - Select Eclipse installation

## 📊 Monitoring & Debugging

### Actuator Endpoints

**Enable in** `pom.xml` (already included):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Configure in** `application.properties`:

```properties
# Expose all endpoints
management.endpoints.web.exposure.include=*

# Or specific endpoints
management.endpoints.web.exposure.include=health,info,metrics
```

**Access endpoints**:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics
```

## 🚀 Production Deployment

### Build for Production

```bash
# Build optimized JAR
./mvnw clean package -Pprod -DskipTests

# Run with production profile
java -jar target/app.jar --spring.profiles.active=prod
```

### Production Checklist

- [ ] Use environment variables for sensitive data
- [ ] Generate 4096-bit RSA keys
- [ ] Enable HTTPS/TLS
- [ ] Configure CORS properly
- [ ] Set up database backups
- [ ] Enable monitoring (Actuator + APM)
- [ ] Configure logging (ELK, Splunk, etc.)
- [ ] Set up CI/CD pipeline
- [ ] Load testing and performance tuning
- [ ] Security audit

### Environment-Specific Configuration

**File**: `application-prod.properties`

```properties
# Production database
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}

# Production JWT keys (from secure storage)
jwt.private.key=${JWT_PRIVATE_KEY_PATH}
jwt.pub.key=${JWT_PUBLIC_KEY_PATH}

# Disable debug
logging.level.root=WARN
spring.jpa.show-sql=false

# Production actuator
management.endpoints.web.exposure.include=health,metrics
```

## 📚 Next Steps

After setup:

1. ✅ Test authentication: [API Documentation](API.md)
2. 📖 Understand architecture: [Architecture Guide](ARCHITECTURE.md)
3. 🔨 Start building: [Development Guide](DEVELOPMENT.md)
4. 🎨 Customize for your project

**Setup complete!** 🎉 Your template is ready for development.
