# EliteSchool Configuration Repository

This repository contains externalized configuration for EliteSchool microservices.

## Structure

```
eliteschool-config/
├── local/                    # Local development
│   ├── auth-service.properties
│   ├── task-service.properties
│   ├── wallet-service.properties
│   ├── store-service.properties
│   └── api-gateway.yml
├── development/              # Development environment
│   ├── auth-service.properties
│   └── ...
├── production/               # Production environment
│   ├── auth-service.properties
│   └── ...
└── README.md
```

## How It Works

1. **Config Server** fetches configs from this repo
2. **Services** request their config from Config Server
3. Config is loaded based on **profile** (local/development/production)

## Usage

### Starting Services with Profile

```bash
# Local
java -jar auth-service.jar --spring.profiles.active=local

# Development
java -jar auth-service.jar --spring.profiles.active=development

# Production
java -jar auth-service.jar --spring.profiles.active=production
```

### Environment Variables

For sensitive data, use environment variables:

| Variable | Description |
|----------|-------------|
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing key |
| `EMAIL_USERNAME` | SMTP username |
| `EMAIL_PASSWORD` | SMTP password |
| `FRONTEND_URL` | Frontend URL for links |

## Config Server Setup

**Environment Variables for Config Server:**

```bash
CONFIG_REPO_URI=https://github.com/YOUR_USERNAME/eliteschool-config
CONFIG_REPO_USERNAME=your-github-username
CONFIG_REPO_TOKEN=your-github-token
CONFIG_SERVER_USER=configuser
CONFIG_SERVER_PASSWORD=configpass
```

## Endpoints

| URL | Description |
|-----|-------------|
| `http://localhost:8888/auth-service/local` | Local config for auth-service |
| `http://localhost:8888/task-service/development` | Dev config for task-service |
| `http://localhost:8888/api-gateway/production` | Prod config for api-gateway |

## Security Notes

1. **Never commit secrets** - Use environment variables
2. **Private repo recommended** for production configs
3. **Use GitHub tokens** instead of passwords
4. **Encrypt sensitive properties** in production

