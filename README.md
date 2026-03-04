# URL Shortener Service

A scalable URL shortening service built with Java and Spring Boot, inspired by bit.ly.

## 🚀 Live Demo
**[https://url-shortener-px0g.onrender.com](https://url-shortener-px0g.onrender.com)**

## Tech Stack
- **Backend:** Java 17, Spring Boot 3.5
- **Database:** PostgreSQL with Hibernate ORM
- **Caching:** Redis with cache-aside pattern (24hr TTL)
- **DevOps:** Docker, Docker Compose
- **Deployment:** Render.com

## Features
- Generate unique 6-character short codes for any URL
- Redirect short URLs to original URLs (HTTP 302)
- Track click counts per short URL
- Redis caching for sub-millisecond redirects on repeated requests
- Collision-safe short code generation
- Clean web UI — paste a URL and get a short link instantly

## API Endpoints

### Shorten a URL
POST /api/shorten

### Redirect to Original URL
GET /api/{shortCode} — returns HTTP 302 redirect

## Architecture
```
Client → UrlController → UrlService → Redis Cache (HIT)
                                         ↓ (MISS)
                                   UrlRepository → PostgreSQL → cache in Redis
```

## How to Run

### Option 1 — Docker (Recommended)
```bash
git clone https://github.com/aarthi-reddy/url-shortener.git
cd url-shortener
docker-compose up --build
```
No need to install MySQL or Redis locally.

### Option 2 — Run Locally
Prerequisites: Java 17, MySQL 8+, Maven
```bash
git clone https://github.com/aarthi-reddy/url-shortener.git
cd url-shortener
mysql -u root -p -e "CREATE DATABASE urlshortener;"
./mvnw spring-boot:run
```

## Upcoming
- Custom alias support
- URL expiration
- Analytics dashboard
