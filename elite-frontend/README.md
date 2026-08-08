# EliteSchool Frontend

Angular **22.1** SPA for student achievement management — verified contribution, Elite Points, school rewards, nominations, and support.

| Related docs | |
|--------------|--|
| Product / demo | [USAGE_GUIDE.md](../USAGE_GUIDE.md) |
| Root setup | [README.md](../README.md) |
| Backend | [elite-backend/README.md](../elite-backend/README.md) |

---

## Features

- Role dashboards (Student / Faculty / Admin)
- Tasks + templates + verification standards (evidence, min notes, rubric)
- Store (Materials / Opportunities, claim windows, FCFS)
- Wallet · nominations (staff) · support · contribution portfolio · audit · leaderboard
- Games (recreation only — no Elite Points)
- Branded client UI: Fraunces + Sora, cyan/teal EliteAura theme, landing hero

---

## Tech stack

| Layer | Choice |
|-------|--------|
| Framework | Angular 22.1 (standalone, application / esbuild builder) |
| UI | PrimeNG 22 · `@primeuix/themes` (EliteAura) · PrimeFlex · PrimeIcons |
| Style | SCSS design tokens · dark / light via `[data-theme]` |
| Data | RxJS · functional guards · lazy routes |

Requires **Node.js `^22.22.3`** (or 24 / 26).

---

## Getting started

Backend API gateway must be reachable on **:8080** (see root [README](../README.md) or `demo-native.bat`).

```bash
cd elite-frontend
npm install
npm start
```

App: **http://localhost:4200**

Login uses **username** (demo Admin: **`admin`** / **`Admin@123`**), not email.

### API URL

| Mode | `apiUrl` |
|------|----------|
| `ng serve` (development) | `http://localhost:8080/api` |
| Production / Docker image | `/api` (nginx → `api-gateway:8080`) |

Configured in `src/environments/environment.development.ts` and `environment.prod.ts`.

---

## Main routes

| Route | Access |
|-------|--------|
| `/home`, `/games`, `/login`, `/register`, forgot/reset password | Public / guest |
| `/dashboard`, `/tasks`, `/courses`, `/profile`, `/settings`, `/leaderboard` | Authenticated |
| `/store`, `/wallet` | Student, Admin, Management |
| `/support` | Student, Faculty, Admin, Management |
| `/nominations` | Faculty, Admin, Management (`StaffGuard`) |
| `/admin/users`, `/admin/audit` | Admin, Management |

---

## Structure

```
src/app/
├── core/          # guards, interceptors, models, services, theme
├── features/      # auth, dashboard, tasks, store, wallet, nominations,
│                  # support, admin, profile, courses, leaderboard, games, home
├── layouts/       # main shell (sidebar + header)
└── shared/
src/assets/        # images (brand mark, hero), fonts via styles
src/styles/        # tokens, mixins, forms, auth, components
```

---

## Build

```bash
npm run build
# or: ng build --configuration production
```

Output: `dist/elite-frontend`.

Docker: root `docker-compose.yml` builds this app with `Dockerfile` + `nginx.conf` (proxies `/api` to the gateway).

---

## License

Proprietary — all rights reserved unless otherwise agreed.
