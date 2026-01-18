# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Darkhold is a self-hosted real-time multiplayer quiz platform (Kahoot alternative) built with Spring Boot 4.0 and Java 21+. It uses Thymeleaf templates with Bootstrap 5 for the frontend and WebSocket/STOMP for live game updates.

## Common Commands

```bash
# Build
./gradlew clean build

# Run locally (H2 in-memory database, port 8181)
./gradlew bootRun
# Login: admin@admin.com / admin

# Run with PostgreSQL
./gradlew bootRun --args='--spring.profiles.active=postgres'

# Test
./gradlew test
./gradlew test --tests "TestClassName"    # Single test class

# Code quality checks
./gradlew check                            # All checks (checkstyle, PMD, JaCoCo)
./gradlew checkstyleMain                   # Checkstyle only
./gradlew pmdMain                          # PMD only
./gradlew jacocoTestReport                 # Coverage report

# Docker
docker-compose up -d                       # Production (PostgreSQL)
docker-compose -f docker-compose.h2.yml up -d  # Development (H2)
```

## Architecture

### Technology Stack
- **Backend**: Spring Boot 4.0, Spring Security 6, Spring Data JPA
- **Database**: H2 (dev), PostgreSQL (prod) with Flyway migrations
- **Frontend**: Thymeleaf templates, Bootstrap 5, vanilla JavaScript
- **Real-time**: WebSocket with STOMP protocol
- **Build**: Gradle 9.x with Error Prone, Checkstyle 12.3.0, PMD 7.19.0, JaCoCo

### Source Structure (`src/main/java/com/quiz/darkhold/`)

| Package | Purpose |
|---------|---------|
| `init/` | Spring configuration (SecurityConfig, WebSocketConfig, GlobalExceptionHandler) |
| `user/` | User management, authentication, role-based access |
| `game/` | Game session management, live gameplay via WebSocket |
| `challenge/` | Quiz creation and management (Excel upload support) |
| `preview/` | Challenge preview functionality |
| `practice/` | Practice mode for individual play |
| `analytics/` | Game statistics and history |
| `score/` | Scoring system with time-based calculations |
| `options/` | User settings and preferences |
| `home/` | Landing page and navigation |

### Frontend Structure (`src/main/resources/`)
- `templates/` - Thymeleaf templates (index, login, game, challenge management)
- `static/scripts/` - JavaScript modules (game-ws.js handles WebSocket game logic)
- `static/styles/` - CSS including dark mode support
- `static/sounds/` - Audio effects and background music
- `db/migration/` - Flyway migration scripts (V1-V9)

### Key Configuration
- Server port: `8181`
- Game timer: `20` seconds default
- PIN length: `5` digits
- Minimum test coverage: `35%` (JaCoCo)

## Database

Development uses H2 with `schema.sql` and `data.sql` for initialization. Production uses PostgreSQL with Flyway migrations in `src/main/resources/db/migration/`.

Environment variables for PostgreSQL:
- `DATABASE_URL` - JDBC URL
- `DATABASE_USER` - Username
- `DATABASE_PASSWORD` - Password

## Code Quality

Pre-commit hooks enforce checks. Reports generated at:
- Checkstyle: `build/reports/checkstyle/main.html`
- PMD: `build/reports/pmd/main.html`
- Coverage: `build/reports/jacoco/test/html/index.html`

Configuration files:
- `config/checkstyle/checkstyle.xml`
- `config/pmd/ruleset.xml`
- `gradle/staticCodeAnalysis.gradle`
