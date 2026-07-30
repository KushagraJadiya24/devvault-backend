# 🔐 DevVault — Secrets Manager for Developer Teams

> Stop sharing API keys over WhatsApp. DevVault gives your team a centralized, encrypted vault for managing secrets across projects and environments.

## 🌐 Live Demo
**API Base URL:** https://devvault-backend-production-d964.up.railway.app

**Swagger UI:** https://devvault-backend-production-d964.up.railway.app/swagger-ui/index.html

---

## 🚀 What is DevVault?

DevVault is a production-grade secrets management REST API built with Spring Boot. It solves a real problem every developer team faces — storing and sharing sensitive configuration values like API keys, database passwords, and tokens securely.

Instead of hardcoding secrets or sharing `.env` files over chat, your team stores secrets in DevVault and fetches them at runtime via a secure, role-protected API.

---

## ✨ Features

### 🔒 Security
- **AES-256-GCM Encryption** — every secret encrypted at rest before hitting the database
- **JWT Authentication** — stateless auth with 24-hour token expiry
- **Role Based Access Control** — ADMIN can manage secrets, MEMBER can only read
- **Rate Limiting** — 60 requests/minute per user via Redis counters

### 📁 Organization
- **Projects** — organize secrets into logical groups (Payment Service, Auth Service etc.)
- **Environment Tags** — separate secrets by DEVELOPMENT, STAGING, PRODUCTION
- **Secret Versioning** — every update creates a new version, old versions never deleted

### ⚡ Performance
- **Redis Caching** — frequently accessed secrets served from cache with 5-minute TTL
- **Cache Invalidation** — automatic eviction on update/delete

### 📋 Auditability
- **Async Audit Logging** — every read/write logged via Redis queue + background worker
- **Full Audit Trail** — userId, action, secretName, IP address, timestamp

### 🛠 Developer Experience
- **.env Import** — upload a `.env` file, all secrets imported automatically
- **.env Export** — download all secrets as a `.env` file instantly
- **Swagger UI** — interactive API documentation at `/swagger-ui/index.html`

---

## 🏗 Architecture

```
Client (Postman / Frontend / CLI)
          ↓
    JWT Auth Filter
          ↓
    Rate Limit Filter
          ↓
    REST Controllers
          ↓
    Service Layer (Business Logic + AES Encryption)
          ↓
    Repository Layer (Spring Data JPA)
          ↓
    PostgreSQL (encrypted secrets at rest)

    Redis (caching + rate limiting + audit queue)
    Async Worker (audit log consumer)
```

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.1, Java 21 |
| Database | PostgreSQL 16 |
| Cache / Queue | Redis |
| Security | Spring Security, JWT (JJWT), BCrypt |
| Encryption | AES-256-GCM |
| Documentation | Springdoc OpenAPI (Swagger UI) |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Deployment | Railway |

---

## 📡 API Endpoints

### Auth
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login and get JWT token |

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
| PUT | `/api/secrets/project/{projectId}/{name}` | ADMIN | Update secret (new version) |
| DELETE | `/api/secrets/project/{projectId}/{name}` | ADMIN | Delete secret |
| GET | `/api/secrets/project/{projectId}/{name}/history` | ALL | Version history |
| GET | `/api/secrets/project/{projectId}/{name}/version/{v}` | ALL | Get specific version |

### .env
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/env/import/{projectId}/{environment}` | ADMIN | Import .env file |
| GET | `/api/env/export/{projectId}/{environment}` | ALL | Export as .env file |

---

## 🚀 Run Locally

### Prerequisites
- Docker Desktop

### One command setup
```bash
git clone https://github.com/KushagraJadiya24/devvault-backend.git
cd devvault-backend
docker compose up --build
```

App runs at `http://localhost:8080`

Swagger UI at `http://localhost:8080/swagger-ui/index.html`

---

## 🔑 Quick Start

**1. Register:**
```bash
curl -X POST https://devvault-backend-production-d964.up.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"you@example.com","password":"yourpassword"}'
```

**2. Login and get token:**
```bash
curl -X POST https://devvault-backend-production-d964.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"you@example.com","password":"yourpassword"}'
```

**3. Create a project:**
```bash
curl -X POST https://devvault-backend-production-d964.up.railway.app/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"My App","description":"Production secrets"}'
```

**4. Store a secret:**
```bash
curl -X POST https://devvault-backend-production-d964.up.railway.app/api/secrets \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"STRIPE_KEY","value":"sk_live_xxx","projectId":1,"environment":"PRODUCTION"}'
```

---

## 👤 Author

**Kushagra Jadiya**
- GitHub: [@KushagraJadiya24](https://github.com/KushagraJadiya24)
- LinkedIn: https://www.linkedin.com/in/kushagra-jadiya/

---

## 📄 License
MIT