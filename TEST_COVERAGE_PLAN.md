# Test Coverage Improvement Plan

*Updated: 2026-02-18 (from actual JaCoCo report)*

## Current State

| Metric | Value |
|--------|-------|
| **Instruction Coverage** | 51% (6,989 / 13,481) |
| **Branch Coverage** | 34% (287 / 842) |
| **Test Files** | 38 |
| **Source Files** | 101 |
| **JaCoCo Minimum** | 35% (current threshold) |
| **Target** | 85% instructions, 70% branches |

---

## Per-Package Coverage (Actual JaCoCo Data)

### Critical Gaps (Below 25%)

| Package | Inst. Cov. | Branch Cov. | Priority |
|---------|:----------:|:-----------:|:--------:|
| analytics.service | 4% | 0% | P0 |
| team.service | 7% | 0% | P0 |
| admin.controller | 10% | n/a | P0 |
| practice.service | 11% | n/a | P0 |
| challenge.service | 15% | 9% | P0 |
| challenge.controller | 18% | 4% | P0 |
| user.service | 24% | 11% | P1 |

### Low Coverage (25-50%)

| Package | Inst. Cov. | Branch Cov. | Priority |
|---------|:----------:|:-----------:|:--------:|
| options.model | 25% | n/a | P1 |
| preview.entity | 26% | 20% | P1 |
| game.service | 36% | 67% | P1 |
| preview.repository | 37% | 20% | P1 |
| challenge.exception | 47% | n/a | P2 |

### Medium Coverage (50-70%)

| Package | Inst. Cov. | Branch Cov. | Priority |
|---------|:----------:|:-----------:|:--------:|
| options.controller | 52% | 12% | P2 |
| game.controller | 53% | 45% | P2 |
| game.entity | 56% | 0% | P2 |
| account.controller | 60% | n/a | P2 |
| init | 60% | 6% | P2 |

### Adequate Coverage (70%+)

| Package | Inst. Cov. | Branch Cov. |
|---------|:----------:|:-----------:|
| home.service | 63% | 40% |
| user.entity | 64% | 0% |
| game.model | 66% | 56% |
| home.model | 74% | n/a |
| preview.service | 77% | 37% |
| options.service | 77% | n/a |
| preview.model | 79% | n/a |
| user.validator | 91% | 80% |
| challenge.entity | 92% | n/a |
| score.controller | 94% | 100% |
| analytics.controller | 98% | 100% |
| home.controller | 99% | 90% |

### Full Coverage (100%)

user.controller, challenge.dto, analytics.entity, preview.controller, team.model, team.dto, team.controller, team.entity, util, DarkholdApplication, user.exception

---

## Goal: 51% to 85% in 5 Milestones

Each milestone is self-contained and raises the coverage floor progressively. Milestones are labeled TC-1 through TC-5 and tracked in [MILESTONES.md](MILESTONES.md).

### Milestone TC-1: Security & Infrastructure Tests (51% -> 58%)

**Focus**: Security-critical code that is almost entirely untested.

| Target | Current | Goal | Est. Tests |
|--------|:-------:|:----:|:----------:|
| SecurityConfig | 60% inst / 6% branch | 80% / 60% | 15 |
| RateLimitingService | (within init 60%) | 90% / 80% | 12 |
| WebSocketConfig | (within init 60%) | 75% / 50% | 8 |
| FileUploadUtil | (within init 60%) | 85% / 70% | 10 |
| GlobalExceptionHandler | (within init 60%) | 90% / 80% | 8 |

**Test types**: Unit tests with mocked Spring context, `@WebMvcTest` for security chain.

**Key test scenarios**:
- Authentication flows (login success/failure, remember-me, logout)
- Authorization rules (admin-only, game-manager-only, public endpoints)
- CSRF token validation (form and WebSocket)
- Rate limiting (threshold, blocking, reset)
- File upload validation (type, size, malicious content)
- WebSocket handshake and STOMP endpoint registration
- Exception handler response codes and bodies

**JaCoCo threshold raise**: 35% -> 45%

---

### Milestone TC-2: Core Service Layer (58% -> 67%)

**Focus**: Business logic services -- the biggest gap and highest-value tests.

| Target | Current | Goal | Est. Tests |
|--------|:-------:|:----:|:----------:|
| analytics.service (ResultService) | 4% | 65% | 18 |
| team.service (TeamService) | 7% | 65% | 20 |
| practice.service (PracticeService) | 11% | 65% | 15 |
| challenge.service (ChallengeService) | 15% | 55% | 20 |
| user.service (UserService, SecurityService) | 24% | 65% | 15 |
| game.service (GameService, Cleanup) | 36% | 65% | 12 |

