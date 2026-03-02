# Darkhold Implementation Milestones

This document breaks down the project goals into actionable milestones. Each milestone is self-contained and delivers tangible value.

---

## Milestone 1: Foundation Improvements - COMPLETED

**Status**: Completed on 2026-01-06
**Focus**: Fix existing issues and prepare for extensibility

### 1.1 Database Production Readiness
- [x] Add PostgreSQL driver dependency
- [x] Create application-postgres.properties profile
- [ ] Add MySQL driver dependency (deferred - PostgreSQL prioritized)
- [ ] Create application-mysql.properties profile (deferred)
- [x] Add Flyway migrations for schema versioning
- [x] Update docker-compose with database options

### 1.2 Code Quality & Bug Fixes
- [x] Fix TODO in ChallengeController - proper HTTP error responses
- [x] Fix TODO in OptionsController - bind all users properly
- [x] Add input validation annotations (@Valid, @NotBlank)
- [x] Improve error handling with custom exception handler (GlobalExceptionHandler)

### 1.3 Configuration Externalization
- [x] Make game timer configurable via properties (darkhold.game.timer-seconds)
- [x] Make PIN length configurable (darkhold.game.pin-length)
- [ ] Add feature flags for upcoming features (deferred to later milestone)

### Files Created
- `src/main/resources/application-postgres.properties`
- `src/main/resources/db/migration/V1__initial_schema.sql`
- `src/main/resources/db/migration/V2__seed_data.sql`
- `src/main/java/com/quiz/darkhold/init/GlobalExceptionHandler.java`
- `src/main/java/com/quiz/darkhold/init/ErrorResponse.java`
- `src/main/java/com/quiz/darkhold/init/GameConfig.java`
- `docker-compose.h2.yml`
- Updated `docker-compose.yml` (PostgreSQL)

### Test Coverage
- All 233 tests passing
- Code coverage: 71%+

**Deliverable**: Production-deployable application with PostgreSQL support

---

## Milestone 2: Question Type Expansion - COMPLETED

**Status**: Completed on 2026-01-07
**Focus**: Support more question formats beyond multiple choice

### 2.1 Data Model Updates - COMPLETED
- [x] Add `questionType` enum to QuestionSet entity (MULTIPLE_CHOICE, TRUE_FALSE, TYPE_ANSWER, POLL)
- [x] Add `timeLimit` field to QuestionSet (per-question timer)
- [x] Add `points` field to QuestionSet (configurable points)
- [x] Add `acceptableAnswers` field for TYPE_ANSWER fuzzy matching
- [x] Create database migration V4 for new fields
- [x] Update DTOs (QuestionRequest, QuestionResponse, ChallengeExportDto)
- [x] Add Apache Commons Text dependency for Levenshtein distance

### 2.2 Question Editor Updates - COMPLETED
- [x] Add question type selector to editchallenge.html
- [x] Add time limit dropdown (5s-240s or default)
- [x] Add points input field (default 1000)
- [x] Conditional answer fields per question type
- [x] True/False radio buttons for T/F questions
- [x] Text input with acceptable answers for TYPE_ANSWER
- [x] Hide correct checkboxes for POLL type
- [x] Update QuestionService to handle new fields

### 2.3 Excel Parser Updates - COMPLETED
- [x] Update Excel parser to handle new columns (Type, TimeLimit, Points, AcceptableAnswers)
- [x] Backward compatible - old 6-column format still works
- [x] Update duplicateChallenge to copy new fields
- [x] Update importFromJson to handle new fields

### 2.4 Game Flow Updates - COMPLETED
- [x] Create AnswerValidationService with Levenshtein fuzzy matching
- [x] Update GameController for different question types
- [x] Add /validate_answer/ endpoint for TYPE_ANSWER server-side validation
- [x] Pass question_type and question_points to game view
- [x] Support per-question time limits

### 2.5 Game UI Updates - COMPLETED
- [x] Update game.html for different question type layouts
- [x] Update game-scripts.js for type-specific answer handling
- [x] Add TYPE_ANSWER text input UI with server-side validation
- [x] TRUE_FALSE question display with True/False buttons
- [x] POLL questions without correct/incorrect highlighting

### 2.6 Import/Export Updates - COMPLETED
- [x] CSV export includes new fields (Type, TimeLimit, Points, AcceptableAnswers)
- [x] JSON export includes new fields (via ChallengeExportDto)
- [x] Import handles new fields

### Files Created/Modified
- `QuestionType.java` - New enum
- `V4__add_question_type_fields.sql` - New migration
- `QuestionSet.java` - Added new fields
- `QuestionRequest.java`, `QuestionResponse.java`, `ChallengeExportDto.java` - Updated DTOs
- `editchallenge.html` - New question type UI
- `QuestionService.java` - Handle new fields
- `ChallengeService.java` - Updated Excel parser and import/export
- `AnswerValidationService.java` - New service with Levenshtein fuzzy matching
- `GameController.java` - Updated for question types, new validation endpoint
- `GameControllerTest.java` - Updated for new constructor parameter
- `game.html` - Conditional layouts for different question types
- `game-scripts.js` - Handlers for TRUE_FALSE, TYPE_ANSWER, POLL
- `ChallengeController.java` - Updated CSV export with new columns
- `dependencies.gradle` - Added commons-text

**Deliverable**: Support for 4 question types with configurable settings

---

## Milestone 3: In-App Question Editor - COMPLETED

**Status**: Completed on 2026-01-06
**Focus**: Create/edit quizzes without Excel files

### 3.1 Challenge Management UI
- [x] Create "New Challenge" form (title, description)
- [x] Create challenge edit page
- [x] Add challenge duplicate/clone feature
- [ ] Implement challenge archive (soft delete) - deferred (hard delete sufficient)

### 3.2 Question Editor
- [x] Create question add/edit form (modal dialog)
- [ ] Implement question type selector (deferred to Milestone 2)
- [x] Add answer options dynamic form (2-4 answers)
- [x] Implement correct answer selector (checkbox)
- [x] Add question preview (in edit page)

### 3.3 Question Management
- [x] Implement drag-and-drop question reordering (SortableJS)
- [x] Add question duplicate within challenge
- [x] Add question delete with confirmation
- [x] Implement bulk question operations

### 3.4 Import/Export
- [x] Keep Excel import as option
- [x] Add JSON export of challenges
- [x] Add JSON import of challenges
- [x] Add CSV export option

### Files Created
- `QuestionController.java` - REST API for question CRUD
- `QuestionService.java` - Question business logic
- `QuestionRequest.java`, `QuestionResponse.java`, `ReorderRequest.java` - DTOs
- `ChallengeExportDto.java` - Export/import DTO
- `V3__add_question_order.sql` - Database migration
- `createchallengeform.html` - New challenge form
- `editchallenge.html` - Question editor with drag-and-drop

### API Endpoints Added
- `POST /save_challenge` - Create empty challenge
- `GET/POST /edit_challenge/{id}` - Edit challenge
- `POST /duplicate_challenge/{id}` - Clone challenge
- `GET /export_challenge/{id}/json` - JSON export
- `GET /export_challenge/{id}/csv` - CSV export
- `POST /import_challenge/json` - JSON import
- `/api/question/*` - Full REST API for questions

**Deliverable**: Full in-browser quiz creation and management

---

## Milestone 4: Media Support - COMPLETED

**Status**: Completed on 2026-01-10
**Focus**: Add images and videos to questions

### 4.1 Image Upload Infrastructure
- [x] Add image size/type validation
- [ ] Create file upload service (deferred - using direct URLs)
- [ ] Configure storage location (deferred - using external URLs)
- [ ] Implement image compression/resize (deferred)

### 4.2 Question Images
- [x] Add `imageUrl` field to QuestionSet
- [x] Update question editor with image URL input
- [x] Display images in game view
- [x] Handle image loading states

### 4.3 Answer Images
- [ ] Add image fields to answer options (deferred to future)
- [ ] Create image answer UI component (deferred)
- [ ] Update game template for image answers (deferred)

