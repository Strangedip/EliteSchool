# EliteSchool Backend

A microservices-based backend for the EliteSchool gamified learning platform. Built with **Spring Boot 3** and **Spring Cloud**, this system provides APIs for user authentication, task management, reward wallets, and a store system.

## 🏗 Architecture

EliteSchool backend follows a **microservices architecture** with the following components:

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT (Frontend)                        │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY (:8080)                         │
│              JWT Validation, Routing, Load Balancing             │
└─────────────────────────────────────────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
        ▼                       ▼                       ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│ AUTH SERVICE  │    │ TASK SERVICE  │    │ WALLET SERVICE│
│    (:8081)    │    │    (:8082)    │    │    (:8083)    │
└───────────────┘    └───────────────┘    └───────────────┘
                                                   │
                                                   ▼
                                          ┌───────────────┐
                                          │ STORE SERVICE │
                                          │    (:8084)    │
                                          └───────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    EUREKA SERVER (:8761)                         │
│                    Service Discovery                             │
└─────────────────────────────────────────────────────────────────┘
```

## 📦 Microservices

### Eureka Server (`eureka-server`)
Service discovery server that enables microservices to locate each other.
- **Port**: 8761
- **Dashboard**: http://localhost:8761

### API Gateway (`api-gateway`)
Central entry point for all client requests.
- **Port**: 8080
- **Features**:
  - JWT token validation
  - Request routing to microservices
  - CORS configuration
  - Load balancing

### Auth Service (`auth-service`)
Handles user authentication and authorization.
- **Port**: 8081
- **Features**:
  - User registration and login
  - JWT token generation
  - Password encryption (BCrypt)
  - User profile management
  - Role-based access (ADMIN, FACULTY, STUDENT, GUEST)

### Task Service (`task-service`)
Manages tasks and submissions.
- **Port**: 8082
- **Features**:
  - Task CRUD operations
  - Task submission by students
  - Task verification by faculty
  - Task types: SINGLE (one student) / MULTIPLE (many students)
  - Status tracking: OPEN, SUBMITTED, COMPLETED, CLOSED

### Wallet Service (`wallet-service`)
Manages student reward points and transactions.
- **Port**: 8083
- **Features**:
  - Wallet balance management
  - Point credits (task completion rewards)
  - Point debits (store purchases)
  - Transaction history
  - Inter-service communication with Store Service

### Store Service (`store-service`)
Manages the reward store.
- **Port**: 8084
- **Features**:
  - Store item management
  - Item purchase processing
  - Stock management
  - Integration with Wallet Service via Feign Client

### Common Utils (`common-utils`)
Shared library used across all services.
- **Components**:
  - `CommonResponseDto` - Standardized API response format
  - `GlobalExceptionHandler` - Centralized exception handling
  - `AppException` - Custom application exception
  - `ResponseUtil` - Response builder utilities

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.x
- **Cloud**: Spring Cloud (Gateway, Eureka, OpenFeign)
- **Security**: Spring Security + JWT
- **Database**: H2 (In-Memory) - configurable for MySQL/PostgreSQL
- **Build Tool**: Maven
- **Java Version**: 17+

## 📁 Service Structure

Each microservice follows a consistent structure:

```
service-name/
├── pom.xml
└── src/main/java/com/eliteschool/service_name/
    ├── ServiceApplication.java      # Main application class
    ├── config/                      # Configuration classes
    ├── controller/                  # REST controllers
    ├── dto/                         # Data Transfer Objects
    │   ├── request/                # Request DTOs
    │   └── response/               # Response DTOs
    ├── exception/                   # Custom exceptions
    ├── mapper/                      # Entity-DTO mappers
    ├── model/                       # JPA entities
    ├── repository/                  # Spring Data repositories
    └── service/                     # Business logic
```

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+

### Quick Start (Recommended)

Use the provided startup scripts to launch all services automatically. Scripts are **cross-platform** and work regardless of where the project is cloned.

**Windows:**
```cmd
cd elite-backend
start-all.bat
# or with PowerShell
.\start-all.ps1
```

**macOS / Linux:**
```bash
cd elite-backend
chmod +x start-all.sh stop-all.sh  # First time only
./start-all.sh
```

The script will:
1. Start Eureka Server and wait for it to initialize (25 seconds)
2. Start each microservice in sequence with proper delays
3. Start the API Gateway last
4. Display all service URLs when complete
5. Create logs in `elite-backend/logs/` directory (Unix/macOS)

### Stopping All Services

**Windows:**
```cmd
stop-all.bat
# or with PowerShell
.\stop-all.ps1
```

**macOS / Linux:**
```bash
./stop-all.sh
```

### Manual Startup (Alternative)

If you prefer to start services manually, run them in this order:

```bash
# 1. Start Eureka Server (Service Discovery)
cd eureka-server
mvn spring-boot:run

