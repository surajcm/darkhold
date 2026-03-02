# Darkhold - Project Goals

A self-hosted, privacy-first real-time quiz platform. This document tracks current state, identified limitations, and strategic goals.

---

## Current State (as of 2026-02-18)

### Phase 1: Kahoot Feature Parity - ACHIEVED

Darkhold has reached and exceeded basic feature parity with Kahoot's core quiz functionality through 11 completed milestones.

| Feature | Status | Milestone |
|---------|:------:|:---------:|
| User Registration & Login (role-based) | Done | 1 |
| PostgreSQL + Flyway migrations | Done | 1 |
| Docker deployment (H2 + PostgreSQL) | Done | 1 |
| Input validation & error handling | Done | 1 |
| Multiple Choice questions | Done | 2 |
| True/False questions | Done | 2 |
| Type Answer (fuzzy matching) | Done | 2 |
| Poll questions | Done | 2 |
| Configurable points & time per question | Done | 2 |
| In-browser question editor (CRUD) | Done | 3 |
| Drag-and-drop question reordering | Done | 3 |
| Challenge duplicate/clone | Done | 3 |
| JSON/CSV import & export | Done | 3 |
| Excel import (backward compatible) | Done | 3 |
| Image URLs on questions | Done | 4 |
| YouTube video embeds | Done | 4 |
| Streak bonuses (1x-4x multipliers) | Done | 5 |
| Score change animations & rank deltas | Done | 5 |
| Pause/Resume/Skip/End game controls | Done | 5 |
| Kick player from lobby | Done | 5 |
| Animated podium (gold/silver/bronze) | Done | 5 |
| Multiple concurrent games (PIN-scoped) | Done | 6 |
| Game timeout & auto-cleanup | Done | 6 |
| Game history & past results | Done | 7 |
| Per-question analytics (success rate, difficulty) | Done | 7 |
| CSV export of results | Done | 7 |
| Practice mode (self-paced solo) | Done | 8 |
| Game mode selection UI | Done | 8 |
| Team mode (balanced/random/manual) | Done | 9 |
| Team leaderboards & podium | Done | 9 |
| Dark/Light theme with persistence | Done | 10 |
| Sound effects & animations | Done | 10 |
| WCAG 2.1 AA accessibility | Done | 10 |
| PWA support | Done | 10 |
| i18n: English, Spanish, French, German | Done | 11 |
| CSRF protection (including WebSocket) | Done | 11+ |
| Rate limiting on PIN entry | Done | 11+ |

### Architecture

- **Backend**: Spring Boot 4.0, Spring Security 6, Spring Data JPA, Java 21+
- **Database**: H2 (dev), PostgreSQL 16 (prod), 11 Flyway migrations, 16 tables
- **Frontend**: Thymeleaf + Bootstrap 5.3 + vanilla JS (ES6+ module pattern)
- **Real-time**: WebSocket with STOMP over SockJS
- **Build**: Gradle 9.2 with Error Prone, Checkstyle 12.3, PMD 7.19, JaCoCo
- **Deploy**: Docker (Temurin JDK 25), GitHub Actions CI
- **Codebase**: 101 source files, 38 test files, 15 packages

---

## Phase 2: Overcoming Platform Limitations

### Goal: Transform from a Kahoot alternative into an enterprise-ready, AI-powered, self-hosted learning platform

The following 12 limitations have been identified through competitive analysis against Kahoot, Wayground (Quizizz), Mentimeter, Slido, and the broader $29B+ game-based learning market. Each limitation has a corresponding milestone in [MILESTONES.md](MILESTONES.md).

### Limitations & Strategic Goals

#### L1. No REST API (Milestone 13)
**Current state**: All interactions are server-rendered Thymeleaf pages. No programmatic access.
**Goal**: Expose a documented REST API (OpenAPI/Swagger) for challenges, games, users, and analytics. Add JWT-based API authentication. This is the foundation for mobile apps, third-party integrations, LMS connectivity, and the plugin ecosystem.
**Why it matters**: Without an API, Darkhold is a closed application. With one, it becomes a platform.

