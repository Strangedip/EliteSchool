# EliteSchool Backend

Spring Boot **4.1** / Java **25** microservices for Elite Points, tasks, rewards, nominations, and support. Clients talk only to the **API Gateway**.

| Related docs | |
|--------------|--|
| Product / demo | [USAGE_GUIDE.md](../USAGE_GUIDE.md) |
| Root setup | [README.md](../README.md) |
| Env sample | [.env.example](../.env.example) |
| Frontend | [elite-frontend/README.md](../elite-frontend/README.md) |

---

## Architecture

```
Client → API Gateway (:8080)
           ├── Identity (:8081) — login, users, profiles, support
           ├── Task (:8082) — tasks, templates, submissions, courses
           ├── Points (:8083) — Elite Points ledger, nominations, purchase orchestration
           └── Rewards (:8084) — catalog, claim windows, stock, claims
Consul (:8500) · PostgreSQL (:5432)
```

---

## Services

| Service | Port | Responsibility |
|---------|------|----------------|
| **api-gateway** | 8080 | JWT validation, routing, CORS |
| **identity-service** | 8081 | Login, users, roles, profiles, support; demo Admin bootstrap; SMTP for password reset |
| **task-service** | 8082 | Tasks, templates, submissions, courses, evidence / min-notes / rubric |
| **points-service** | 8083 | Balances, credits/debits, nominations, rewards purchase orchestration |
| **rewards-service** | 8084 | Materials / Opportunities, claim windows, stock, claims |
| **common-utils** | — | Shared DTOs / exceptions (`com.eliteschool`) |
| **Consul** | 8500 | Discovery + KV (`consul/config/*.yml`) |

Demo Admin (when no Admin exists): **`admin`** / **`Admin@123`**.

---

## Getting started

### Native (recommended on 16GB hosts)

1. Install **JDK 25**, **Consul** (on `PATH`), and **PostgreSQL** with DB `EliteSchool`.
2. Optional: copy repo-root `.env.example` → `.env`.
3. Build and run:

```bat
build-all.bat
start-all.bat
```

`build-all.bat` prefers JDK 25 when installed under `C:\Program Files\Java\jdk-25.0.4`.  
`start-all.bat` starts Consul → seeds KV → identity/task/points/rewards → gateway. It does **not** start PostgreSQL.

```bat
stop-all.bat
```

From the repo root you can also use `demo-native.bat` (backend + frontend).

### Docker (optional)

From the **repo root** (not this folder):

```bash
cp .env.example .env
docker compose up --build
```

Images use **Temurin 25**. Compose Postgres is published on host **5433** to avoid clashing with a local Postgres on 5432. Cap Docker Desktop RAM (~4–6GB) on 16GB machines.

---

## Environment

See [../.env.example](../.env.example).

| Variable | Purpose |
|----------|---------|
| `JWT_SECRET` | Must match across gateway + all services |
| `DB_*` | PostgreSQL connection |
| `FRONTEND_URL` | Password-reset links |
| `CORS_ORIGINS` | Allowed browser origins (gateway) |
| `EMAIL_*` | SMTP — optional for core demo; required for Forgot Password |
| `CONSUL_HOST` / `CONSUL_PORT` | Native discovery (Compose sets these itself) |

Consul KV stores YAML with `${…}` placeholders; **Spring resolves them at runtime** from each service’s environment.

---

## API (via gateway `http://localhost:8080`)

### Identity / users / courses

```
POST /api/identity/signup | login | logout
GET  /api/identity/validate-token | profile
POST /api/identity/forgot-password | reset-password | change-password
GET  /api/user | /api/user/{id} | /api/user/students
POST /api/user/create
PUT  /api/user/{id} | /{id}/status | /{id}/role
GET|POST|PUT|DELETE /api/courses
```

### Tasks / templates / submissions

```
GET|POST|PUT|DELETE /api/tasks/...
POST /api/tasks/from-template/{id}
PUT  /api/tasks/{id}/close
GET|POST|PUT|DELETE /api/task-templates
POST /api/task-submissions
PUT  /api/task-submissions/{id}/verify
```

### Points / nominations

```
GET  /api/points/{userId}/balance | transactions
POST /api/points/{userId}/credit | debit
POST /api/points/{userId}/purchase/{itemId}
GET  /api/points/leaderboard
POST|GET /api/points/nominations
PUT  /api/points/nominations/{id}/approve   # Admin only
PUT  /api/points/nominations/{id}/reject    # Admin only
```

### Rewards / support

```
GET|POST|PUT|DELETE /api/rewards/items
GET  /api/rewards/purchases | /purchases/student/{id}
GET|POST /api/support/tickets
POST /api/support/tickets/{id}/messages
PUT  /api/support/tickets/{id}/status
```

---

## Security

- JWT: `Authorization: Bearer <token>` (cookie `AUTH_TOKEN` also set on login; Angular uses Bearer)
- Trusted identity headers from gateway: `e-username`, `e-user-role`, `e-user-id`
- Roles: `ADMIN`, `MANAGEMENT`, `FACULTY`, `STUDENT`
- Nomination approve/reject = **Admin only**
- Public signup = **Student only**
- SMTP failures must not break login; Forgot Password needs real `EMAIL_*`

---

## Configuration notes

- Per-service infra (port, DB, Consul) lives in `application.properties` / `application.yml`.
- Shared JWT / mail / frontend URL also live under `consul/config/` and can be overridden by env.
- Inter-service (Feign): **Points → Rewards** (purchase), **Task → Points** (award points).

---

## License

Proprietary — all rights reserved unless otherwise agreed.
