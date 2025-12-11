# EliteSchool Frontend

A modern, gamified learning platform built with **Angular 18** and **PrimeNG**. EliteSchool enables students to earn reward points by completing tasks, track their achievements, and redeem points for rewards in the Elite Store.

## 🎯 Features

- **Dashboard** - Overview of student progress, reward points, and recent activity
- **Task Management** - Browse, submit, and track task completion
- **Reward Store** - Redeem earned points for exclusive items
- **Wallet System** - View balance and transaction history
- **Games** - Mini-games including Tic-Tac-Toe and Rock-Paper-Scissors
- **User Profiles** - View and manage personal information
- **Role-Based Access** - Different views for Students, Faculty, and Admins

## 🛠 Tech Stack

- **Framework**: Angular 18 (Standalone Components)
- **UI Library**: PrimeNG with Aura Theme
- **Styling**: SCSS with CSS Variables
- **State Management**: RxJS BehaviorSubjects
- **HTTP**: Angular HttpClient with Interceptors
- **Routing**: Angular Router with Lazy Loading

## 📁 Project Structure

```
src/app/
├── app.component.*           # Root component
├── app.routes.ts             # Main routing configuration
│
├── core/                     # Singleton services & utilities
│   ├── enums/               # Application enums (roles, etc.)
│   ├── guards/              # Route guards (AuthGuard, GuestGuard)
│   ├── interceptors/        # HTTP interceptors (auth, error handling)
│   ├── models/              # TypeScript interfaces & models
│   └── services/            # Application services
│       ├── auth.service.ts
│       ├── user.service.ts
│       ├── task.service.ts
│       ├── wallet.service.ts
│       ├── store.service.ts
│       └── toast.service.ts
│
├── features/                 # Feature modules (lazy-loaded)
│   ├── auth/                # Authentication (login, register)
│   ├── dashboard/           # Main dashboard
│   ├── tasks/               # Task board and management
│   ├── store/               # Reward store
│   ├── wallet/              # Wallet and transactions
│   ├── profile/             # User profile
│   ├── games/               # Mini-games
│   ├── settings/            # User settings
│   ├── home/                # Landing page
│   └── not-found/           # 404 page
│
├── layouts/                  # Layout components
│   └── main-layout/         # Authenticated app layout
│
└── shared/                   # Reusable components
    └── top-navbar/          # Navigation bar
```

## 🚀 Getting Started

### Prerequisites

- Node.js 18+ 
- npm 9+
- Angular CLI 18+

### Installation

```bash
# Clone the repository
git clone <repository-url>

# Navigate to frontend directory
cd elite-frontend

# Install dependencies
npm install

# Start development server
ng serve
```

The application will be available at `http://localhost:4200`

### Environment Configuration

The app connects to the backend API Gateway at `http://localhost:8080` by default.

## 📜 Available Routes

| Route | Description | Access |
|-------|-------------|--------|
| `/home` | Landing page | Public |
| `/login` | User login | Guest only |
| `/register` | User registration | Guest only |
| `/dashboard` | Main dashboard | Authenticated |
| `/tasks` | Task board | Authenticated |
| `/store` | Reward store | Authenticated |
| `/wallet` | Wallet & transactions | Authenticated |
| `/profile` | User profile | Authenticated |
| `/games` | Mini-games | Authenticated |
| `/settings` | User settings | Authenticated |

## 🔐 Authentication

- JWT-based authentication
- Token stored in localStorage
- Automatic token refresh handling
- Route guards for protected pages
- HTTP interceptor for API authentication

## 🎨 Theming

The application uses a dark theme with cyan/blue accent colors. Theme variables are defined in `styles.scss`:

```scss
:root {
  --primary: #0077b6;
  --primary-light: #00b4d8;
  --accent: #00d4ff;
  --bg-dark: #050f1e;
  --text: #f1f5f9;
}
```

## 📦 Build

```bash
# Development build
ng build

# Production build
ng build --configuration production
```

Build artifacts are stored in the `dist/` directory.

## 🧪 Testing

```bash
# Run unit tests
ng test

# Run tests with coverage
ng test --code-coverage
```

## 📝 Code Style

- Standalone components throughout
- Functional route guards
- Lazy loading for all feature modules
- RxJS for reactive state management
- Strict TypeScript configuration

## 📄 License

This project is proprietary software.