### 4.4 Video Support
- [x] Add `videoUrl` field to QuestionSet
- [x] Support YouTube embed URLs
- [x] Create video player component for questions
- [ ] Support direct video file upload (deferred - using YouTube/external URLs)

### 4.5 Cloud Storage (Optional)
- [ ] Add S3 storage adapter (deferred)
- [ ] Add configuration for cloud storage (deferred)
- [ ] Implement CDN support for media (deferred)

### Files Created/Modified
- `V6__add_media_support.sql` - Added imageUrl and videoUrl columns
- `QuestionSet.java` - Added imageUrl and videoUrl fields
- `game.html` - Added media display in game view
- `editchallenge.html` - Added URL input fields for media

**Deliverable**: Rich media questions with images and videos via external URLs

---

## Milestone 5: Enhanced Game Experience - COMPLETED

**Status**: Completed on 2026-01-10
**Focus**: Improve the live game playing experience

### 5.1 Scoring Improvements
- [x] Implement answer streak bonus (2x, 3x, 4x multipliers)
- [x] Show score change animation after each question
- [x] Display rank change (+2, -1) with up/down arrows
- [x] Display streak badges on scoreboard
- [ ] Add "Double Points" question option (deferred)

### 5.2 Lobby Enhancements
- [x] Show live participant count (getParticipantCount in GameService)
- [x] Add "Kick player" functionality for moderator (kickPlayer in GameService)
- [ ] Display participant avatars/colors (deferred)
- [ ] Implement nickname validation (length, characters) (deferred)
- [ ] Add basic profanity filter (deferred)

### 5.3 Game Controls
- [x] Add "Pause Game" button for moderator
- [x] Add "Skip Question" option
- [x] Add "End Game Early" with confirmation
- [ ] Implement "Extend Time" mid-question (deferred)

### 5.4 Player Experience
- [x] Show question progress (Question X of Y)
- [x] Display time remaining prominently (FlipClock countdown)
- [x] Add answer confirmation animation
- [x] Show "Waiting for others" after answering

### 5.5 Results Screen
- [x] Animate score reveals (countUp animation)
- [x] Show podium for top 3 with gold/silver/bronze
- [x] Display streak information on scoreboard
- [ ] Display statistics (fastest answer, most streaks) (deferred to Milestone 7: Analytics)

### Files Created/Modified
- `Challenge.java` - Added totalQuestions field
- `GameController.java` - Pass totalQuestions to view
- `game.html` - Added question progress badge, waiting message
- `game-scripts.js` - Show waiting message after answer submission
- `scoreboard.html` - Already had rank indicators, streak badges, score deltas
- `finalscore.html` - Already had animated podium with gold/silver/bronze
- `GameService.java` - Already had streak tracking, pause/resume, kick player
- `ScoreResult.java` - Already had streak multipliers, rank change calculation

**Deliverable**: Polished, engaging game experience with streak bonuses and visual feedback

---

## Milestone 6: Multiple Concurrent Games - COMPLETED

**Status**: Completed on 2026-01-10
**Focus**: Allow multiple games to run simultaneously

### 6.1 Session Isolation
- [x] CurrentGame already PIN-scoped (repository-based)
- [x] GameService already supports PIN parameters
- [x] Update PreviewService.getActiveChallenge() - deprecated for concurrent games
- [x] WebSocket messages already game-scoped via PIN

### 6.2 Game Management
- [x] Create "My Active Games" dashboard for moderators
- [x] Add game status indicators (WAITING, IN_PROGRESS, PAUSED, FINISHED)
- [x] Implement game timeout/auto-cleanup scheduler

### 6.3 PIN Management
- [x] Ensure PIN uniqueness across active games (retry logic with 10 attempts)
- [x] Add PIN expiration (2 hours for WAITING, 24 hours for active games)
- [x] Add moderator tracking for game ownership queries

### Files Created/Modified
- `Game.java` - Added moderator field
- `GameRepository.java` - Added methods for querying by moderator and status
- `PreviewService.java` - Added PIN uniqueness check, moderator tracking, concurrent game queries
- `ActiveGamesController.java` - NEW: Controller for active games dashboard
- `GameInfo.java` - NEW: DTO for game display
- `activegames.html` - NEW: Dashboard template
- `GameCleanupScheduler.java` - NEW: Scheduled cleanup of expired games
- `DarkholdApplication.java` - Added @EnableScheduling
- `navbar.html` - Added link to active games dashboard
- `common-scripts.js` - Added toActiveGames() function
- `application.properties` - Added game expiration configuration
- `V8__add_moderator_to_game.sql` - NEW: Database migration

**Deliverable**: Full support for hosting multiple simultaneous quiz sessions with proper isolation and management

---

## Milestone 7: Analytics & Reports - COMPLETED

**Status**: Completed on 2026-01-10
**Focus**: Track and report game results

### 7.1 Data Persistence
- [x] Create GameResult entity for completed games
- [x] Create ParticipantResult entity for individual scores
- [x] Create QuestionResult entity for answer statistics
- [x] Store results when game ends

### 7.2 Game History
- [x] Create "Past Games" page
- [x] Display game summary (date, participants, winner)
- [x] View detailed results per game
- [x] Filter games by moderator (automatic per user)
- [ ] Advanced filters by challenge/date (deferred)

### 7.3 Question Analytics
- [x] Show correct/incorrect distribution per question
- [x] Calculate success rate percentages
- [x] Identify "hardest" questions (difficulty levels: EASY, MEDIUM, HARD)
- [x] Display question-level statistics in detailed view
- [ ] Display average answer time (infrastructure ready, data collection pending)

### 7.4 Export Features
- [x] Export game results to CSV
- [ ] Export game results to Excel (deferred)
- [ ] Generate PDF summary report (deferred)
- [ ] Email results to moderator (deferred)

### Files Created/Modified
- `GameResult.java` - Entity for game sessions
- `ParticipantResult.java` - Entity for player performance
- `QuestionResult.java` - Entity for question statistics
- `GameResultRepository.java` - Repository with query methods
- `ParticipantResultRepository.java` - Repository for participant data
- `QuestionResultRepository.java` - Repository for question data
- `ResultService.java` - Service for saving/retrieving results
- `PastGamesController.java` - Controller for past games and results
- `pastgames.html` - Past games list view
- `gameresult.html` - Detailed game result view with analytics
- `GameController.java` - Modified to save results on game end
- `navbar.html` - Added link to past games
- `common-scripts.js` - Added toPastGames() function
- `V9__add_analytics_tables.sql` - Database migration
- `checkstyle/suppressions.xml` - Added suppressions for analytics files

**Deliverable**: Comprehensive game analytics with detailed statistics and CSV export

---

## Milestone 8: Game Modes - COMPLETED

**Status**: Completed on 2026-01-10
**Focus**: Support different ways to play

### 8.1 Self-Paced Mode
- [x] Game mode defined in enum (SELF_PACED)
- [x] UI preview card created ("Coming Soon" status)
- [ ] Full implementation deferred to future release
- [ ] Generate shareable link (no PIN needed) (deferred)
- [ ] Allow retakes with configurable attempts (deferred)
- [ ] Set availability window (start/end date) (deferred)

### 8.2 Practice Mode
- [x] Add game_mode field to Game and CurrentGameSession entities
- [x] Create PracticeService for solo play
- [x] Database migration V7 for game mode support
- [x] Practice game initialization without PIN (UUID-based)
- [x] Skip waiting room (direct to game)
- [x] UI for starting practice mode from preview page
- [x] Session-based practice game tracking
- [ ] Show correct answer immediately after each question (infrastructure ready, UI refinement deferred)
- [ ] Enhanced instant feedback UI (deferred to Milestone 10: UI/UX)

### 8.3 Challenge Mode
- [x] Game mode defined in enum (CHALLENGE)
- [x] UI preview card created ("Coming Soon" status)
- [ ] Full implementation deferred to future release
- [ ] Leaderboard across all attempts (deferred)
- [ ] Best score tracking (deferred)
- [ ] Time-based ranking (deferred)

