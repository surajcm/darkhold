# Darkhold - Project Goals

A self-hosted quiz platform inspired by Kahoot. This document outlines the current state and the roadmap to reach feature parity with Kahoot.

## Current State Assessment

### Implemented Features

| Feature | Status | Notes |
|---------|--------|-------|
| User Registration & Login | Done | Spring Security with role-based access |
| Role Management | Done | ADMIN, GUEST, GAME_MANAGER, PARTICIPANT |
| Challenge Creation (Excel) | Done | Upload XLSX with questions |
| Multiple Choice Questions | Done | 4 answer options |
| Single/Multiple Correct Answers | Done | Via correctOptions field |
| Game PIN Generation | Done | 5-digit random PIN |
| Real-time Game Play | Done | WebSocket/STOMP |
| Participant Lobby | Done | Wait screen before game starts |
| Time-based Scoring | Done | 20 second countdown |
| Live Scoreboard | Done | After each question |
| Final Scores Display | Done | End of game summary |
| Docker Support | Done | Available on Docker Hub |
| Challenge Delete | Done | Delete created quizzes |
| View Own Challenges | Done | Filter by owner |

### Architecture

- **Backend**: Spring Boot 4.0
- **Database**: H2 (in-memory)
- **Frontend**: Thymeleaf + Bootstrap 5
- **Real-time**: WebSocket with STOMP
- **Build**: Gradle

---

## Roadmap to Feature Parity

### Phase 1: Core Enhancements (High Priority)

#### 1.1 Question Types
- [ ] True/False questions
- [ ] Type answer (text input)
- [ ] Poll (no correct answer)
- [ ] Slider (numeric range answer)

#### 1.2 Media Support
- [ ] Image upload for questions
- [ ] Video/YouTube embed support
- [ ] Image answers (not just text)

#### 1.3 In-App Question Editor
- [ ] Create questions without Excel
- [ ] Edit existing challenges
- [ ] Duplicate/clone challenges
- [ ] Drag-and-drop question reordering

#### 1.4 Configurable Time Limits
- [ ] Per-question time limit (5s, 10s, 20s, 30s, 60s, 90s, 120s)
- [ ] No time limit option for practice mode

#### 1.5 Production Database Support
- [ ] PostgreSQL support
- [ ] MySQL support
- [ ] Database configuration profiles

### Phase 2: Game Experience (Medium Priority)

#### 2.1 Points & Scoring
- [ ] Answer streak bonuses
- [ ] Configurable base points per question
- [ ] Double points option
- [ ] No points option (for practice)

#### 2.2 Game Modes
- [ ] Live game (current implementation)
- [ ] Self-paced mode (homework/assignment)
- [ ] Practice mode (solo play)

#### 2.3 Multiple Concurrent Games
- [ ] Support multiple active games simultaneously
- [ ] Game session isolation
- [ ] Unique PIN validation per session

#### 2.4 Enhanced Lobby
- [ ] Show participant count
- [ ] Kick participant option
- [ ] Lobby nickname validation
- [ ] Profanity filter for nicknames

#### 2.5 Game Controls
- [ ] Pause game
- [ ] Skip question
- [ ] End game early
- [ ] Extend question time mid-game

### Phase 3: Analytics & Reports (Medium Priority)

#### 3.1 Game History
- [ ] Store completed game results in database
- [ ] View past game sessions
- [ ] Participant scores history

#### 3.2 Question Analytics
- [ ] Correct/incorrect answer distribution per question
- [ ] Average answer time per question
- [ ] Identify difficult questions

#### 3.3 Export Reports
- [ ] Export game results to CSV/Excel
- [ ] PDF certificate for participants
- [ ] Email results to participants

### Phase 4: Team & Social Features (Lower Priority)

#### 4.1 Team Mode
- [ ] Create teams before game starts
- [ ] Team vs Team scoring
- [ ] Random team assignment

#### 4.2 Leaderboards
- [ ] Global leaderboard
- [ ] Per-challenge leaderboard
- [ ] Weekly/monthly rankings

#### 4.3 Challenge Library
- [ ] Public challenge sharing
- [ ] Search public challenges
- [ ] Clone public challenges

### Phase 5: UI/UX & Accessibility (Ongoing)

#### 5.1 Mobile Experience
- [ ] Fully responsive game interface
- [ ] Touch-optimized answer buttons
- [ ] PWA support for mobile

#### 5.2 Themes & Customization
- [ ] Dark/Light mode toggle
- [ ] Custom quiz themes
- [ ] Lobby background music
- [ ] Answer sound effects

#### 5.3 Accessibility
- [ ] ARIA labels
- [ ] Keyboard navigation
- [ ] Screen reader support
- [ ] Color blind friendly color schemes

#### 5.4 Internationalization
- [ ] Multi-language support
- [ ] RTL language support

### Phase 6: Infrastructure & Security (Ongoing)

#### 6.1 Security Hardening
- [ ] Rate limiting on PIN entry
- [ ] CSRF protection review
- [ ] Input validation enhancement
- [ ] Password strength requirements

#### 6.2 Scalability
- [ ] Redis for session storage
- [ ] Horizontal scaling support
- [ ] Load balancer configuration

#### 6.3 Monitoring
- [ ] Health check endpoints (actuator enhancements)
- [ ] Prometheus metrics
- [ ] Grafana dashboards

---

## Known Issues / TODOs in Code

These are existing TODOs found in the codebase:

1. `ChallengeController.java:64` - Change HTTP status code and give error message properly
2. `OptionsController.java:63` - Find a way to get all users bound
3. `GameService.java:31` - The saveAndGetAllParticipants method is marked as needed

---

## Quick Wins (Easy Implementation)

1. **Configurable game timer** - Add timer field to Challenge entity
2. **True/False questions** - Add question type field, handle 2 answers
3. **PostgreSQL support** - Add datasource configuration profiles
4. **Duplicate challenge** - Add clone endpoint in ChallengeController
5. **Show participant count in lobby** - Display count on publish page

---

## Comparison with Kahoot

| Feature | Kahoot | Darkhold |
|---------|--------|----------|
| Multiple choice | Yes | Yes |
| True/False | Yes | Planned |
| Type answer | Yes | Planned |
| Poll | Yes | Planned |
| Puzzle | Yes | Not planned |
| Image questions | Yes | Planned |
| Video questions | Yes | Planned |
| Self-paced mode | Yes | Planned |
| Team mode | Yes | Planned |
| Reports | Yes | Planned |
| Public library | Yes | Planned |
| Self-hosted | No | Yes |
| Free for unlimited players | No | Yes |
| Open source | No | Yes |

---

## Contributing

See [CONTRIBUTING.md](https://github.com/surajcm/darkhold/wiki) for guidelines.

Contributions are welcome for any of the roadmap items above!
