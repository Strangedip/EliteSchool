# EliteSchool — Usage Guide

For schools, teachers, and **client demos**: what EliteSchool is for, who uses it, and how the main flows work.

> **Install / run:** [README.md](./README.md) · helper: `demo-native.bat`

---

## Why EliteSchool exists

Schools need a clear way to **manage student achievement** — who contributes, what was verified, and how learners grow over time.

Verified school work earns **Elite Points**, a trusted measure of effort. Those points can unlock **school rewards** (materials, access, opportunities) so students who need support are not left behind. Rewards are a support layer — the core product is achievement.

**Flow:** contribute → faculty verifies → earn Elite Points → claim school rewards when offered.

- Staff credit points only when the school decides (Wallet or Admin-approved nomination).
- **Support is never for asking for points.**
- **Games are recreation only** — they do not earn points.

---

## Who uses it

| Role | What they do | How they get an account |
|------|----------------|-------------------------|
| **Student** | Tasks, wallet, store, profile, support | Self-register or Admin creates |
| **Faculty** | Tasks, verification, nominations | Admin creates (Manage Users) |
| **Admin / Management** | Users, store, courses, audit, adjustments | Fresh install seeds demo Admin; more staff via Manage Users |

**Visitors** (not logged in): Home, Docs, Games, Login, Register, Forgot password.

In-app product guide: open **Docs** from the home page (`/docs`) for an overview of roles, modules, and how EliteSchool works.

---

## Demo setup (quick)

Prefer **native** on 16GB machines ([README](./README.md)). Docker Compose is optional.

### Checklist

1. PostgreSQL running with database **`EliteSchool`**
2. **Consul** installed and on `PATH`
3. JDK **25**, Node **22.22.3+**
4. From repo root: `demo-native.bat`  
   (or `elite-backend\build-all.bat` → `start-all.bat`, then `elite-frontend\npm start`)
5. Open **http://localhost:4200**
6. Sign in as Admin: username **`admin`** / password **`Admin@123`** (**username**, not email)
7. Change the demo password after first login
8. **Manage Users** → create Faculty (and students if needed)
9. Register or create a **Student**

Skip **Forgot password** unless SMTP is configured in `.env`: set `EMAIL_USERNAME` / `EMAIL_PASSWORD` (Gmail App Password) and usually `EMAIL_FROM` to the same address. With valid credentials, reset emails send immediately.

**Tip:** When creating users in Manage Users, password must be at least **8 characters**. Mobile is optional; if filled, use a valid number (e.g. `9876543210` or `+919876543210`).

---

## Roles and journeys

### Student

Tasks (submit with notes / evidence / rubric when required) → Wallet → Store (Materials / Opportunities, first-come stock) → Profile → Contribution → Support (concerns only).

No Nominations, Audit, or Manage Users.

### Faculty

Create tasks and templates (optional evidence, min notes, rubric) → verify submissions → optionally **Nominate** a contribution.

Cannot credit the wallet directly; **Admin** approves nominations.

### Admin / Management

Manage Users · Store (Materials & Opportunities — brief/checklist for Opportunities, claim windows, stock) · Nominations (**Admin only** approves credit) · Wallet adjustments · Courses · Audit · Support queue.

Management can nominate and run most operations; **nomination wallet credit is Admin-only**.

---

## Nominations

Staff-only path for recognizing contribution outside a normal task.

1. Faculty (or Management/Admin) nominates a student with suggested points and a reason.
2. **Admin** Approves (credits Elite Points) or Rejects.

Students never see this as a self-serve “request points” tool. Support is not a points channel.

---

## Suggested 10–15 minute client demo

1. **Visitor (Home)** — brand story on the landing page; open **Docs** for the product guide; open Games briefly (recreation only, no points).
2. **Admin** (`admin` / `Admin@123`) — Manage Users → create Faculty; Store → add a **Material** (points) and an **Opportunity** (brief, checklist, window, stock).
3. **Faculty** — create a task with verification standards; approve a student submission; optionally nominate.
4. **Student** — submit work → get approved → Wallet → claim store item → Profile → Contribution timeline.
5. **Admin** — approve nomination if used; show Claimed / Expired store state; remind that Support is for concerns only.

---

## Menu by role

| Menu | Student | Faculty | Admin / Management |
|------|:-------:|:-------:|:------------------:|
| Dashboard | ✓ | ✓ | ✓ |
| Tasks | ✓ | ✓ | ✓ |
| Courses | ✓ | ✓ | ✓ |
| Store | ✓ | — | ✓ |
| Wallet | ✓ | — | ✓ |
| Support | ✓ | ✓ | ✓ |
| Nominations | — | ✓ | ✓ |
| Leaderboard | ✓ | ✓ | ✓ |
| Profile | ✓ | ✓ | ✓ |
| Games | ✓ | ✓ | ✓ |
| Audit | — | — | ✓ |
| Manage Users | — | — | ✓ |
| Settings | ✓ | ✓ | ✓ |

---

## Good practices

- Create staff accounts via Admin only.
- Use real contribution tasks; give clear reject feedback; set verification standards when needed.
- Store: FCFS via stock (no waitlist); Opportunities need a brief.
- Credits: Wallet or Admin-approved nomination — never Support.
- Games do not score Elite Points.

---

**EliteSchool** — achievement management for schools; rewards that keep every student moving forward.