### 8.4 Game Mode Selection UI
- [x] Enhanced preview page with mode selection cards
- [x] Visual game mode selector with icons and descriptions
- [x] Interactive cards with hover effects
- [x] Support for MULTIPLAYER and PRACTICE modes (active)
- [x] Placeholder cards for SELF_PACED and CHALLENGE modes

### Files Created/Modified
- `V7__add_game_mode.sql` - Added game_mode column
- `PracticeService.java` - Solo play service implementation
- `GameMode.java` - Expanded enum: MULTIPLAYER, PRACTICE, SELF_PACED, CHALLENGE
- `PreviewController.java` - Added startPractice endpoint
- `preview.html` - Game mode selection UI with styled cards
- `CurrentGameSession.java` - game_mode and gameStatus fields

**Deliverable**: Complete game mode infrastructure with MULTIPLAYER and PRACTICE modes fully functional, modern UI for mode selection, and extensible framework for future modes (SELF_PACED and CHALLENGE)

---

## Milestone 9: Team Features - COMPLETED

**Status**: Completed on 2026-01-30
**Focus**: Enable team-based gameplay

### 9.1 Team Creation
- [x] Create team before game starts
- [x] Random team assignment option
- [x] Custom team names/colors
- [x] Balance teams by participant count (Balanced/Random/Manual assignment methods)

### 9.2 Team Scoring
- [x] Aggregate team scores
- [x] Show team leaderboard
- [x] Display individual contribution to team

### 9.3 Team UI
- [x] Team lobby view
- [x] Team vs Team results screen
- [x] Team celebration animations

### Files Created
- `src/main/java/com/quiz/darkhold/team/` - Full team package
  - `TeamController.java` - Team REST API
  - `TeamService.java` - Team business logic
  - `TeamConfig.java`, `TeamInfo.java`, `TeamScoreResult.java` - DTOs
  - `TeamResult.java` - Entity for team results
  - `TeamResultRepository.java` - Data access
  - `TeamAssignmentMethod.java` - Enum (BALANCED, RANDOM, MANUAL)

**Deliverable**: Collaborative team gameplay

---

## Milestone 10: UI/UX Polish - COMPLETED

**Status**: Completed on 2026-01-17
**Focus**: Modern, accessible interface

### 10.1 Responsive Design
- [x] Audit mobile game experience
- [x] Optimize touch targets for answers
- [x] Test on various screen sizes
- [x] Add PWA manifest for mobile install

### 10.2 Theming
- [x] Implement dark/light mode toggle
- [x] Create theme system (CSS variables)
- [ ] Allow custom quiz colors (deferred)
- [ ] Add lobby background options (deferred)

### 10.3 Accessibility
- [x] Add ARIA labels throughout
- [x] Ensure keyboard navigation works
- [x] Test with screen readers
- [x] Implement high contrast mode
- [x] Add colorblind-friendly palettes

### 10.4 Sound & Animation
- [x] Add optional sound effects
- [x] Implement countdown sounds
- [x] Add victory/defeat sounds
- [x] Create smooth transitions

### 10.5 Authentication Pages Redesign
- [x] Modernize login page with brand identity
- [x] Modernize registration page to match login
- [ ] Add password reset page styling (deferred)
- [x] Consistent error/success messaging

### Files Created
- `theme-variables.css` - CSS variable system with light/dark themes
- `theme-manager.js` - Theme switching with localStorage persistence
- `audio-manager.js` - Sound effect manager with volume control
- `accessibility.js` - Keyboard navigation, ARIA live regions
- `animations.css` - Victory/defeat animations, confetti, focus indicators
- `manifest.json` - PWA configuration with 8 icon sizes
- `service-worker.js` - Offline caching and asset management
- `offline.html` - Offline fallback page
- 7 sound files (correct, incorrect, tick, countdown, victory, defeat, join)

**Deliverable**: Polished, accessible user experience

---

## Milestone 11: Internationalization - COMPLETED ✅

**Status**: 100% Complete (All 4 target languages complete)
**Focus**: Multi-language support
**Completed**: 2026-02-16

### 11.1 Framework Setup - COMPLETED ✅
- [x] Configure Spring MessageSource (I18nConfig.java)
- [x] Extract all UI strings to messages.properties (427 keys)
- [x] Create language selector component (navbar dropdown with globe icon)
- [x] Store user language preference (localStorage + session)
- [x] All 24 Thymeleaf templates updated to use `#{key}` expressions

### 11.2 Translations - COMPLETED ✅
- [x] **English (en)** - 427/427 messages ✅ Complete
- [x] **Spanish (es)** - 427/427 messages ✅ Complete
- [x] **French (fr)** - 427/427 messages ✅ Complete
- [x] **German (de)** - 427/427 messages ✅ Complete
- [x] Community contribution guidelines added to CONTRIBUTING.md

### 11.3 RTL Support - PENDING
- [ ] Add RTL CSS styles
- [ ] Test Arabic/Hebrew layouts
- [ ] Handle mixed LTR/RTL content

### Files Created
- `src/main/java/com/quiz/darkhold/init/I18nConfig.java` - MessageSource, LocaleResolver, LocaleChangeInterceptor
- `src/main/resources/messages.properties` - English base (427 keys) ✅
- `src/main/resources/messages_es.properties` - Spanish translations (427 keys) ✅
- `src/main/resources/messages_fr.properties` - French translations (427 keys) ✅
- `src/main/resources/messages_de.properties` - German translations (427 keys) ✅
- `src/main/resources/static/scripts/language-manager.js` - Language switching logic
- `I18N_STATUS.md` - Translation progress tracking document
- `translation_helper.sh` - Script to check translation progress

**Deliverable**: Full support for English, Spanish, French, and German languages across all UI elements

### Templates Updated
All 24 Thymeleaf templates updated to use `#{key}` message expressions:
- Authentication: login.html, registration.html
- Game: game.html, gamewait.html, scoreboard.html, finalscore.html
- Challenge: createchallenge.html, editchallenge.html, viewchallenges.html, preview.html, publish.html
- Admin: options.html, usermanagement.html, user_form.html, gameManagement.html
- Analytics: activegames.html, pastgames.html, gameresult.html
- Other: index.html, myprofile.html, error.html, offline.html

**Deliverable**: Multi-language quiz platform

---

## Milestone 12: Security & Scale

**Focus**: Production hardening

### 12.1 Security
- [ ] Implement rate limiting on PIN entry
- [ ] Add CAPTCHA for repeated failures
- [ ] Review CSRF protection
- [ ] Add Content Security Policy headers
- [ ] Implement password strength requirements
- [ ] Add account lockout policy

### 12.2 Performance
- [ ] Add Redis for session storage
- [ ] Implement caching for challenges
- [ ] Optimize database queries
- [ ] Add database connection pooling

### 12.3 Monitoring
- [ ] Enhance actuator endpoints
- [ ] Add Prometheus metrics
- [ ] Create Grafana dashboard templates
- [ ] Implement structured logging

### 12.4 Deployment
- [ ] Create Kubernetes manifests
- [ ] Add Helm chart
- [ ] Document horizontal scaling
- [ ] Add health check endpoints

**Deliverable**: Secure, scalable, production-ready platform

---

## Milestone 13: REST API & Integration Foundation

**Status**: Pending
**Focus**: Expose programmatic access to all platform capabilities
**Addresses**: Limitation L1 (No REST API), L8 (No Webhooks)
**Enables**: Milestones 14, 16, 18, 20

### 13.1 API Infrastructure
- [ ] Add springdoc-openapi dependency for Swagger UI and OpenAPI 3.0 spec generation
- [ ] Create `/api/v1/` base path with versioning strategy
- [ ] Implement JWT-based API authentication (issue tokens via `/api/v1/auth/token`)
- [ ] Add API rate limiting (separate from UI rate limits)
- [ ] Create API key management for service-to-service integration
- [ ] Add CORS configuration for API consumers

