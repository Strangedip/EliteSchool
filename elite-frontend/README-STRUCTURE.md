# Elite School Application Structure

This project follows the Angular 18 recommended folder structure and best practices.

## Folder Structure

```
src/
├── app/
│   ├── core/                  # Core module (singleton services, interceptors, etc.)
│   │   ├── models/            # Core domain models
│   │   ├── services/          # Application-wide singleton services
│   │   ├── interceptors/      # HTTP interceptors
│   │   └── core.module.ts     # Core module definition
│   │
│   ├── shared/                # Shared module (common components, pipes, directives)
│   │   ├── components/        # Reusable components
│   │   ├── directives/        # Custom directives
│   │   ├── pipes/             # Custom pipes
│   │   └── shared.module.ts   # Shared module definition
│   │
│   ├── features/              # Feature modules
│   │   ├── auth/              # Authentication feature (login, register, etc.)
│   │   ├── dashboard/         # Dashboard feature
│   │   └── ...                # Other feature modules
│   │
│   ├── layout/                # Layout components
│   │   ├── header/            # Header component
│   │   ├── footer/            # Footer component
│   │   └── ...                # Other layout components
│   │
│   └── app.component.*        # Root app component
│
├── assets/                    # Static assets
└── environments/              # Environment configuration
```

## Core vs. Shared

- **Core Module**: Contains singleton services used application-wide, like AuthService, UserService, etc. This module should be imported only once in the AppModule.
- **Shared Module**: Contains reusable components, directives, and pipes that can be imported by multiple feature modules.

## Feature Modules

Each feature should be contained in its own module, promoting separation of concerns and enabling lazy loading.

## Standard Naming Conventions

1. Feature modules: `feature-name.module.ts`
2. Components: `component-name.component.ts`
3. Services: `service-name.service.ts`
4. Models: `model-name.model.ts`
5. Interfaces: `interface-name.interface.ts`
6. Enums: `enum-name.enum.ts`
7. Guards: `guard-name.guard.ts`
8. Pipes: `pipe-name.pipe.ts`
9. Directives: `directive-name.directive.ts`

## Using the Core Module

The core module has been set up to handle application-wide concerns like authentication, HTTP interceptors, and user management. This ensures a consistent approach to these shared concerns throughout the application.

### Services

- `AuthService`: Handles user authentication, token management
- `UserService`: Manages user profile and data
- `ToastService`: Provides toast notifications

### Interceptors

- `AuthInterceptor`: Adds authorization headers to requests
- `HttpErrorInterceptor`: Handles HTTP errors and displays appropriate messages

### Models

- `User`: Core user model
- `CommonResponseDto`: Standard API response format

## Angular 18 Best Practices

1. Use standalone components by default
2. Use the functional approach for interceptors with `withInterceptors`
3. Use environment variables for configuration
4. Use typed forms with `FormControl<T>` and reactive forms
5. Use signals for reactive state management
6. Implement proper lazy loading for feature modules

## Migration Notes

When migrating existing components, make sure to:

1. Update import paths to use the core module for shared services
2. Use standardized models from core
3. Follow the new folder structure for new components 