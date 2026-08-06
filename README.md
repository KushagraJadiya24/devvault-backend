# 🔐 DevVault — Open Source Self-Hosted Secrets Manager

> Stop sharing API keys over WhatsApp. DevVault is a self-hosted secrets management platform your team can deploy in one command — full data ownership, no third-party servers.

## 🌐 Live Demo
**Frontend:** https://devvault-frontend.vercel.app

**API Docs (Swagger):** https://devvault-backend-production-d964.up.railway.app/swagger-ui/index.html

---

## 🚀 What is DevVault?

DevVault is an open-source, self-hosted alternative to tools like HashiCorp Vault and Doppler. It solves a problem every engineering team faces — storing and sharing sensitive configuration values like API keys, database passwords, and tokens securely.

**The self-hosted difference:** Your secrets never leave your infrastructure. Clone the repo, deploy with one command, and you own everything.

**How it works:**
1. Deploy DevVault on your own server
2. First person to register becomes the ADMIN automatically
3. ADMIN invites teammates by adding their emails
4. Teammates register and get MEMBER access
5. Store secrets organized by project and environment
6. Import existing `.env` files in one click

---

## ✨ Features

### 🔒 Security
- **AES-256-GCM Encryption** — every secret encrypted at rest before hitting the database
- **JWT Authentication** — stateless auth with 24-hour token expiry
- **Role Based Access Control** — ADMIN manages secrets, MEMBER reads
- **Rate Limiting** — 60 requests/minute per user via Redis atomic counters
- **Team Access Control** — only ADMIN-approved emails can register

### 📁 Organization
- **Projects** — organize secrets by service (Payment Service, Auth Service etc.)
- **Environment Tags** — separate DEVELOPMENT, STAGING, PRODUCTION secrets
- **Secret Versioning** — every update creates a new version, old values never deleted

### ⚡ Performance
- **Redis Caching** — secrets served from cache with 5-minute TTL
- **Cache Invalidation** — automatic eviction on update/delete prevents stale data

### 📋 Auditability
- **Async Audit Logging** — every access logged via Redis queue + background worker
- **Full Audit Trail** — userId, action, secretName, IP address, timestamp

### 🛠 Developer Experience
- **.env Import** — upload a `.env` file, all secrets imported automatically
- **.env Export** — download all secrets as a `.env` file instantly
- **Swagger UI** — interactive API docs at `/swagger-ui/index.html`
- **Next.js Frontend** — clean dashboard for non-technical teammates

---

## 🏗 Architecture

```
Client (Browser / Postman)
          ↓
    JWT Auth Filter
          ↓
    Rate Limit Filter (Redis)
          ↓
    REST Controllers
          ↓
    Service Layer (AES-256-GCM Encryption)
          ↓
    Repository Layer (Spring Data JPA)
          ↓
    PostgreSQL (encrypted secrets at rest)

    Redis → Caching (5min TTL)
          → Rate limiting (per user per minute)
          → Audit event queue (async processing)

    Background Worker → drains audit queue every 2s → PostgreSQL
```

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.1, Java 21 |
| Frontend | Next.js 15, Tailwind CSS, shadcn/ui |
| Database | PostgreSQL 16 |
| Cache / Queue | Redis |
| Security | Spring Security, JWT (JJWT 0.11), BCrypt |
| Encryption | AES-256-GCM |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Deployment | Any Docker-compatible host |

---

## 🚀 Self-Host in One Command

### Prerequisites
- Docker Desktop

### Setup
```bash
git clone https://github.com/KushagraJadiya24/devvault-backend.git
cd devvault-backend
docker compose up --build
```

App runs at `http://localhost:8080`
Swagger UI at `http://localhost:8080/swagger-ui/index.html`

### First time setup
1. Register at the frontend — first user automatically becomes **ADMIN**
2. Go to **Team** tab → add your teammates' emails
3. Share your deployment URL with teammates — they register and get **MEMBER** access
4. Create projects, add secrets, import `.env` files

---

## 📡 API Endpoints

### Auth
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public* | Register (*first user = ADMIN, others need invite) |
| POST | `/api/auth/login` | Public | Login and get JWT token |

### Team Management
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/admin/allowed-emails` | ADMIN | List allowed emails |
| POST | `/api/admin/allowed-emails` | ADMIN | Allow a new email |
| DELETE | `/api/admin/allowed-emails/{id}` | ADMIN | Remove allowed email |

### Projects
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/projects` | ADMIN | Create a project |
| GET | `/api/projects` | ALL | List all projects |
| GET | `/api/projects/{id}` | ALL | Get project by ID |
| PUT | `/api/projects/{id}` | ADMIN | Update project |
| DELETE | `/api/projects/{id}` | ADMIN | Delete project |

### Secrets
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/secrets` | ADMIN | Create a secret |
| GET | `/api/secrets/project/{projectId}` | ALL | List secrets by project |
| GET | `/api/secrets/project/{projectId}/env/{env}` | ALL | Filter by environment |
| GET | `/api/secrets/project/{projectId}/{name}` | ALL | Get decrypted secret |
| PUT | `/api/secrets/project/{projectId}/{name}` | ADMIN | Update (creates new version) |
| DELETE | `/api/secrets/project/{projectId}/{name}` | ADMIN | Delete secret |
| GET | `/api/secrets/project/{projectId}/{name}/history` | ALL | Version history |
| GET | `/api/secrets/project/{projectId}/{name}/version/{v}` | ALL | Get specific version |

### .env
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/env/import/{projectId}/{environment}` | ADMIN | Import .env file |
| GET | `/api/env/export/{projectId}/{environment}` | ALL | Export as .env file |

### Audit
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/audit` | ADMIN | Full audit log |

---

## 🔧 Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `devvault` |
| `DB_USERNAME` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `REDIS_URL` | Redis connection URL | `redis://localhost:6379` |
| `ENCRYPTION_KEY` | AES-256 key (32 chars) | default key |
| `JWT_SECRET` | JWT signing key (32+ chars) | default key |

> ⚠️ Always set `ENCRYPTION_KEY` and `JWT_SECRET` to strong custom values in production.

---

## 👤 Author

**Kushagra Jadiya**
- GitHub: [@KushagraJadiya24](https://github.com/KushagraJadiya24)
- LinkedIn: https://www.linkedin.com/in/kushagra-jadiya/

---

## 📄 License
MIT