### 13.2 Challenge API
- [ ] `GET /api/v1/challenges` - List challenges (paginated, filterable)
- [ ] `POST /api/v1/challenges` - Create challenge
- [ ] `GET /api/v1/challenges/{id}` - Get challenge details
- [ ] `PUT /api/v1/challenges/{id}` - Update challenge
- [ ] `DELETE /api/v1/challenges/{id}` - Delete challenge
- [ ] `POST /api/v1/challenges/{id}/duplicate` - Duplicate challenge
- [ ] `GET /api/v1/challenges/{id}/questions` - List questions
- [ ] `POST /api/v1/challenges/{id}/questions` - Add question
- [ ] `PUT /api/v1/challenges/{id}/questions/{qid}` - Update question
- [ ] `DELETE /api/v1/challenges/{id}/questions/{qid}` - Delete question
- [ ] `POST /api/v1/challenges/import` - Import (JSON/Excel)
- [ ] `GET /api/v1/challenges/{id}/export` - Export (JSON/CSV)

### 13.3 Game API
- [ ] `POST /api/v1/games` - Create/publish game from challenge
- [ ] `GET /api/v1/games/{pin}` - Get game status
- [ ] `POST /api/v1/games/{pin}/start` - Start game
- [ ] `POST /api/v1/games/{pin}/pause` - Pause game
- [ ] `POST /api/v1/games/{pin}/resume` - Resume game
- [ ] `POST /api/v1/games/{pin}/skip` - Skip current question
- [ ] `POST /api/v1/games/{pin}/end` - End game
- [ ] `GET /api/v1/games/{pin}/participants` - List participants
- [ ] `GET /api/v1/games/{pin}/scoreboard` - Get current scoreboard

### 13.4 Analytics API
- [ ] `GET /api/v1/analytics/games` - List past games (paginated)
- [ ] `GET /api/v1/analytics/games/{id}` - Get game result details
- [ ] `GET /api/v1/analytics/games/{id}/participants` - Participant results
- [ ] `GET /api/v1/analytics/games/{id}/questions` - Question statistics
- [ ] `GET /api/v1/analytics/games/{id}/export` - Export results (CSV/PDF)

### 13.5 User & Admin API
- [ ] `GET /api/v1/users/me` - Current user profile
- [ ] `PUT /api/v1/users/me` - Update profile
- [ ] `GET /api/v1/admin/users` - List users (admin only)
- [ ] `POST /api/v1/admin/users` - Create user (admin only)
- [ ] `PUT /api/v1/admin/users/{id}` - Update user (admin only)

### 13.6 Webhook System
- [ ] Create webhook registration entity and admin UI
- [ ] `POST /api/v1/webhooks` - Register webhook URL
- [ ] `GET /api/v1/webhooks` - List registered webhooks
- [ ] `DELETE /api/v1/webhooks/{id}` - Remove webhook
- [ ] Emit events: `game.created`, `game.started`, `game.ended`, `participant.joined`, `results.finalized`
- [ ] Implement retry logic with exponential backoff (3 attempts)
- [ ] Add webhook signature verification (HMAC-SHA256)

### Files to Create/Modify
- `api/` package - New API controllers, DTOs, JWT filter
- `init/ApiSecurityConfig.java` - Separate security chain for `/api/**`
- `init/JwtTokenProvider.java` - JWT issue/validate
- `webhook/` package - Webhook entity, repository, service, async dispatcher
- Database migration for webhook and API key tables
- `application.properties` - JWT secret, webhook config

**Deliverable**: Documented REST API (Swagger UI at `/api/docs`) with JWT auth and webhook event system

---

## Milestone 14: AI-Powered Quiz Generation

**Status**: Pending
**Focus**: Generate quizzes from text, documents, and URLs using AI
**Addresses**: Limitation L2 (No AI Features)
**Depends on**: Milestone 13 (REST API for AI endpoints)

### 14.1 AI Provider Abstraction
- [ ] Create `AiProvider` interface with `generateQuestions(prompt, config)` method
- [ ] Implement `OpenAiProvider` (GPT-4o / GPT-4o-mini)
- [ ] Implement `AnthropicProvider` (Claude)
- [ ] Implement `OllamaProvider` for local models (Llama 3, Mistral, Gemma)
- [ ] Create provider factory with configurable selection
- [ ] Add admin settings page for API keys and provider selection
- [ ] Support provider fallback chain (try primary, fall back to secondary)

### 14.2 Content Extraction Pipeline
- [ ] Plain text input - direct pass to LLM
- [ ] PDF upload - extract text via Apache PDFBox
- [ ] URL input - fetch and extract main content (Jsoup HTML parsing)
- [ ] Word document upload - extract via Apache POI (already in dependencies)
- [ ] Implement content chunking for documents exceeding token limits
- [ ] Add content sanitization and length validation

### 14.3 Quiz Generation Engine
- [ ] Design prompt templates for each question type (MCQ, T/F, Type Answer, Poll)
- [ ] Generate configurable number of questions (5, 10, 15, 20)
- [ ] Generate difficulty levels (Easy, Medium, Hard, Mixed)
- [ ] Generate distractors (wrong answers) that are plausible
- [ ] Parse structured LLM output into QuestionRequest DTOs
- [ ] Add generation parameters: topic focus, language, target audience

### 14.4 Review & Edit Workflow
- [ ] Create "AI Generate" button on challenge creation page
- [ ] Build generation wizard UI (input source -> configure -> review)
- [ ] Show generated questions in editable preview before saving
- [ ] Allow accept/reject/edit individual questions
- [ ] "Regenerate" button for individual questions
- [ ] Save accepted questions to challenge via existing QuestionService

### 14.5 AI API Endpoints
- [ ] `POST /api/v1/ai/generate` - Generate questions from text/URL/file
- [ ] `POST /api/v1/ai/regenerate` - Regenerate a single question
- [ ] `GET /api/v1/ai/providers` - List available providers and status
- [ ] `POST /api/v1/ai/explain` - Generate explanation for a question (future use)

### 14.6 Air-Gapped / Local Model Support
- [ ] Document Ollama setup and compatible models
- [ ] Auto-detect Ollama availability on configurable host:port
- [ ] Test with Llama 3 8B, Mistral 7B, Gemma 2B (various resource profiles)
- [ ] Add model selection in admin settings
- [ ] Graceful fallback messaging when no AI provider is configured

### Files to Create/Modify
- `ai/` package - AiProvider interface, implementations, service, controller
- `ai/prompt/` - Prompt templates per question type and language
- `ai/extraction/` - PDF, URL, DOCX content extractors
- Admin settings page for AI configuration
- AI generation wizard templates and JavaScript
- Database migration for AI configuration storage

**Deliverable**: AI quiz generation from text, PDF, URLs with support for cloud APIs and local models (Ollama)

---

## Milestone 15: SSO & Enterprise Identity

**Status**: Pending
**Focus**: Enterprise identity provider integration
**Addresses**: Limitation L3 (No SSO/SAML/OIDC)

### 15.1 OAuth2 / OIDC Integration
- [ ] Add spring-boot-starter-oauth2-client dependency
- [ ] Configure OAuth2 login for generic OIDC providers
- [ ] Implement auto-provisioning of users from OIDC claims (email, name, roles)
- [ ] Map OIDC groups/roles to Darkhold roles (ADMIN, GAME_MANAGER, PARTICIPANT)
- [ ] Support multiple concurrent OIDC providers

### 15.2 Pre-configured Providers
- [ ] Google Workspace (OAuth2)
- [ ] Microsoft Azure AD / Entra ID (OAuth2 + OIDC)
- [ ] Okta (OIDC)
- [ ] Keycloak (OIDC)
- [ ] Generic OIDC provider (manual configuration)

### 15.3 SAML 2.0 Support
- [ ] Add spring-security-saml2-service-provider dependency
- [ ] Implement SAML 2.0 Service Provider
- [ ] Support metadata XML import for IdP configuration
- [ ] Map SAML attributes to user fields and roles
- [ ] Test with ADFS, Okta SAML, Shibboleth

### 15.4 Admin Configuration UI
- [ ] Create "Identity Providers" admin page
- [ ] Add/edit/delete OIDC provider configuration
- [ ] Add/edit/delete SAML provider configuration
- [ ] Test connection button for each provider
- [ ] Configure role mapping rules
- [ ] Enable/disable local password login per deployment

