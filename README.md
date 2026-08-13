# EliteSchool

Platform for schools to manage **student achievement** through verified contribution. Students build a trusted record of effort; **Elite Points** recognise that work and unlock school **rewards** so every learner can stay equipped and moving forward.

**Java 25** · **Spring Boot 4.1** · **Angular 22.1** · **PrimeNG 22** · **PostgreSQL** · **Consul**

| Doc | For |
|-----|-----|
| [USAGE_GUIDE.md](./USAGE_GUIDE.md) | Schools, teachers, client demos |
| [elite-backend/README.md](./elite-backend/README.md) | Backend developers |
| [elite-frontend/README.md](./elite-frontend/README.md) | Frontend developers |
| [.env.example](./.env.example) | Environment variables |

---

## Mission

| Principle | Meaning |
|-----------|---------|
| Achievement first | Tasks and verification build a clear record of student contribution |
| Elite Points | School measure of verified effort — not a payment system |
| Rewards that include everyone | Rewards materials & opportunities help students who need support stay equipped |
| Staff-initiated credits | Elite Points adjustment or Admin-approved nomination — never via Support |
| Recreation separate | Games do not earn points |

---

## Who uses it

| Role | Responsibility |
|------|----------------|
| **Student** | Tasks, Elite Points, Rewards, support (concerns), contribution profile |
| **Faculty** | Tasks/templates, verify work, nominate contributions |
| **Admin / Management** | Users, Rewards, courses, audit, point adjustments; **Admin only** approves nomination credits |
| **Visitors** | Home, Docs, Games (no login) |

Public signup = **students only**. Fresh database seeds demo Admin: username **`admin`** / password **`Admin@123`** (change after first login).

---

## Architecture

```
Angular (:4200) → API Gateway (:8080)
                    ├── Identity (:8081)
                    ├── Task (:8082)
                    ├── Points (:8083)
                    └── Rewards (:8084)
Consul (:8500) · PostgreSQL (:5432 native / :5433 host port in Docker)
```

- **Native `ng serve`:** browser → `http://localhost:8080/api`
- **Docker frontend:** same-origin `/api` (nginx → gateway)

---

## Prerequisites

| Tool | Notes |
|------|--------|
| **JDK 25** | Scripts prefer `C:\Program Files\Java\jdk-25.0.4` when present |
| **Maven** | Or use each service’s `mvnw.cmd` |
| **Node.js** | `^22.22.3` (or 24 / 26) |
| **PostgreSQL** | Database named `EliteSchool` (not started by backend scripts) |
| **Consul** | On `PATH` for native runs — [download](https://developer.hashicorp.com/consul/downloads) |

Copy [.env.example](./.env.example) to `.env` at the repo root if you need custom DB/JWT/SMTP values. Defaults work for local demos.

---

## Quick start (native — recommended on 16GB hosts)

Prefer native so Docker Desktop does not compete with the OS, browser, and IDE for RAM.

```bat
demo-native.bat
```

Or manually:

```bat
cd elite-backend
build-all.bat
start-all.bat

cd ..\elite-frontend
npm install
npm start
```

| | URL / credentials |
|--|--|
| App | http://localhost:4200 |
| API | http://localhost:8080 |
| Consul UI | http://localhost:8500 |
| Demo login | **`admin`** / **`Admin@123`** (username, not email) |

`start-all.bat` starts Consul → seeds KV → services → gateway. It does **not** start PostgreSQL.

---

## Docker Compose (optional)

Cap Docker Desktop memory to about **4–6GB**. From the repo root:

```bash
cp .env.example .env   # optional
docker compose up --build
```

Then open http://localhost:4200.

| | |
|--|--|
| App | http://localhost:4200 |
| API | http://localhost:8080 |
| Consul | http://localhost:8500 |
| Postgres (host tools) | **localhost:5433** → container `5432` |

Compose maps Postgres to host **5433** so it does not clash with a local PostgreSQL on **5432**. Services inside Compose still use `postgres:5432`.

For LAN access, set `FRONTEND_URL` and `CORS_ORIGINS` in `.env`.

---

## Roles & access

| Capability | Student | Faculty | Admin / Management |
|------------|:-------:|:-------:|:------------------:|
| Dashboard, tasks, courses, leaderboard, profile | ✓ | ✓ | ✓ |
| Submit / resubmit work | ✓ | — | — |
| Create & verify tasks | — | ✓ | ✓ |
| Wallet / store purchases | ✓ | — | — |
| Store catalog CRUD | — | — | ✓ |
| Support | ✓ | ✓ | ✓ |
| Nominations (create) | — | ✓ | ✓ |
| Nominations (approve credit) | — | — | **Admin only** |
| Audit / manage users / adjust points | — | — | ✓ |

---

## Core flow

1. Faculty or Admin creates a task (optional template + verification standards).
2. Student submits notes / evidence / rubric as required.
3. Staff approve or reject → approval credits the wallet.
4. Student claims Materials / Opportunities while the window is open and stock remains.
5. Optional: nomination → Admin approve → credit.
6. Leaderboard reflects Elite Point balances.

Client-facing product walkthrough: [USAGE_GUIDE.md](./USAGE_GUIDE.md).

---

## Security (overview)

JWT at the gateway · role checks via trusted identity headers · BCrypt · student-only public signup · demo Admin when no Admin exists.

Rotate demo credentials and SMTP secrets before any shared or production use. Skip **Forgot password** in demos unless real `EMAIL_*` is configured.

---

## License

Proprietary — all rights reserved unless otherwise agreed.

**EliteSchool** — achievement management for schools; rewards that keep every student moving forward.