#### L2. No AI Features (Milestone 14)
**Current state**: All quizzes are created manually (30+ minutes per quiz).
**Goal**: Generate quizzes from pasted text, uploaded PDFs, or URLs using LLM integration. Support both cloud APIs (OpenAI, Anthropic) and local models (Ollama) for air-gapped deployments.
**Why it matters**: AI quiz generation is the #1 feature race in 2025-2026. Every major competitor has shipped this. Without it, Darkhold is perceived as a generation behind. With it -- as a self-hosted platform with local AI -- it is unique in the market.

#### L3. No SSO / Enterprise Identity (Milestone 15)
**Current state**: Own user management with BCrypt passwords only.
**Goal**: Support enterprise identity providers via OAuth2/OIDC (Okta, Azure AD, Google Workspace, Keycloak) and SAML 2.0. Leverage Spring Security 6's built-in OAuth2 client support.
**Why it matters**: Enterprise IT departments reject tools without SSO. This is a hard gate to institutional adoption.

#### L4. No Async / Homework Mode (Milestone 16)
**Current state**: All play is synchronous (live games) except basic practice mode.
**Goal**: Allow hosts to publish challenges with deadlines. Participants complete at their own pace within the window. Results aggregate after deadline. Support configurable attempts and availability windows.
**Why it matters**: Doubles the use-case surface area. Teachers assign homework; trainers assign pre/post assessments; event organizers run pre-event trivia. Live games are episodic; async drives daily active usage.

#### L5. No Advanced Analytics (Milestone 17)
**Current state**: Basic per-game results with CSV export. No visualizations or trend analysis.
**Goal**: Build rich analytics dashboards with Chart.js: learning gap analysis, question difficulty curves, participant progress over time, cohort comparison, engagement heatmaps. Add PDF report generation and email delivery.
**Why it matters**: Analytics is a premium differentiator across all competitors. Users who see insights keep coming back.

#### L6. No LMS Integration (Milestone 18)
**Current state**: No SCORM, LTI, or xAPI support.
**Goal**: Export results in SCORM 1.2/2004 format. Implement LTI 1.3 provider so Darkhold games launch from Moodle, Canvas, Blackboard, Google Classroom with automatic grade passback.
**Why it matters**: Unlocks the two largest market segments (education 44.9% + enterprise training 20.3% CAGR). Without LMS integration, institutional procurement is blocked.

#### L7. No White-Label / Branding (Milestone 19)
**Current state**: All deployments show "Darkhold" branding.
**Goal**: Admin-configurable logo, colors, fonts, domain, email templates, and game screen branding. CSS custom properties already in place for theming -- extend to admin-managed brand assets.
**Why it matters**: Organizations want their brand on training materials. Required for enterprise adoption and reseller channels.

#### L8. No Webhook / Event System (Milestone 13)
**Current state**: No external event notifications.
**Goal**: Emit webhooks on key events (game started, ended, participant joined, results finalized). Allow organizations to pipe data into Slack, Teams, email, or custom systems.
**Why it matters**: Enterprise workflows require automation hooks. Low effort, high integration value.

#### L9. No Presentation Tool Integration (Milestone 20)
**Current state**: Quizzes run in a separate browser tab from presentations.
**Goal**: Build embeddable game view that can be iframed into presentation tools. Create plugins for PowerPoint and Google Slides.
**Why it matters**: Mentimeter and Slido dominate the presentation-integrated quiz space. This opens the events/meetings segment.

#### L10. Single-Server Architecture (Milestone 21)
**Current state**: WebSocket state is in-memory per server. No horizontal scaling.
**Goal**: Support multi-node deployment with Redis pub-sub for WebSocket message brokering, distributed session storage, and Kubernetes manifests with Helm charts.
**Why it matters**: Organizations running large events (500+ concurrent) or institution-wide deployments need reliability at scale.

