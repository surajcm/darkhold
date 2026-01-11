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

## Milestone 9: Team Features

**Focus**: Enable team-based gameplay

### 9.1 Team Creation
- [ ] Create team before game starts
- [ ] Random team assignment option
- [ ] Custom team names/colors
- [ ] Balance teams by participant count

### 9.2 Team Scoring
- [ ] Aggregate team scores
- [ ] Show team leaderboard
- [ ] Display individual contribution to team

### 9.3 Team UI
- [ ] Team lobby view
- [ ] Team vs Team results screen
- [ ] Team celebration animations

**Deliverable**: Collaborative team gameplay

---

## Milestone 10: UI/UX Polish

**Focus**: Modern, accessible interface

### 10.1 Responsive Design
- [ ] Audit mobile game experience
- [ ] Optimize touch targets for answers
- [ ] Test on various screen sizes
- [ ] Add PWA manifest for mobile install

### 10.2 Theming
- [ ] Implement dark/light mode toggle
- [ ] Create theme system (CSS variables)
- [ ] Allow custom quiz colors
- [ ] Add lobby background options

### 10.3 Accessibility
- [ ] Add ARIA labels throughout
- [ ] Ensure keyboard navigation works
- [ ] Test with screen readers
- [ ] Implement high contrast mode
- [ ] Add colorblind-friendly palettes

### 10.4 Sound & Animation
- [ ] Add optional sound effects
- [ ] Implement countdown sounds
- [ ] Add victory/defeat sounds
- [ ] Create smooth transitions

**Deliverable**: Polished, accessible user experience

---

## Milestone 11: Internationalization

**Focus**: Multi-language support

### 11.1 Framework Setup
- [ ] Configure Spring MessageSource
- [ ] Extract all UI strings to messages.properties
- [ ] Create language selector component
- [ ] Store user language preference

### 11.2 Translations
- [ ] Complete English (en) base
- [ ] Add Spanish (es) translation
- [ ] Add French (fr) translation
- [ ] Add German (de) translation
- [ ] Community contribution guidelines for translations

### 11.3 RTL Support
- [ ] Add RTL CSS styles
- [ ] Test Arabic/Hebrew layouts
- [ ] Handle mixed LTR/RTL content

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

## Summary

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
| 9 | Teams | Team-based gameplay | Pending |
| 10 | UI/UX | Modern, accessible interface | Pending |
| 11 | i18n | Multi-language support | Pending |
| 12 | Security & Scale | Production hardening | Pending |

---

## Recommended Implementation Order

**Suggested sequence based on value and dependencies:**

1. **Milestone 1** - Foundation (required for everything)
2. **Milestone 3** - Question Editor (high user value)
3. **Milestone 2** - Question Types (expands functionality)
4. **Milestone 5** - Game Experience (polish current flow)
5. **Milestone 6** - Concurrent Games (scalability)
6. **Milestone 4** - Media Support (rich content)
7. **Milestone 7** - Analytics (insights)
8. **Milestone 8** - Game Modes (flexibility)
9. **Milestone 10** - UI/UX (polish)
10. **Milestone 9** - Teams (advanced feature)
11. **Milestone 11** - i18n (reach)
12. **Milestone 12** - Security & Scale (production)

---

## Contributing

Pick any milestone or task and submit a PR! See the main README for contribution guidelines.