**Test types**: Unit tests with `@ExtendWith(MockitoExtension.class)`, mocked repositories.

**Key test scenarios**:
- ResultService: save game results, calculate stats, CSV export formatting
- TeamService: balanced/random/manual assignment, score aggregation, team creation
- PracticeService: session creation, solo gameplay flow, score tracking
- ChallengeService: CRUD, Excel/JSON import parsing, duplicate, export, validation
- UserService: registration, password change, role management, email uniqueness
- GameService: game lifecycle (create/start/pause/resume/end), question advancement
- GameCleanupScheduler: expired game cleanup, edge cases (active games not cleaned)

**JaCoCo threshold raise**: 45% -> 55%

---

### Milestone TC-3: Controller Integration Tests (67% -> 73%)

**Focus**: Undertested controllers with `MockMvc` integration tests.

| Target | Current | Goal | Est. Tests |
|--------|:-------:|:----:|:----------:|
| admin.controller | 10% | 75% | 10 |
| challenge.controller | 18% | 70% | 15 |
| options.controller | 52% | 80% | 8 |
| game.controller | 53% | 75% | 10 |
| account.controller | 60% | 85% | 8 |

**Test types**: `@WebMvcTest` with `@MockBean` for service dependencies.

**Key test scenarios**:
- AdminController: user management CRUD, role assignment, admin-only access
- ChallengeController: create, edit, delete, duplicate, import/export endpoints
- OptionsController: settings page, preference updates
- GameController: publish, join, gameplay WebSocket endpoints, question flow
- AccountController: profile view, profile update

**JaCoCo threshold raise**: 55% -> 62%

---

### Milestone TC-4: Data Layer & Model Tests (73% -> 78%)

**Focus**: Entities, repositories, and models with low coverage.

| Target | Current | Goal | Est. Tests |
|--------|:-------:|:----:|:----------:|
| preview.entity | 26% | 75% | 12 |
| preview.repository | 37% | 70% | 10 |
| options.model | 25% | 80% | 8 |
| game.entity | 56% | 85% | 8 |
| user.entity | 64% | 90% | 8 |
| home.model | 74% | 95% | 5 |

**Test types**: Unit tests for models/entities; `@DataJpaTest` for repository integration tests.

**Key test scenarios**:
- CurrentGameSession: state transitions, JSON serialization, field validation
- CurrentGame: CRUD operations, PIN-scoped queries, concurrent access
- Repository custom queries: find by status, find by moderator, date filtering
- Entity validation: required fields, constraints, relationship cascades
- Model builders and equality contracts

**JaCoCo threshold raise**: 62% -> 68%

---

### Milestone TC-5: Branch Coverage & End-to-End (78% -> 85%)

**Focus**: Branch coverage (34% -> 70%), error paths, integration tests.

| Target | Current Goal | Est. Tests |
|--------|:-----------:|:----------:|
| Branch coverage across all packages | 34% -> 70% | 30 |
| Full game lifecycle integration test | New | 5 |
| WebSocket integration tests | New | 8 |
| Error/edge case paths | Mixed | 20 |

**Test types**: `@SpringBootTest` integration tests, parameterized tests for branches.

**Key test scenarios**:
- Full game lifecycle: create challenge -> publish -> join -> play all questions -> scoreboard -> end
- WebSocket: STOMP connect, subscribe, send answer, receive scoreboard update
- Error paths: invalid PIN, expired game, duplicate nickname, concurrent answer
- Boundary conditions: 0 participants, max participants, timer expiry, no correct answer
- Null/empty inputs across all service methods
- Concurrent game isolation (two games running simultaneously)

**JaCoCo threshold raise**: 68% -> 75%

---

## Test Configuration

Create `src/test/resources/application-test.properties`:
```properties
darkhold.game.timer-seconds=5
darkhold.game.pin-length=5
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.com.quiz.darkhold=WARN
```

---

## Monitoring Progress

```bash
# Run coverage report
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html

# Verify threshold
./gradlew jacocoTestCoverageVerification
```

---

## Summary

| Milestone | Focus | Coverage Target | Threshold Raise |
|-----------|-------|:---------------:|:---------------:|
| TC-1 | Security & Infrastructure | 58% | 35% -> 45% |
| TC-2 | Core Service Layer | 67% | 45% -> 55% |
| TC-3 | Controller Integration | 73% | 55% -> 62% |
| TC-4 | Data Layer & Models | 78% | 62% -> 68% |
| TC-5 | Branches & End-to-End | 85% | 68% -> 75% |