### 15.5 User Lifecycle
- [ ] Just-in-time (JIT) user provisioning on first SSO login
- [ ] Link existing local accounts to SSO identities by email
- [ ] Handle account deactivation when removed from IdP
- [ ] Support forced SSO (disable local login option)
- [ ] Audit log for SSO login events

### 15.6 Database Changes
- [ ] Migration: Add `identity_provider` table (id, type, name, config_json, enabled)
- [ ] Migration: Add `user_identity` table (user_id, provider_id, external_id, email)
- [ ] Migration: Add `sso_enabled` and `local_login_enabled` to app settings

### Files to Create/Modify
- `sso/` package - SSO configuration, provider management, user provisioning
- `init/SecurityConfig.java` - Add OAuth2/SAML security chains
- Admin pages for identity provider management
- Database migrations for SSO tables
- `application.properties` - SSO configuration properties

**Deliverable**: Enterprise SSO via OAuth2/OIDC and SAML 2.0 with auto-provisioning and role mapping

---

## Milestone 16: Async & Homework Mode

**Status**: Pending
**Focus**: Enable self-paced quiz completion with deadlines
**Addresses**: Limitation L4 (No Async/Homework Mode)
**Depends on**: Milestone 13 (REST API)

### 16.1 Assignment Data Model
- [ ] Create `Assignment` entity (challenge_id, creator_id, title, instructions)
- [ ] Add deadline fields (start_datetime, end_datetime, timezone)
- [ ] Add attempt configuration (max_attempts, allow_retake, best_score_policy)
- [ ] Add access control (password, allowed_users, allowed_groups)
- [ ] Create `AssignmentAttempt` entity (assignment_id, user_id, started_at, completed_at, score)
- [ ] Database migration for assignment tables

### 16.2 Assignment Creation UI
- [ ] Add "Assign as Homework" option alongside "Play Live" on preview page
- [ ] Create assignment configuration form (deadline, attempts, access)
- [ ] Generate shareable link (no PIN needed, URL-based access)
- [ ] Generate QR code for assignment link
- [ ] Show assignment in "My Assignments" dashboard

### 16.3 Self-Paced Play Experience
- [ ] Participant opens assignment link, authenticates or enters name
- [ ] Skip waiting room - go directly to first question
- [ ] Show timer per question (or allow untimed mode)
- [ ] Show correct answer and explanation after each question
- [ ] Allow review of all answers before final submission
- [ ] Track attempt duration and per-question timing

### 16.4 Results & Aggregation
- [ ] Aggregate results after deadline
- [ ] Show class/group performance summary
- [ ] Compare across attempts (if retakes allowed)
- [ ] Rank by best score or latest attempt (configurable)
- [ ] Integrate with existing analytics (Milestone 7 tables)
- [ ] Export assignment results (CSV, API)

### 16.5 Notifications (Optional)
- [ ] Email notification when assignment is published (if SMTP configured)
- [ ] Reminder before deadline (24h, 1h)
- [ ] Results available notification after deadline
- [ ] Webhook events for assignment lifecycle

### 16.6 API Endpoints
- [ ] `POST /api/v1/assignments` - Create assignment
- [ ] `GET /api/v1/assignments` - List assignments
- [ ] `GET /api/v1/assignments/{id}` - Get assignment details
- [ ] `PUT /api/v1/assignments/{id}` - Update assignment
- [ ] `DELETE /api/v1/assignments/{id}` - Delete assignment
- [ ] `GET /api/v1/assignments/{id}/results` - Get aggregated results
- [ ] `POST /api/v1/assignments/{id}/attempt` - Start attempt (participant)

### Files to Create/Modify
- `assignment/` package - Entity, repository, service, controller
- Assignment creation and management templates
- Self-paced play templates (extend practice mode infrastructure)
- Assignment results/analytics templates
- Database migration for assignment tables
- `preview.html` - Add "Assign as Homework" card to game mode selection

**Deliverable**: Full async/homework mode with deadlines, retakes, and aggregate results

---

## Milestone 17: Advanced Analytics & Reporting

**Status**: Pending
**Focus**: Rich analytics dashboards with visualizations and PDF reports
**Addresses**: Limitation L5 (No Advanced Analytics)
**Depends on**: Milestone 7 (existing analytics tables), Milestone 13 (API)

### 17.1 Analytics Dashboard
- [ ] Add Chart.js or Apache ECharts dependency (CDN or bundled)
- [ ] Create analytics landing page with summary cards (total games, participants, questions)
- [ ] Add date range picker for filtering
- [ ] Show games-over-time line chart
- [ ] Show participants-per-game bar chart
- [ ] Show average scores trend line

### 17.2 Learning Gap Analysis
- [ ] Identify questions with lowest success rates across games
- [ ] Group weak areas by topic/tag (requires question tags from Question Bank)
- [ ] Show per-participant knowledge heatmap (topics vs. mastery)
- [ ] Highlight questions where wrong answers cluster on specific distractors
- [ ] Provide "recommended review" topics based on gap analysis

### 17.3 Question Analytics Deep Dive
- [ ] Question difficulty curve (distribution across Easy/Medium/Hard)
- [ ] Answer distribution pie charts per question
- [ ] Average time-to-answer per question
- [ ] Discrimination index (do good students get it right, weak students wrong?)
- [ ] Flag problematic questions (too easy, too hard, bad distractors)

### 17.4 Participant Progress Tracking
- [ ] Track individual participant performance over multiple games
- [ ] Show improvement trends (score trajectory over time)
- [ ] Compare participants within a cohort/group
- [ ] Streak and accuracy statistics per participant
- [ ] Achievement milestones (personal bests, improvement badges)

### 17.5 Report Generation
- [ ] PDF report generation using OpenPDF library
- [ ] Game summary report (overview, participants, question stats)
- [ ] Participant report card (individual performance, strengths, weaknesses)
- [ ] Cohort comparison report (class average, distribution, outliers)
- [ ] Email report delivery (if SMTP configured)
- [ ] Scheduled report generation (weekly/monthly summaries)

### 17.6 API Endpoints
- [ ] `GET /api/v1/analytics/dashboard` - Dashboard summary data
- [ ] `GET /api/v1/analytics/trends` - Time-series data for charts
- [ ] `GET /api/v1/analytics/gaps` - Learning gap analysis
- [ ] `GET /api/v1/analytics/participants/{id}/progress` - Individual progress
- [ ] `GET /api/v1/analytics/reports/{id}/pdf` - Generate PDF report

### Files to Create/Modify
- `analytics/dashboard/` - Dashboard controller, service, aggregation queries
- `analytics/report/` - PDF generator, email sender
- Analytics dashboard templates with Chart.js integration
- Participant progress tracking templates
- Database migration for tracking tables (if needed beyond existing schema)

**Deliverable**: Interactive analytics dashboards with charts, learning gap analysis, and PDF reports

---

## Milestone 18: LMS Integration (SCORM & LTI)

**Status**: Pending
**Focus**: Integrate with Learning Management Systems
**Addresses**: Limitation L6 (No LMS Integration)
**Depends on**: Milestone 13 (REST API), Milestone 16 (Async Mode)

### 18.1 SCORM 1.2 Export
- [ ] Create SCORM 1.2 content package generator
- [ ] Package challenge as SCO (Shareable Content Object)
- [ ] Implement SCORM API adapter (JavaScript runtime)
- [ ] Track: completion_status, score.raw, score.min, score.max, session_time
- [ ] Generate imsmanifest.xml with proper metadata
- [ ] ZIP package download from challenge export page

### 18.2 SCORM 2004 Export
- [ ] Create SCORM 2004 (3rd/4th Edition) package generator
- [ ] Implement sequencing and navigation rules
- [ ] Track: cmi.completion_status, cmi.success_status, cmi.score.scaled
- [ ] Support multi-SCO packages (one SCO per question group)

### 18.3 LTI 1.3 Tool Provider
- [ ] Add IMS Global LTI 1.3 library dependency
- [ ] Implement LTI 1.3 launch endpoint
- [ ] Handle OIDC login initiation flow
- [ ] Implement Deep Linking (Content-Item Message) for challenge selection
- [ ] Implement Assignment and Grade Services (AGS) for grade passback
- [ ] Support Names and Roles Provisioning Services (NRPS)

