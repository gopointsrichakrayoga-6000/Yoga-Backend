# Pranav Dhyan Yoga Ashram — Backend API Server

The official Spring Boot REST API and server-side media processing backend for **Sri Chakra Yoga (`Pranav Dhyan Parampara`)**. This service handles secure JWT authentication, media ingestion, automatic HD photographic/video compression, category management, and data persistence.

## 🛠 Technology Stack

- **Java Version:** 17+
- **Framework:** Spring Boot 3.2.3
- **ORM / Persistence:** Spring Data JPA / Hibernate
- **Database:** Local H2 File Database (default) or PostgreSQL (production via environment overrides)
- **Security:** Spring Security 6 with stateless JWT (`Bearer`) authentication
- **Media Processing:** Thumbnailator (Adaptive HD Photo Compression) & JAVE2 / FFmpeg (Server-side Video Encoding)

---

## 🚀 Getting Started & Local Setup

### 1. Prerequisites
- Java JDK 17 or higher (`java -version`)
- Maven 3.8+ (`mvn -version`) or use the included Maven wrapper

### 2. Environment Configuration
Copy the sample environment template if you wish to override default local settings:
```bash
cp .env.example .env
```

#### Key Environment Variables
| Variable | Description | Default Value |
|---|---|---|
| `PORT` | HTTP server listening port | `8081` |
| `DB_URL` | JDBC Database Connection URL | `jdbc:h2:file:./data/ashram_media_db` |
| `DB_DRIVER` | Database JDBC Driver Class | `org.h2.Driver` |
| `DB_USERNAME` | Database username | `sa` |
| `DB_PASSWORD` | Database password | *(empty)* |
| `JWT_SECRET` | Secret key for signing JWT tokens | `PranavDhyanAshramSacredSecretKey...` |
| `APP_CORS_ALLOWED_ORIGINS`| Allowed frontend origins | `http://localhost:5173,http://localhost:5174` |
| `SEED_DEMO_DATA` | Whether to seed baseline archive & demo accounts | `false` (`true` recommended for local dev) |

### 3. Running the Server Locally

To run the backend locally with demo account seeding enabled (`admin@srichakrayoga.org` / `Password123!`):

#### On Windows (PowerShell):
```powershell
$env:SEED_DEMO_DATA="true"; mvn spring-boot:run
```

#### On Linux / macOS (Bash):
```bash
SEED_DEMO_DATA=true mvn spring-boot:run
```

The API server will boot up and listen on **`http://localhost:8081`**.

---

## 📂 Storage Architecture

By default, all uploaded media items are compressed and stored locally outside of Git tracking:
- `uploads/photos/` — High-definition compressed master images (`1920px` longest side at `0.82` JPEG quality)
- `uploads/thumbnails/` — Crisp masonry grid thumbnails (`1000px` longest side at `0.78` JPEG quality)
- `uploads/videos/` — Re-encoded H.264 / AAC MP4 video streams

> [!NOTE]
> The `uploads/` directory and local database files (`data/*.mv.db`) are ignored via `.gitignore` to keep the Git repository lightweight.

---

## 🔐 API Overview

- `POST /api/auth/login` — Authenticate and receive JWT token
- `POST /api/auth/register` — Register a new seeker account
- `GET /api/media` — Fetch paginated media records (supports `?categoryId=...&type=PHOTO|VIDEO`)
- `GET /api/categories` — List all ashram media categories with item counts
- `POST /api/admin/media/upload-file` — Upload raw photo/video file for server-side HD compression (`Admin Only`)
- `POST /api/admin/media` — Create a verified media record after upload (`Admin Only`)
