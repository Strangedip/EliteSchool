# EliteSchool

A full-stack **gamified learning platform** where students earn reward points by completing tasks, track their achievements, and redeem points for rewards. Built with Angular 18 and Spring Boot microservices.

## 🎯 Overview

EliteSchool transforms the educational experience by incorporating gamification elements:

- **Students** complete tasks assigned by faculty to earn reward points
- **Faculty** create and verify tasks, approve student submissions  
- **Admins** manage the system, users, and store inventory
- **Everyone** can view leaderboards and track progress

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    ELITESCHOOL FRONTEND                          │
│                  Angular 18 + PrimeNG                            │
│                     (Port 4200)                                  │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY                                 │
│                Spring Cloud Gateway                              │
│                     (Port 8080)                                  │
└─────────────────────────────────────────────────────────────────┘
                                │
    ┌───────────────┬───────────┴───────────┬───────────────┐
    │               │                       │               │
    ▼               ▼                       ▼               ▼
┌────────┐   ┌────────┐              ┌────────┐      ┌────────┐
│  AUTH  │   │  TASK  │              │ WALLET │      │ STORE  │
│  8081  │   │  8082  │              │  8083  │      │  8084  │
└────────┘   └────────┘              └────────┘      └────────┘
                                │
                                ▼
                    ┌─────────────────────┐
                    │   EUREKA SERVER     │
                    │       8761          │
                    └─────────────────────┘
```

## 📦 Project Structure

```
EliteSchool/
├── elite-frontend/          # Angular 18 SPA
│   ├── src/app/
│   │   ├── core/           # Services, guards, interceptors
│   │   ├── features/       # Feature modules (lazy-loaded)
│   │   ├── layouts/        # Layout components
│   │   └── shared/         # Reusable components
│   └── README.md
│
├── elite-backend/           # Spring Boot Microservices
│   ├── eureka-server/      # Service discovery
│   ├── api-gateway/        # API gateway with JWT validation
│   ├── auth-service/       # Authentication & user management
│   ├── task-service/       # Task & submission management
│   ├── wallet-service/     # Points & transactions
│   ├── store-service/      # Reward store
│   ├── common-utils/       # Shared utilities
│   └── README.md
│
└── README.md               # This file
```

## 🛠 Tech Stack

### Frontend
- Angular 18 (Standalone Components)
- PrimeNG + PrimeFlex
- RxJS
- SCSS

### Backend
- Spring Boot 3.x
- Spring Cloud (Gateway, Eureka, OpenFeign)
- Spring Security + JWT
- Spring Data JPA
- H2 Database (configurable)

## 🚀 Quick Start

### Prerequisites
- Node.js 18+
- Java 17+
- Maven 3.8+

### Backend Setup

**Quick Start (Recommended):**

The startup scripts work on **any platform** and from **any directory**:

```bash
cd elite-backend

# Windows
start-all.bat

# macOS / Linux
chmod +x start-all.sh  # First time only
./start-all.sh
```

The script automatically starts all 6 services in the correct order with proper delays.

**To stop all services:**
```bash
# Windows
stop-all.bat

# macOS / Linux
./stop-all.sh
```

**Manual Setup (Alternative):**
```bash
cd elite-backend

# Start Eureka Server first
cd eureka-server && mvn spring-boot:run &

# Start microservices
cd ../auth-service && mvn spring-boot:run &
cd ../task-service && mvn spring-boot:run &
cd ../wallet-service && mvn spring-boot:run &
cd ../store-service && mvn spring-boot:run &

# Start API Gateway last
cd ../api-gateway && mvn spring-boot:run &
```

### Frontend Setup

```bash
cd elite-frontend

# Install dependencies
npm install

# Start development server
ng serve
```

### Access Points

| Component | URL |
|-----------|-----|
| Frontend | http://localhost:4200 |
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |

## 👥 User Roles

| Role | Capabilities |
|------|-------------|
| **ADMIN** | Full system access, user management, store management |
| **FACULTY** | Create tasks, verify submissions, view student progress |
| **STUDENT** | Complete tasks, earn points, redeem rewards |
| **GUEST** | Limited read-only access |

## 🎮 Key Features

### For Students
- View and accept available tasks
- Submit completed work with evidence
- Track earned reward points
- Redeem points in the store
- View transaction history
- Play mini-games

### For Faculty
- Create and manage tasks
- Set reward points for tasks
- Review and verify submissions
- Approve/reject student work

### For Admins
- Manage all users
- Add/edit store items
- View system analytics
- Full CRUD on all entities

## 📖 Documentation

- [Frontend Documentation](./elite-frontend/README.md)
- [Backend Documentation](./elite-backend/README.md)

## 🔐 Authentication

- JWT-based authentication
- Tokens expire after 24 hours
- Secure password hashing with BCrypt
- Role-based access control on all endpoints

## 📄 License

This project is proprietary software.

---

**EliteSchool** - Empowering students through gamified learning