### 18.4 LMS Platform Registration
- [ ] Create LMS registration admin page
- [ ] Store platform credentials (client_id, deployment_id, key set URL)
- [ ] Support Moodle LTI 1.3 configuration
- [ ] Support Canvas LTI 1.3 configuration
- [ ] Support Blackboard LTI 1.3 configuration
- [ ] Support Google Classroom (via LTI or Classroom API)
- [ ] Provide Darkhold's public key set endpoint (JWKS)

### 18.5 Grade Passback
- [ ] Send scores back to LMS grade book after game/assignment completion
- [ ] Map Darkhold scores to LMS grade scale (0-100%)
- [ ] Support both immediate (live game) and deferred (assignment) grading
- [ ] Handle grade update for retakes (best score, latest score, average)
- [ ] Sync completion status for compliance tracking

### 18.6 xAPI (Experience API) Support
- [ ] Implement xAPI statement generation for game events
- [ ] Statements: attempted, completed, scored, answered, interacted
- [ ] Configure Learning Record Store (LRS) endpoint in admin settings
- [ ] Batch statement sending with retry logic

### Files to Create/Modify
- `lms/` package - SCORM packager, LTI provider, xAPI client
- `lms/scorm/` - SCORM manifest builder, API adapter JS
- `lms/lti/` - LTI launch handler, OIDC flow, AGS service
- `lms/xapi/` - Statement builder, LRS client
- Admin pages for LMS platform registration
- Database migration for LMS platform credentials and grade sync records
- Challenge export page - Add SCORM download button

**Deliverable**: SCORM 1.2/2004 export, LTI 1.3 provider with grade passback, xAPI event reporting

---

## Milestone 19: White-Label & Branding

**Status**: Pending
**Focus**: Organization-specific branding and customization
**Addresses**: Limitation L7 (No White-Label/Branding)

### 19.1 Brand Asset Management
- [ ] Create `BrandConfig` entity (logo_url, favicon_url, primary_color, secondary_color, accent_color, font_family, app_name)
- [ ] Admin page for uploading/configuring brand assets
- [ ] Store uploaded logos and favicons on disk (configurable path)
- [ ] Preview brand changes before saving
- [ ] Database migration for brand configuration table

### 19.2 Dynamic Theme Application
- [ ] Extend existing CSS custom properties to accept admin-configured values
- [ ] Inject brand colors into `theme-variables.css` dynamically via Thymeleaf
- [ ] Replace hardcoded "Darkhold" text with configurable `app_name` throughout templates
- [ ] Replace logo and favicon references with brand-configured assets
- [ ] Support custom CSS injection for advanced customization

### 19.3 Game Screen Branding
- [ ] Show organization logo on game waiting screen
- [ ] Show organization logo on final score screen
- [ ] Optional "Powered by Darkhold" footer (can be hidden for full white-label)
- [ ] Custom background patterns/images for game screens

### 19.4 Email & Export Branding
- [ ] Branded email templates (if SMTP configured)
- [ ] Branded PDF report headers and footers
- [ ] Branded CSV export with organization metadata
- [ ] Custom domain support documentation

### Files to Create/Modify
- `branding/` package - BrandConfig entity, repository, service, controller
- Admin branding configuration page
- Update all Thymeleaf templates to use brand variables
- Update `theme-variables.css` generation to include brand overrides
- Database migration for brand config table

**Deliverable**: Admin-configurable branding with custom logos, colors, fonts, and app name

---

## Milestone 20: Platform Ecosystem

**Status**: Pending
**Focus**: Plugin architecture, embeddable widgets, and community marketplace
**Addresses**: Limitation L9 (No Presentation Integration), future extensibility
**Depends on**: Milestone 13 (REST API)

### 20.1 Embeddable Game Widget
- [ ] Create lightweight embeddable game view (`/embed/game/{pin}`)
- [ ] Strip navigation, header, footer for clean iframe embedding
- [ ] Generate embed code snippet (`<iframe>` with configurable dimensions)
- [ ] Support PostMessage API for parent-frame communication
- [ ] Configurable embed options (show/hide scoreboard, branding, sound)

### 20.2 Presentation Tool Integration
- [ ] Google Slides Add-on: Launch quiz from within a presentation
- [ ] PowerPoint Add-in (Office.js): Embed quiz slide in PPTX
- [ ] Generic embed URL for any presentation tool that supports web embeds
- [ ] "Presentation Mode" game view optimized for projector display

### 20.3 Plugin Architecture
- [ ] Define plugin interface: `DarkholdPlugin` with lifecycle hooks (init, destroy)
- [ ] Extension points: custom question types, custom scoring algorithms, custom themes, custom export formats
- [ ] Plugin descriptor format (plugin.json with metadata, dependencies, version)
- [ ] Plugin classloader isolation (prevent conflicts)
- [ ] Plugin admin management page (install, enable, disable, remove)
- [ ] Plugin configuration storage (per-plugin settings)

### 20.4 Challenge Marketplace
- [ ] Allow users to publish challenges as "public" (opt-in)
- [ ] Create browse/search interface for public challenges
- [ ] Add tagging and categorization (subject, grade level, difficulty)
- [ ] Rating and review system (1-5 stars, text reviews)
- [ ] Fork/clone public challenges to own library
- [ ] Featured/trending challenges section
- [ ] Report inappropriate content mechanism

### 20.5 Community Features
- [ ] Public user profiles (games hosted, challenges created, rating)
- [ ] Follow creators for notifications on new challenges
- [ ] Challenge collections/playlists (curated sets by topic)
- [ ] Contributor leaderboard (most shared, highest rated)

### Files to Create/Modify
- `embed/` package - Embeddable game controller and stripped-down templates
- `plugin/` package - Plugin loader, registry, extension point interfaces
- `marketplace/` package - Public challenge service, search, ratings
- Embed templates (game-embed.html, scoreboard-embed.html)
- Marketplace browse/search templates
- Database migrations for marketplace tables (ratings, tags, visibility)

**Deliverable**: Embeddable quiz widget, presentation tool plugins, plugin architecture, and community challenge marketplace

---

## Milestone 21: Horizontal Scaling & High Availability

**Status**: Pending
**Focus**: Multi-node deployment for large-scale use
**Addresses**: Limitation L10 (Single-Server Architecture)
**Depends on**: Milestone 12 (Security & Scale foundation)

### 21.1 Distributed WebSocket Messaging
- [ ] Add Spring WebSocket broker relay with Redis pub-sub (or RabbitMQ STOMP)
- [ ] Replace SimpleBroker with external message broker
- [ ] Ensure game events propagate across all nodes
- [ ] Test concurrent games across multiple nodes
- [ ] Handle node failure gracefully (in-flight games continue on surviving nodes)

### 21.2 Distributed Session & State
- [ ] Add Spring Session with Redis backend
- [ ] Migrate `CurrentGame` in-memory state to Redis or database-backed store
- [ ] Implement distributed locks for game state mutations (Redisson or Spring Integration)
- [ ] Configure sticky sessions as fallback (if Redis unavailable)
- [ ] Session replication across nodes

### 21.3 Caching Layer
- [ ] Add Spring Cache with Redis backend
- [ ] Cache challenge data (read-heavy, write-infrequent)
- [ ] Cache user profiles and roles
- [ ] Cache i18n message bundles
- [ ] Implement cache invalidation on write operations
- [ ] Add cache hit/miss metrics

### 21.4 Database Optimization
- [ ] Connection pool tuning (HikariCP) for multi-node
- [ ] Read replica support for analytics queries
- [ ] Database migration for optimistic locking on game state
- [ ] Index optimization for high-query tables (game_result, participant_result)
- [ ] Query performance monitoring and slow query logging

### 21.5 Kubernetes Deployment
- [ ] Create Kubernetes manifests (Deployment, Service, Ingress, ConfigMap, Secret)
- [ ] Create Helm chart with configurable values (replicas, resources, database, Redis)
- [ ] Add horizontal pod autoscaler (HPA) based on CPU/memory/WebSocket connections
- [ ] Configure liveness and readiness probes (actuator endpoints)
- [ ] Document rolling update strategy with zero-downtime deployment
- [ ] Add PodDisruptionBudget for availability during node maintenance

