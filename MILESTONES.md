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

## Milestone 4: Media Support

**Focus**: Add images and videos to questions

### 4.1 Image Upload Infrastructure
- [ ] Create file upload service
- [ ] Configure storage location (local filesystem)
- [ ] Add image size/type validation
- [ ] Implement image compression/resize

### 4.2 Question Images
- [ ] Add `imageUrl` field to QuestionSet
- [ ] Update question editor with image upload
- [ ] Display images in game view
- [ ] Handle image loading states

### 4.3 Answer Images
- [ ] Add image fields to answer options
- [ ] Create image answer UI component
- [ ] Update game template for image answers

### 4.4 Video Support
- [ ] Add `videoUrl` field to QuestionSet
- [ ] Support YouTube embed URLs
- [ ] Support direct video file upload
- [ ] Create video player component for questions

### 4.5 Cloud Storage (Optional)
- [ ] Add S3 storage adapter
- [ ] Add configuration for cloud storage
- [ ] Implement CDN support for media

**Deliverable**: Rich media questions with images and videos

---

## Milestone 5: Enhanced Game Experience

**Focus**: Improve the live game playing experience

### 5.1 Scoring Improvements
- [ ] Implement answer streak bonus (2x, 3x multipliers)
- [ ] Add "Double Points" question option
- [ ] Show score change animation after each question
- [ ] Display rank change (+2, -1) after answers

### 5.2 Lobby Enhancements
- [ ] Show live participant count
- [ ] Display participant avatars/colors
- [ ] Add "Kick player" functionality for moderator
- [ ] Implement nickname validation (length, characters)
- [ ] Add basic profanity filter

### 5.3 Game Controls
- [ ] Add "Pause Game" button for moderator
- [ ] Add "Skip Question" option
- [ ] Add "End Game Early" with confirmation
- [ ] Implement "Extend Time" mid-question

### 5.4 Player Experience
- [ ] Show question number (3 of 10)
- [ ] Display time remaining prominently
- [ ] Add answer confirmation animation
- [ ] Show "Waiting for others" after answering

### 5.5 Results Screen
- [ ] Animate score reveals
- [ ] Show podium for top 3
- [ ] Display statistics (fastest answer, most streaks)

**Deliverable**: Polished, engaging game experience

---

## Milestone 6: Multiple Concurrent Games

**Focus**: Allow multiple games to run simultaneously

### 6.1 Session Isolation
- [ ] Refactor CurrentGame to be PIN-scoped
- [ ] Remove single-game assumptions from GameService
- [ ] Update PreviewService.getActiveChallenge() to handle multiple
- [ ] Ensure WebSocket messages are game-scoped

### 6.2 Game Management
- [ ] Create "My Active Games" dashboard for moderators
- [ ] Add game status indicators (Waiting, In Progress, Finished)
- [ ] Implement game timeout/auto-cleanup

### 6.3 PIN Management
- [ ] Ensure PIN uniqueness across active games
- [ ] Add PIN expiration
- [ ] Implement PIN retry limits

**Deliverable**: Host multiple simultaneous quiz sessions

---

## Milestone 7: Analytics & Reports

**Focus**: Track and report game results

### 7.1 Data Persistence
- [ ] Create GameResult entity for completed games
- [ ] Create ParticipantResult entity for individual scores
- [ ] Create QuestionResult entity for answer statistics
- [ ] Store results when game ends

### 7.2 Game History
- [ ] Create "Past Games" page
- [ ] Display game summary (date, participants, winner)
- [ ] View detailed results per game
- [ ] Filter games by challenge/date

### 7.3 Question Analytics
- [ ] Show correct/incorrect distribution per question
- [ ] Display average answer time
- [ ] Identify "hardest" questions
- [ ] Identify questions to review (low success rate)

### 7.4 Export Features
- [ ] Export game results to CSV
- [ ] Export game results to Excel
- [ ] Generate PDF summary report
- [ ] Email results to moderator

**Deliverable**: Comprehensive game analytics and reporting

---

## Milestone 8: Game Modes

**Focus**: Support different ways to play

### 8.1 Self-Paced Mode
- [ ] Create assignment/homework mode
- [ ] Generate shareable link (no PIN needed)
- [ ] Allow retakes with configurable attempts
- [ ] Set availability window (start/end date)

### 8.2 Practice Mode
- [ ] Allow solo play without moderator
- [ ] Show correct answer after each question
- [ ] No scoring pressure mode
- [ ] Instant feedback option

### 8.3 Challenge Mode
- [ ] Leaderboard across all attempts
- [ ] Best score tracking
- [ ] Time-based ranking

**Deliverable**: Flexible game modes for different use cases

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
| 1 | Foundation | Production database support | COMPLETED |
| 2 | Question Types | T/F, Poll, Type Answer | COMPLETED |
| 3 | Question Editor | In-browser quiz creation | COMPLETED |
| 4 | Media | Images & videos in questions | Pending |
| 5 | Game Experience | Polished live gameplay | Pending |
| 6 | Concurrency | Multiple simultaneous games | Pending |
| 7 | Analytics | Reports & game history | Pending |
| 8 | Game Modes | Self-paced & practice modes | Pending |
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
