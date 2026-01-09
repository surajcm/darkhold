# Darkhold - How-To Guide

A comprehensive guide for running, developing, and deploying Darkhold - a self-hosted quiz platform.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Development Setup](#development-setup)
3. [Running the Application](#running-the-application)
4. [Database Configuration](#database-configuration)
5. [Configuration Options](#configuration-options)
6. [Docker Deployment](#docker-deployment)
7. [Creating Quizzes](#creating-quizzes)
8. [Running a Game](#running-a-game)
9. [Troubleshooting](#troubleshooting)

---

## Quick Start

### Prerequisites

- Java 21+ (JDK)
- Gradle 9.x (or use included wrapper)
- Docker & Docker Compose (for production deployment)

### Run Locally (Development)

```bash
# Clone the repository
git clone https://github.com/surajcm/darkhold.git
cd darkhold

# Run with H2 in-memory database (default)
./gradlew bootRun

# Access the application
open http://localhost:8181
```

Default admin credentials:
- **Email**: admin@admin.com
- **Password**: admin

---

## Development Setup

### 1. Clone and Build

```bash
git clone https://github.com/surajcm/darkhold.git
cd darkhold

# Build the project
./gradlew clean build

# Run tests only
./gradlew test

# Run with code coverage report
./gradlew test jacocoTestReport
# Report at: build/reports/jacoco/test/html/index.html
```

### 2. IDE Setup

**IntelliJ IDEA:**
1. Open the project folder
2. Import as Gradle project
3. Set Project SDK to Java 21+
4. Enable annotation processing (for Lombok if used)

**VS Code:**
1. Install "Extension Pack for Java"
2. Open the project folder
3. Gradle extension will auto-detect the project

### 3. Code Quality Tools

The project uses:
- **Checkstyle** - Code style enforcement
- **PMD** - Static code analysis
- **JaCoCo** - Code coverage (minimum 71%)

```bash
# Run all checks
./gradlew check

# View reports
open build/reports/checkstyle/main.html
open build/reports/pmd/main.html
open build/reports/jacoco/test/html/index.html
```

---

## Running the Application

### Option 1: Development Mode (H2 Database)

```bash
./gradlew bootRun
```

- Uses in-memory H2 database
- Data is reset on restart
- H2 Console available at: http://localhost:8181/h2-console
  - JDBC URL: `jdbc:h2:mem:darkhold`
  - Username: `sa`
  - Password: (empty)

### Option 2: Production Mode (PostgreSQL)

```bash
# Set environment variables
export SPRING_PROFILES_ACTIVE=postgres
export DATABASE_URL=jdbc:postgresql://localhost:5432/darkhold
export DATABASE_USER=darkhold
export DATABASE_PASSWORD=your_secure_password

# Run
./gradlew bootRun
```

Or use the docker-compose setup (recommended for production).

---

## Database Configuration

### H2 (Default - Development)

No configuration needed. Uses in-memory database by default.

To persist H2 data to file:
```properties
# In application.properties
spring.datasource.url=jdbc:h2:file:/path/to/db/darkhold
```

### PostgreSQL (Production)

#### 1. Create Database

```sql
CREATE DATABASE darkhold;
CREATE USER darkhold WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE darkhold TO darkhold;
```

#### 2. Configure Application

Create or use `application-postgres.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/darkhold
spring.datasource.username=darkhold
spring.datasource.password=your_password
```

Or use environment variables:
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/darkhold
export DATABASE_USER=darkhold
export DATABASE_PASSWORD=your_password
```

#### 3. Run with PostgreSQL Profile

```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

### Flyway Migrations

When using PostgreSQL, Flyway automatically runs migrations:
- `V1__initial_schema.sql` - Creates all tables
- `V2__seed_data.sql` - Seeds roles and default admin user

To run migrations manually:
```bash
./gradlew flywayMigrate -Dflyway.url=jdbc:postgresql://localhost:5432/darkhold \
  -Dflyway.user=darkhold -Dflyway.password=your_password
```

---

## Configuration Options

### Game Settings

In `application.properties`:

```properties
# Timer duration for answering questions (seconds)
darkhold.game.timer-seconds=20

# Length of generated game PIN
darkhold.game.pin-length=5
```

### Server Settings

```properties
# Server port
server.port=8181

# Enable/disable H2 console
spring.h2.console.enabled=true
```

### All Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8181 | HTTP server port |
| `darkhold.game.timer-seconds` | 20 | Question answer time |
| `darkhold.game.pin-length` | 5 | Game PIN digit count |
| `spring.h2.console.enabled` | true | H2 web console |
| `spring.profiles.active` | (none) | Active profile (postgres) |

---

## Docker Deployment

### Option 1: Simple Deployment (H2)

```bash
# Use the H2 compose file
docker-compose -f docker-compose.h2.yml up -d

# Check logs
docker-compose -f docker-compose.h2.yml logs -f
```

### Option 2: Production Deployment (PostgreSQL)

```bash
# Start PostgreSQL and Darkhold
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f darkhold
```

### Docker Compose Files

- `docker-compose.yml` - Production setup with PostgreSQL
- `docker-compose.h2.yml` - Simple H2 setup for testing

### Building Docker Image Locally

```bash
# Build the image
docker build -t darkhold:local .

# Run with H2
docker run -p 8181:8181 darkhold:local

# Run with PostgreSQL
docker run -p 8181:8181 \
  -e SPRING_PROFILES_ACTIVE=postgres \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/darkhold \
  -e DATABASE_USER=darkhold \
  -e DATABASE_PASSWORD=password \
  darkhold:local
```

---

## Creating Quizzes

### Step 1: Log In

1. Navigate to http://localhost:8181
2. Click "Login"
3. Use admin credentials or create a new account

### Step 2: Create Challenge via Excel

1. Go to **Options** > **Upload Challenge**
2. Prepare an Excel file (.xlsx) with the following format:

| Question | Answer1 | Answer2 | Answer3 | Answer4 | CorrectOptions |
|----------|---------|---------|---------|---------|----------------|
| What is 2+2? | 3 | 4 | 5 | 6 | 2 |
| Capital of France? | London | Paris | Berlin | Rome | 2 |

- **CorrectOptions**: Comma-separated indices (1-4) for correct answers
- Example: `1,3` means Answer1 and Answer3 are correct

3. Enter a title and description
4. Click Upload

### Step 3: View Your Challenges

1. Go to **Options** > **View Challenges**
2. See all challenges you've created
3. Click on a challenge to preview or start a game

---

## Running a Game

### As a Moderator (Host)

1. Log in to your account
2. Go to **Options** > **View Challenges**
3. Select a challenge
4. Click **Publish** to generate a game PIN
5. Share the PIN with participants
6. Wait for participants to join
7. Click **Start Game** when ready

### As a Participant (Player)

1. Go to http://localhost:8181
2. Enter the game PIN provided by the host
3. Enter your nickname
4. Wait for the game to start
5. Answer questions within the time limit
6. View your score on the leaderboard

### Game Flow

```
[Host creates game] → [Participants join with PIN] → [Host starts game]
       ↓                                                    ↓
[Question displayed] → [Participants answer] → [Scores shown]
       ↓                                                    ↓
[Next question...] → [...] → [Final scores displayed]
```

---

## Troubleshooting

### Common Issues

#### Application won't start

```bash
# Check if port 8181 is in use
lsof -i :8181

# Try a different port
./gradlew bootRun --args='--server.port=8080'
```

#### Database connection failed

```bash
# PostgreSQL: Check if database is running
pg_isready -h localhost -p 5432

# Check connection
psql -h localhost -U darkhold -d darkhold
```

#### Build fails

```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies

# Check Java version
java -version  # Should be 21+
```

#### Tests fail

```bash
# Run tests with more output
./gradlew test --info

# Run a specific test
./gradlew test --tests "GameControllerTest"
```

### Logs

```bash
# Development - logs go to console

# Production - check Docker logs
docker-compose logs -f darkhold

# Or inside container
docker exec -it darkhold cat /tmp/darkhold.log
```

### Reset Database

**H2 (Development):**
Just restart the application - H2 is in-memory.

**PostgreSQL:**
```sql
-- Connect to PostgreSQL
DROP DATABASE darkhold;
CREATE DATABASE darkhold;
-- Restart application to run migrations
```

---

## API Endpoints

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Home page |
| POST | `/enterGame` | Enter game with PIN |
| POST | `/joinGame` | Join game with nickname |

### Authenticated Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/options` | Options menu |
| POST | `/upload_challenge` | Upload new challenge |
| DELETE | `/delete_challenge` | Delete a challenge |
| POST | `/preview` | Preview a challenge |
| POST | `/publish` | Start a game |

### WebSocket Endpoints

| Destination | Description |
|-------------|-------------|
| `/app/game` | Join game session |
| `/app/start` | Start game trigger |
| `/app/question` | Fetch current question |
| `/app/scores` | Fetch current scores |
| `/topic/game` | Game updates broadcast |

---

## Contributing

See [CONTRIBUTING.md](https://github.com/surajcm/darkhold/wiki) for guidelines.

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure tests pass (`./gradlew check`)
5. Submit a pull request

---

## Support

- **Issues**: https://github.com/surajcm/darkhold/issues
- **Wiki**: https://github.com/surajcm/darkhold/wiki