### 21.6 Monitoring & Observability
- [ ] Add Micrometer metrics for Prometheus
- [ ] Expose: active games, connected WebSockets, questions served, API latency
- [ ] Create Grafana dashboard templates
- [ ] Implement distributed tracing (Micrometer Tracing + Zipkin/Jaeger)
- [ ] Structured JSON logging for log aggregation (ELK/Loki)
- [ ] Alert rules for critical metrics (game failures, high latency, node loss)

### Files to Create/Modify
- `init/RedisConfig.java` - Redis connection and Spring Session/Cache config
- `init/WebSocketConfig.java` - Switch to broker relay
- `preview/repository/CurrentGame.java` - Refactor to distributed store
- `k8s/` directory - Kubernetes manifests
- `helm/darkhold/` directory - Helm chart
- `grafana/` directory - Dashboard JSON templates
- `application-cluster.properties` - Clustering configuration profile
- Database migrations for optimistic locking columns

**Deliverable**: Multi-node deployment with Redis, Kubernetes manifests, Helm chart, and Grafana monitoring

---

## Milestone TC-1: Test Coverage - Security & Infrastructure

**Status**: Pending
**Focus**: Security-critical code with near-zero test coverage
**Coverage Target**: 51% -> 58% instruction, raise JaCoCo minimum from 35% to 45%
**Details**: See [TEST_COVERAGE_PLAN.md](TEST_COVERAGE_PLAN.md)

### TC-1.1 Security Configuration Tests
- [ ] Authentication flow tests (login success, failure, logout, remember-me)
- [ ] Authorization rule tests (admin-only endpoints, game-manager endpoints, public access)
- [ ] CSRF token validation tests (form submission, WebSocket handshake)
- [ ] Password encoding verification (BCrypt)
- [ ] Session management tests (concurrent sessions, session timeout)

### TC-1.2 Rate Limiting Tests
- [ ] PIN entry rate limit enforcement (5 attempts per 5-minute window)
- [ ] 15-minute block after threshold exceeded
- [ ] Rate limit reset after window expiry
- [ ] Different IP addresses tracked independently
- [ ] Edge cases: exactly at threshold, rapid successive attempts

### TC-1.3 WebSocket Configuration Tests
- [ ] STOMP endpoint registration verification
- [ ] WebSocket handshake with valid/invalid CSRF token
- [ ] Message broker topic configuration
- [ ] Allowed origins validation
- [ ] SockJS fallback behavior

### TC-1.4 File Upload & Exception Handling Tests
- [ ] File type validation (allowed: xlsx, png, jpg; rejected: exe, sh)
- [ ] File size limit enforcement
- [ ] Malicious filename sanitization
- [ ] GlobalExceptionHandler: 404, 500, validation errors, access denied
- [ ] Error response structure and HTTP status codes

### TC-1.5 Raise JaCoCo Threshold
- [ ] Update `staticCodeAnalysis.gradle`: minimum 0.35 -> 0.45
- [ ] Verify all tests pass with new threshold
- [ ] Update CI/CD pipeline if needed

**Deliverable**: ~53 new tests covering security infrastructure; JaCoCo minimum raised to 45%

---

## Milestone TC-2: Test Coverage - Core Service Layer

**Status**: Pending
**Focus**: Business logic services (largest gap, highest-value tests)
**Coverage Target**: 58% -> 67% instruction, raise JaCoCo minimum to 55%
**Depends on**: TC-1

### TC-2.1 ResultService Tests (analytics.service: 4% -> 65%)
- [ ] Save game results on game end
- [ ] Calculate participant statistics (score, accuracy, streak)
- [ ] Calculate question statistics (success rate, difficulty)
- [ ] CSV export formatting and content
- [ ] Handle games with 0 participants
- [ ] Handle questions with 0 answers

### TC-2.2 TeamService Tests (team.service: 7% -> 65%)
- [ ] Balanced team assignment (even distribution)
- [ ] Random team assignment (all players assigned)
- [ ] Manual team assignment
- [ ] Team score aggregation from member scores
- [ ] Team creation with custom names and colors
- [ ] Edge cases: 1 player, odd numbers, more teams than players

### TC-2.3 PracticeService Tests (practice.service: 11% -> 65%)
- [ ] Practice session creation (UUID-based, no PIN)
- [ ] Solo gameplay flow (question progression)
- [ ] Score tracking without leaderboard
- [ ] Session-based state management
- [ ] Practice mode with different question types

### TC-2.4 ChallengeService Tests (challenge.service: 15% -> 55%)
- [ ] Challenge CRUD operations
- [ ] Excel import (valid file, empty rows, invalid format, backward compat)
- [ ] JSON import/export round-trip
- [ ] Challenge duplication (deep copy verification)
- [ ] Question ordering and reordering
- [ ] Validation (missing title, too few questions)

### TC-2.5 UserService & SecurityService Tests (user.service: 24% -> 65%)
- [ ] User registration (valid data, duplicate email, weak password)
- [ ] Password change (correct old password, wrong old password)
- [ ] Role management (assign, revoke)
- [ ] User lookup by email and ID
- [ ] SecurityService: auto-login after registration

### TC-2.6 GameService Tests (game.service: 36% -> 65%)
- [ ] Game lifecycle: create, start, pause, resume, end
- [ ] Question advancement (next question, skip, last question)
- [ ] Player join and kick
- [ ] Streak tracking across questions
- [ ] GameCleanupScheduler: expired WAITING games, expired active games, active games untouched

### TC-2.7 Raise JaCoCo Threshold
- [ ] Update `staticCodeAnalysis.gradle`: minimum 0.45 -> 0.55
- [ ] Verify all tests pass with new threshold

**Deliverable**: ~100 new tests covering all service classes; JaCoCo minimum raised to 55%

---

## Milestone TC-3: Test Coverage - Controller Integration

**Status**: Pending
**Focus**: Undertested controllers with MockMvc integration tests
**Coverage Target**: 67% -> 73% instruction, raise JaCoCo minimum to 62%
**Depends on**: TC-2

### TC-3.1 AdminController Tests (10% -> 75%)
- [ ] GET user management page (admin access only)
- [ ] POST create user (valid data, validation errors)
- [ ] PUT update user roles
- [ ] Access denied for non-admin users
- [ ] User list pagination and filtering

### TC-3.2 ChallengeController Tests (18% -> 70%)
- [ ] GET view challenges page
- [ ] POST create challenge (valid, validation errors)
- [ ] GET/POST edit challenge
- [ ] POST duplicate challenge
- [ ] DELETE challenge (own, not owned, admin override)
- [ ] GET export CSV/JSON
- [ ] POST import Excel/JSON (valid, invalid, empty)

### TC-3.3 OptionsController Tests (52% -> 80%)
- [ ] GET options page with current settings
- [ ] POST update preferences (theme, sound, language)
- [ ] Validation of preference values

### TC-3.4 GameController Tests (53% -> 75%)
- [ ] POST publish game (creates game with PIN)
- [ ] POST join game (valid PIN, invalid PIN, expired PIN)
- [ ] Game control endpoints (pause, resume, skip, end)
- [ ] Access control (only moderator can control)

### TC-3.5 AccountController Tests (60% -> 85%)
- [ ] GET profile page
- [ ] POST update profile (name, email)
- [ ] Validation errors on invalid input

### TC-3.6 Raise JaCoCo Threshold
- [ ] Update `staticCodeAnalysis.gradle`: minimum 0.55 -> 0.62
- [ ] Verify all tests pass with new threshold

**Deliverable**: ~51 new tests for controller layer; JaCoCo minimum raised to 62%

---

## Milestone TC-4: Test Coverage - Data Layer & Models

**Status**: Pending
**Focus**: Entities, repositories, and models with low coverage
**Coverage Target**: 73% -> 78% instruction, raise JaCoCo minimum to 68%
**Depends on**: TC-3

