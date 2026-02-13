# Test Coverage Improvement Plan

## Current Status
- **Overall Instruction Coverage:** 43% (5,762 / 13,287)
- **Branch Coverage:** 28% (240 / 840)
- **Tests Passing:** 358 ✅
- **Minimum Required:** 35% ✅ (Currently meeting)

---

## Priority 1: Critical Controllers (< 10% Coverage)

### Analytics Controller (3% → Target: 70%)
**Files:** `AnalyticsController.java`
**Missing Tests:**
- GET `/game-result/{id}` - View game results
- GET `/game-result/{id}/export-csv` - CSV export
- Error handling for invalid game IDs

**Suggested Test Cases:**
```java
@Test
void testViewGameResult_Success()
@Test
void testViewGameResult_NotFound()
@Test
void testExportGameResultCSV_Success()
@Test
void testExportGameResultCSV_InvalidId()
```

### Score Controller (6% → Target: 70%)
**Files:** `ScoreController.java`
**Missing Tests:**
- POST `/scores/submit` - Submit player scores
- GET `/scores/leaderboard/{gameId}` - Get leaderboard
- WebSocket score updates

### Team Controller (4% → Target: 70%)
**Files:** `TeamController.java`
**Missing Tests:**
- POST `/team/create` - Create team
- GET `/team/{id}` - Get team details
- PUT `/team/{id}/assign-player` - Assign player to team

### Home Controller (7% → Target: 70%)
**Files:** `HomeController.java`
**Missing Tests:**
- GET `/` - Home page
- GET `/login` - Login page
- POST `/logout` - Logout

### Preview Controller (6% → Target: 70%)
**Files:** `PreviewController.java`
**Missing Tests:**
- GET `/preview/{id}` - Preview challenge
- POST `/preview/start` - Start preview session

---

## Priority 2: Service Layer (10-25% Coverage)

### Challenge Service (15% → Target: 60%)
**Files:** `ChallengeService.java`, `ChallengeServiceImpl.java`
**Missing Tests:**
- `deleteChallenge(Long id)` - Delete with dependency check
- `updateChallenge(...)` - Update existing challenge
- `validateChallengeData(...)` - Validation logic
- Excel import edge cases (empty rows, invalid format)

### Team Service (7% → Target: 60%)
**Files:** `TeamService.java`, `TeamServiceImpl.java`
**Missing Tests:**
- `assignPlayersToTeams(...)` - Team assignment algorithms
- `calculateTeamScores(...)` - Team score calculation
- `redistributePlayers(...)` - Player rebalancing
- Random vs manual assignment modes

### User Service (25% → Target: 70%)
**Files:** `UserService.java`, `UserServiceImpl.java`
**Missing Tests:**
- `updateUserPhoto(...)` - Photo upload validation
- `changePassword(...)` - Password change logic
- `deleteUser(...)` - Cascade deletion
- Email uniqueness validation

### Practice Service (11% → Target: 60%)
**Files:** `PracticeService.java`
**Missing Tests:**
- Practice session creation
- Solo gameplay flow
- Score tracking without leaderboard

---

## Priority 3: Game Flow & Integration

### Game Service (36% → Target: 75%)
**Files:** `GameService.java`, `GameServiceImpl.java`
**Focus Areas:**
- Complete game lifecycle (start → play → end)
- WebSocket message handling
- Multi-player synchronization
- Question advancement logic

### Analytics Service (4% → Target: 60%)
**Files:** `AnalyticsService.java`
**Missing Tests:**
- `generateGameReport(...)` - Statistics calculation
- `calculatePlayerStats(...)` - Individual metrics
- `getQuestionAnalytics(...)` - Question difficulty analysis
- CSV export formatting

---

## Implementation Strategy

### Week 1: Controller Layer
**Focus:** Critical controllers (Analytics, Score, Team, Home, Preview)
- Write integration tests using `@SpringBootTest` and `MockMvc`
- Mock service layer dependencies
- Test HTTP responses, status codes, view names
- **Estimated Coverage Gain:** +10-12%

### Week 2: Service Layer
**Focus:** Challenge, Team, User services
- Unit tests with mocked repositories
- Edge case testing (null inputs, invalid data)
- Business logic validation
- **Estimated Coverage Gain:** +8-10%

### Week 3: Game Flow & WebSocket
**Focus:** Game service and real-time features
- Integration tests for WebSocket endpoints
- Game state transition testing
- Concurrent player handling
- **Estimated Coverage Gain:** +6-8%

### Week 4: Analytics & Reports
**Focus:** Analytics service and data export
- Report generation testing
- CSV export validation
- Statistical calculation accuracy
- **Estimated Coverage Gain:** +4-6%

---

## Quick Wins (Easy 5% Boost)

### 1. DTOs and Entities
Most DTOs/Entities already have constructors and getters. Add simple tests:
```java
@Test
void testConstructorAndGetters() {
    var dto = new GameResultDTO(1L, "Test Game", 10);
    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getName()).isEqualTo("Test Game");
}
```

### 2. Validators
Add validation failure tests:
```java
@Test
void testEmailValidator_Invalid() {
    assertFalse(validator.isValid("invalid-email", context));
}
```

### 3. Exception Handlers
Test `GlobalExceptionHandler`:
```java
@Test
void testHandleNotFoundException() {
    var response = handler.handleNotFound(new NotFoundException());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
}
```

---

## Test Patterns to Follow

### Controller Tests
```java
@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    void testViewGameResult() throws Exception {
        when(analyticsService.getGameResult(1L))
            .thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("gameresult"))
            .andExpect(model().attributeExists("gameResult"));
    }
}
```

### Service Tests
```java
@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @Test
    void testCreateTeam() {
        Team team = new Team("Red Team");
        when(teamRepository.save(any())).thenReturn(team);

        Team result = teamService.createTeam("Red Team");

        assertThat(result.getName()).isEqualTo("Red Team");
        verify(teamRepository).save(any());
    }
}
```

---

## Coverage Goals

| Phase | Target | Expected Timeline |
|-------|--------|-------------------|
| **Current** | 43% | - |
| **Phase 1** | 53% | Week 1-2 |
| **Phase 2** | 63% | Week 3-4 |
| **Phase 3** | 70% | Week 5-6 |
| **Stretch Goal** | 75% | Week 7-8 |

---

## Monitoring Progress

### Run Coverage Report
```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

### Check Minimum Threshold
```bash
./gradlew jacocoTestCoverageVerification
```

### CI/CD Integration
- Add JaCoCo report to GitHub Actions
- Fail PR if coverage drops below threshold
- Display coverage badge in README

---

## Notes

- Focus on **business logic** over trivial getters/setters
- Prioritize **controller layer** for immediate impact
- Test **edge cases** and **error scenarios**
- Aim for **60-70% realistic coverage** (100% is often unnecessary)
- Write **maintainable tests** that document expected behavior