#### L11. Low Test Coverage Floor (Milestones TC-1 through TC-5)
**Current state**: 51% instruction coverage, 34% branch coverage. JaCoCo minimum set at 35%. Seven packages below 25% coverage (analytics.service at 4%, team.service at 7%, practice.service at 11%).
**Goal**: Raise to 85% instruction / 70% branch coverage through 5 dedicated milestones. Progressively raise JaCoCo minimum: 35% -> 45% -> 55% -> 62% -> 68% -> 75%. See [TEST_COVERAGE_PLAN.md](TEST_COVERAGE_PLAN.md) for per-package breakdown.
**Why it matters**: Enterprise customers expect 60%+ coverage. Higher coverage enables safe refactoring for the features above. Security-critical code (SecurityConfig, RateLimitingService, WebSocketConfig) is currently near-zero branch coverage.

#### L12. No Mobile Apps (Future)
**Current state**: Browser-only with responsive design and PWA.
**Goal**: Evaluate React Native or Flutter mobile app built on the REST API (Milestone 13). PWA improvements in the interim.
**Why it matters**: Native apps improve notification delivery, offline caching, and app store discoverability.

---

## Comparison with Competitors (Updated)

| Feature | Kahoot | Wayground | Mentimeter | Slido | Darkhold (Now) | Darkhold (Goal) |
|---------|:------:|:---------:|:----------:|:-----:|:--------------:|:---------------:|
| Multiple choice | Yes | Yes | Yes | Yes | Yes | Yes |
| True/False | Yes | Yes | Yes | Yes | Yes | Yes |
| Type answer | Yes | Yes | No | No | Yes | Yes |
| Poll | Yes | Yes | Yes | Yes | Yes | Yes |
| Image/Video questions | Yes | Yes | Yes | Partial | Yes | Yes |
| AI quiz generation | Yes | Yes | Yes | Partial | **No** | **Yes (M14)** |
| Self-paced/Homework | Yes | Yes | Yes | No | Partial | **Yes (M16)** |
| Team mode | Yes | No | No | No | Yes | Yes |
| Analytics & reports | Yes | Yes | Yes | Yes | Basic | **Advanced (M17)** |
| LMS (SCORM/LTI) | SCORM | LTI | Partial | Webex | **No** | **Yes (M18)** |
| SSO/OIDC | Yes | Yes | Yes | Yes | **No** | **Yes (M15)** |
| REST API | Yes | Yes | Yes | Yes | **No** | **Yes (M13)** |
| White-label branding | Enterprise | Enterprise | Enterprise | No | **No** | **Yes (M19)** |
| Self-hosted | **No** | **No** | **No** | **No** | **Yes** | **Yes** |
| Free unlimited players | No | Limited | No | No | **Yes** | **Yes** |
| Open source | No | No | No | No | **Yes** | **Yes** |
| Air-gapped / offline AI | No | No | No | No | No | **Yes (M14)** |
| Horizontal scaling | Yes | Yes | Yes | Yes | **No** | **Yes (M21)** |

---

## Milestone Execution Order

**Recommended sequence based on dependencies and strategic value:**

| Order | Milestone | Focus | Rationale |
|:-----:|:---------:|-------|-----------|
| 1 | **M12** | Security & Scale | Foundation hardening; already scoped |
| 2 | **M13** | REST API | Enables M14, M16, M18, M20; platform foundation |
| 3 | **M14** | AI Quiz Generation | Closes #1 competitive gap; unique with local model support |
| 4 | **M15** | SSO / Enterprise Identity | Hard gate to enterprise adoption |
| 5 | **M16** | Async / Homework Mode | Doubles use-case surface area |
| 6 | **M17** | Advanced Analytics | Premium differentiator; leverages existing data |
| 7 | **M18** | LMS Integration | Unlocks education + enterprise procurement |
| 8 | **M19** | White-Label Branding | Enterprise requirement; extends theme system |
| 9 | **M20** | Platform Ecosystem | Plugin architecture + marketplace |
| 10 | **M21** | Horizontal Scaling | Large deployment support |

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines. Pick any milestone or task and submit a PR.