### TC-4.1 Preview Entity & Repository Tests
- [ ] CurrentGameSession: constructor, getters/setters, state transitions
- [ ] CurrentGameSession: JSON field serialization/deserialization
- [ ] CurrentGame: CRUD operations via repository
- [ ] CurrentGame: PIN-scoped queries (find active by PIN)
- [ ] CurrentGameSessionRepository: custom query methods
- [ ] Concurrent access patterns

### TC-4.2 Game Entity Tests (56% -> 85%)
- [ ] Game entity: field validation, status enum transitions
- [ ] GameRepository: find by status, find by moderator
- [ ] GameMode enum coverage

### TC-4.3 User Entity Tests (64% -> 90%)
- [ ] User entity: required fields, role association
- [ ] DarkholdUserDetails: authorities mapping from roles
- [ ] Role entity: name constraints

### TC-4.4 Model & DTO Tests
- [ ] Options models (ChallengeInfo, ChallengeSummary): construction, fields
- [ ] Home models (GameInfo, PinValidationResponse): construction, fields
- [ ] Preview models (PreviewInfo, PublishInfo): construction, fields

### TC-4.5 Raise JaCoCo Threshold
- [ ] Update `staticCodeAnalysis.gradle`: minimum 0.62 -> 0.68
- [ ] Verify all tests pass with new threshold

**Deliverable**: ~51 new tests for data layer; JaCoCo minimum raised to 68%

---

## Milestone TC-5: Test Coverage - Branch Coverage & End-to-End

**Status**: Pending
**Focus**: Branch coverage (34% -> 70%), error paths, full integration tests
**Coverage Target**: 78% -> 85% instruction, 70% branch; JaCoCo minimum to 75%
**Depends on**: TC-4

### TC-5.1 Branch Coverage Improvement
- [ ] Parameterized tests for all enum-based switches (QuestionType, GameStatus, GameMode)
- [ ] Null-path testing across service methods
- [ ] Error branch testing (try/catch paths, validation failures)
- [ ] Conditional logic in ChallengeService Excel parser (all column combinations)
- [ ] Init package branch coverage (6% -> 60%): config conditional paths

### TC-5.2 Full Game Lifecycle Integration Tests
- [ ] Complete multiplayer game: create -> publish -> 3 players join -> play 5 questions -> end
- [ ] Practice mode: create -> start -> play all questions -> view results
- [ ] Team game: create -> configure teams -> play -> team results
- [ ] Game with all question types (MCQ, T/F, Type Answer, Poll)

### TC-5.3 WebSocket Integration Tests
- [ ] STOMP client connect and subscribe to game topic
- [ ] Send answer via STOMP and receive acknowledgment
- [ ] Receive scoreboard update after all players answer
- [ ] Handle disconnect and reconnect during game
- [ ] Multiple concurrent games on different topics

### TC-5.4 Error & Edge Case Tests
- [ ] Invalid PIN entry (wrong PIN, expired game, rate limited)
- [ ] Duplicate nickname in same game
- [ ] Answer after timer expires
- [ ] Concurrent answers from same player
- [ ] Game with 0 questions (validation)
- [ ] Maximum participants per game

### TC-5.5 Raise JaCoCo Threshold
- [ ] Update `staticCodeAnalysis.gradle`: minimum 0.68 -> 0.75
- [ ] Verify all tests pass with new threshold
- [ ] Add per-package minimum rules for critical packages (init, game.service, challenge.service)

**Deliverable**: ~63 new tests; 85% instruction coverage, 70% branch coverage; JaCoCo minimum at 75%

---

## Summary

### Phase 1: Kahoot Feature Parity (Completed)

| Milestone | Focus Area | Key Deliverable | Status |
|-----------|------------|-----------------|--------|
| 1 | Foundation | Production database support | ✅ COMPLETED |
| 2 | Question Types | T/F, Poll, Type Answer | ✅ COMPLETED |
| 3 | Question Editor | In-browser quiz creation | ✅ COMPLETED |
| 4 | Media | Images & videos in questions | ✅ COMPLETED |
| 5 | Game Experience | Polished live gameplay | ✅ COMPLETED |
| 6 | Concurrency | Multiple simultaneous games | ✅ COMPLETED |
| 7 | Analytics | Reports & game history | ✅ COMPLETED |
| 8 | Game Modes | Self-paced & practice modes | ✅ COMPLETED |
| 9 | Teams | Team-based gameplay | ✅ COMPLETED |
| 10 | UI/UX | Modern, accessible interface | ✅ COMPLETED |
| 11 | i18n | Multi-language support | ✅ COMPLETED |

### Phase 2: Enterprise-Ready AI Platform (Planned)

| Milestone | Focus Area | Key Deliverable | Addresses | Status |
|-----------|------------|-----------------|-----------|--------|
| 12 | Security & Scale | Production hardening | L11 (Test Coverage) | Pending |
| 13 | REST API | Documented API + Webhooks | L1, L8 | Pending |
| 14 | AI Generation | AI quiz from text/PDF/URL | L2 | Pending |
| 15 | SSO / Identity | OAuth2, OIDC, SAML 2.0 | L3 | Pending |
| 16 | Async Mode | Homework with deadlines | L4 | Pending |
| 17 | Analytics | Dashboards, charts, PDF reports | L5 | Pending |
| 18 | LMS Integration | SCORM, LTI 1.3, xAPI | L6 | Pending |
| 19 | White-Label | Custom branding | L7 | Pending |
| 20 | Ecosystem | Plugins, embeds, marketplace | L9 | Pending |
| 21 | Scaling | Redis, Kubernetes, Helm | L10 | Pending |

### Phase 3: Test Coverage (51% -> 85%)

| Milestone | Focus Area | Key Deliverable | Coverage Target | Status |
|-----------|------------|-----------------|:---------------:|--------|
| TC-1 | Security & Infrastructure | Security config, rate limiting, WebSocket tests | 51% -> 58% | Pending |
| TC-2 | Core Service Layer | ResultService, TeamService, GameService, etc. | 58% -> 67% | Pending |
| TC-3 | Controller Integration | Admin, Challenge, Options, Game controllers | 67% -> 73% | Pending |
| TC-4 | Data Layer & Models | Entities, repositories, DTOs | 73% -> 78% | Pending |
| TC-5 | Branch Coverage & E2E | Error paths, lifecycle integration, WebSocket | 78% -> 85% | Pending |

See [TEST_COVERAGE_PLAN.md](TEST_COVERAGE_PLAN.md) for per-package breakdown and detailed test scenarios.

---

## Recommended Execution Order

**Phase 1 milestones (1-11)** are completed. **Phase 2 milestones (12-21)** should be executed in this order based on dependencies and strategic value:

| Order | Milestone | Rationale |
|:-----:|:---------:|-----------|
| 1 | **M12** | Foundation hardening; already scoped; unblocks enterprise trust |
| 2 | **M13** | REST API enables M14, M16, M18, M20; transforms app into platform |
| 3 | **M14** | Closes #1 competitive gap; unique with local AI for air-gapped deployments |
| 4 | **M15** | Hard gate to enterprise adoption; Spring Security 6 has built-in support |
| 5 | **M16** | Doubles use-case surface area; teachers and trainers need assignments |
| 6 | **M17** | Premium differentiator; leverages existing analytics tables |
| 7 | **M18** | Unlocks education (44.9%) and enterprise training (20.3% CAGR) procurement |
| 8 | **M19** | Enterprise requirement; extends existing CSS custom properties |
| 9 | **M20** | Ecosystem play; community-driven growth and content network effects |
| 10 | **M21** | Large deployment support; requires stable platform from M12-M20 |

### Dependency Graph

```
M12 (Security) ─────────────────────────────────────────── M21 (Scaling)
  │
  └── M13 (REST API) ──┬── M14 (AI Generation)
                        ├── M15 (SSO)
                        ├── M16 (Async Mode) ──── M18 (LMS Integration)
                        ├── M17 (Analytics)
                        ├── M19 (White-Label)
                        └── M20 (Ecosystem)
```

---

## Contributing

Pick any milestone or task and submit a PR! See the main README for contribution guidelines.
