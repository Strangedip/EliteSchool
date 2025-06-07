# EliteSchool

## Project Overview
EliteSchool is a personal project demonstrating a full-stack application with a Spring Boot backend and an Angular frontend. It aims to provide a robust and scalable solution for managing school-related functionalities, leveraging microservices architecture for the backend.

## Technologies Used

### Backend (elite-backend)
The backend is built with Spring Boot and follows a microservices architecture.
- **Framework**: Spring Boot `3.2.12`
- **Language**: Java `21`
- **Build Tool**: Maven
- **Microservices**:
    - `api-gateway`: Spring Cloud Gateway for routing and authentication.
    - `auth-service`: Handles user authentication and authorization.
    - `eureka-server`: Spring Cloud Eureka for service discovery.
    - `store-service`: Manages store-related data and logic.
    - `task-service`: Manages tasks and assignments.
    - `wallet-service`: Handles wallet and payment functionalities.
    - `common-utils`: Common utilities and shared components.
- **Security**: Spring Security, JWT (`0.11.5`)
- **Other Libraries**: Lombok

### Frontend (elite-frontend)
The frontend is a single-page application developed with Angular.
- **Framework**: Angular `18.0.0`
- **Language**: TypeScript `5.4.0`
- **Package Manager**: npm
- **UI Libraries**:
    - PrimeNG `18.0.2`
    - PrimeFlex `4.0.0`
    - PrimeIcons `7.0.0`
    - Bootstrap `5.3.3`

## Setup Steps

### Prerequisites
Before you begin, ensure you have the following installed:
- Java Development Kit (JDK) `21` or later
- Apache Maven `3.6.0` or later
- Node.js `18` or later
- npm `8` or later (usually comes with Node.js)
- Git

### Backend Setup

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/EliteSchool.git
    cd EliteSchool
    ```
2.  **Navigate to the backend directory**:
    ```bash
    cd elite-backend
    ```
3.  **Build the backend microservices**:
    ```bash
    mvn clean install
    ```
    This command will build all the individual microservices.
4.  **Run the Eureka Server**:
    First, navigate to the `eureka-server` directory and run it. The Eureka Server should be started before any other microservices.
    ```bash
    cd eureka-server
    mvn spring-boot:run
    ```
5.  **Run other microservices**:
    In separate terminal windows, navigate to each microservice directory (e.g., `api-gateway`, `auth-service`, `store-service`, `task-service`, `wallet-service`) and run them:
    ```bash
    cd ../api-gateway # or other service directory
    mvn spring-boot:run
    ```
    Repeat for all desired microservices.

### Frontend Setup

1.  **Navigate to the frontend directory**:
    ```bash
    cd ../elite-frontend
    ```
2.  **Install frontend dependencies**:
    ```bash
    npm install
    ```
3.  **Start the Angular development server**:
    ```bash
    npm start
    ```
    The frontend application will typically be available at `http://localhost:4200/`.

## Running the Application
Once both the backend microservices (especially Eureka Server and API Gateway) and the frontend development server are running, you can access the application through your web browser at `http://localhost:4200/`.

## Project Structure
```
EliteSchool/
├── elite-backend/             # Spring Boot Backend
│   ├── api-gateway/         # API Gateway
│   ├── auth-service/        # Authentication Service
│   ├── common-utils/        # Common Utility Module
│   ├── eureka-server/       # Service Discovery Server
│   ├── store-service/       # Store Management Service
│   ├── task-service/        # Task Management Service
│   └── wallet-service/      # Wallet Service
├── elite-frontend/            # Angular Frontend
│   ├── src/                 # Source code
│   ├── angular.json         # Angular CLI configuration
│   ├── package.json         # Frontend dependencies and scripts
│   └── tsconfig.json        # TypeScript configuration
├── .gitignore               # Git ignore file
└── README.md                # Project README
```

## Contributing
Contributions are welcome! Please feel free to open issues or submit pull requests.

## License
This project is licensed under the MIT License - see the LICENSE.md file for details. (Note: A LICENSE.md file is not yet created. You might want to add one.)