# 2. Start Auth Service
cd auth-service
mvn spring-boot:run

# 3. Start Task Service
cd task-service
mvn spring-boot:run

# 4. Start Wallet Service
cd wallet-service
mvn spring-boot:run

# 5. Start Store Service
cd store-service
mvn spring-boot:run

# 6. Start API Gateway (Last)
cd api-gateway
mvn spring-boot:run
```

### Service URLs

| Service | Port | URL |
|---------|------|-----|
| Eureka Dashboard | 8761 | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080 |
| Auth Service | 8081 | http://localhost:8081 |
| Task Service | 8082 | http://localhost:8082 |
| Wallet Service | 8083 | http://localhost:8083 |
| Store Service | 8084 | http://localhost:8084 |

## 📡 API Endpoints

All endpoints are accessed through the API Gateway at `http://localhost:8080`

### Authentication
```
POST   /api/auth/signup          # User registration
POST   /api/auth/login           # User login
GET    /api/auth/validate-token  # Validate JWT token
GET    /api/auth/profile         # Get current user profile
POST   /api/auth/logout          # User logout
```

### Users
```
GET    /api/users                # Get all users (Admin)
GET    /api/users/{id}           # Get user by ID
PUT    /api/users/{id}           # Update user
DELETE /api/users/{id}           # Delete user (Admin)
```

### Tasks
```
GET    /api/tasks/all            # Get all tasks
GET    /api/tasks/{id}           # Get task by ID
POST   /api/tasks/create         # Create new task (Faculty/Admin)
PUT    /api/tasks/{id}           # Update task
DELETE /api/tasks/{id}           # Delete task
GET    /api/tasks/status/{status}       # Get tasks by status
PUT    /api/tasks/{id}/complete/{userId} # Complete a task
PUT    /api/tasks/{id}/close            # Close a task
```

### Task Submissions
```
POST   /api/submissions          # Submit task
GET    /api/submissions/student/{id}    # Get student submissions
PUT    /api/submissions/{id}/verify     # Verify submission (Faculty)
```

### Wallet
```
GET    /api/wallet/{userId}              # Get wallet balance
GET    /api/wallet/{userId}/transactions # Get transaction history
POST   /api/wallet/{userId}/credit       # Credit points
POST   /api/wallet/{userId}/debit        # Debit points
```

### Store
```
GET    /api/store/items          # Get all store items
GET    /api/store/items/{id}     # Get item by ID
POST   /api/store/items          # Add new item (Admin)
PUT    /api/store/items/{id}     # Update item (Admin)
DELETE /api/store/items/{id}     # Delete item (Admin)
POST   /api/store/purchase       # Purchase item
```

## 🔐 Security

- **JWT Authentication**: All protected endpoints require a valid JWT token
- **Token Header**: `Authorization: Bearer <token>`
- **Password Encryption**: BCrypt with strength 10
- **Role-Based Access Control**:
  - `ADMIN` - Full system access
  - `FACULTY` - Task management, verification
  - `STUDENT` - Task submission, store purchases
  - `GUEST` - Limited read access

## 📊 Response Format

All API responses follow a standardized format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "error": null,
  "timestamp": "2025-12-12T10:30:00Z"
}
```

Error responses:
```json
{
  "success": false,
  "message": "Operation failed",
  "data": null,
  "error": {
    "errorCode": "VALIDATION_ERROR",
    "errorDescription": "Invalid input data",
    "details": "Email format is invalid"
  },
  "timestamp": "2025-12-12T10:30:00Z"
}
```

## 🧪 Testing

```bash
# Run tests for a specific service
cd service-name
mvn test

# Run tests with coverage
mvn test jacoco:report
```

## 📝 Configuration

Each service has its own `application.properties` file. Key configurations:

```properties
# Service Discovery
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

# Database (H2 default, can be changed to MySQL/PostgreSQL)
spring.datasource.url=jdbc:h2:mem:dbname
spring.jpa.hibernate.ddl-auto=update

# JWT Configuration (Auth Service)
jwt.secret=your-secret-key
jwt.expiration=86400000
```

## 🔄 Inter-Service Communication

Services communicate using **OpenFeign** clients:
- Store Service → Wallet Service (for purchase transactions)
- Task Service → Wallet Service (for reward credits)

## 📄 License

This project is proprietary software.

