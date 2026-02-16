# CSRF Protection Audit Report

**Date**: 2026-02-16
**Status**: 🔴 **CRITICAL VULNERABILITY FOUND**

## Executive Summary

CSRF protection is **completely disabled** in the application. This is a critical security vulnerability that allows attackers to perform unauthorized actions on behalf of authenticated users.

## Current Status

### SecurityConfig.java (Line 36)
```java
http.csrf(AbstractHttpConfigurer::disable);  // TODO: we need to enable CSRF
```

**Risk Level**: 🔴 **CRITICAL**

## Vulnerable Endpoints

### State-Changing HTTP Endpoints (35+ found)

#### Challenge Management (10 endpoints)
- `POST /options` - Navigate to options
- `POST /upload_challenge` - Upload quiz
- `DELETE /delete_challenge` - Delete quiz ⚠️ High Risk
- `POST /save_challenge` - Create quiz
- `POST /update_challenge/{id}` - Update quiz
- `POST /duplicate_challenge/{id}` - Duplicate quiz
- `POST /import_challenge/json` - Import quiz
- `POST /questions` - Add question
- `PUT /questions/{id}` - Update question
- `DELETE /questions/{id}` - Delete question ⚠️ High Risk

#### Game Management (8 endpoints)
- `POST /interstitial` - Game interstitial
- `POST /question` - Load question
- `POST /final` - Final scores
- `POST /game` - Start game
- `POST /answer/` - Submit answer
- `POST /validate_answer/` - Validate answer
- `POST /scoreboard` - Show scoreboard
- `POST /publish` - Publish game

#### User Management (5 endpoints)
- `POST /registration` - User registration ⚠️ High Risk
- `POST /logme` - Login
- `POST /logmein` - Login alternative
- `POST /users/save` - Save user ⚠️ High Risk
- `POST /users/check_email` - Check email

#### Team Management (2 endpoints)
- `POST /teams/create` - Create teams
- `POST /teams/assign` - Assign players

### WebSocket Endpoints (12 found)

**Endpoint**: `/darkhold-websocket` (SockJS)

#### Message Mappings
- `@MessageMapping("/user")` - Join game
- `@MessageMapping("/start")` - Start game ⚠️ High Risk
- `@MessageMapping("/question_fetch")` - Fetch question
- `@MessageMapping("/fetch_scores")` - Fetch scores
- `@MessageMapping("/pause_game")` - Pause game ⚠️ Moderator action
- `@MessageMapping("/resume_game")` - Resume game ⚠️ Moderator action
- `@MessageMapping("/skip_question")` - Skip question ⚠️ Moderator action
- `@MessageMapping("/end_game_early")` - End game ⚠️ Moderator action
- `@MessageMapping("/kick_player")` - Kick player ⚠️ Moderator action
- `@MessageMapping("/participant_count")` - Get count
- `@MessageMapping("/team/assign")` - Assign to team
- `@MessageMapping("/team/list")` - Get teams
- `@MessageMapping("/team/scores")` - Get team scores

## Attack Scenarios

### Scenario 1: Quiz Deletion
Attacker sends victim (authenticated moderator):
```html
<img src="http://darkhold.com/delete_challenge?id=123" />
```
Result: Quiz deleted without user consent

### Scenario 2: Game Disruption
Attacker creates malicious page with:
```javascript
// WebSocket CSRF - kick all players
ws.send('/app/end_game_early', pin);
```
Result: Game terminated without moderator action

### Scenario 3: Unauthorized User Creation
```html
<form action="http://darkhold.com/registration" method="POST">
  <input name="email" value="attacker@evil.com" />
  <input name="password" value="hacked123" />
</form>
<script>document.forms[0].submit();</script>
```
Result: Unauthorized account created

## Recommendations

### Priority 1: Enable CSRF Protection (CRITICAL)

1. **Remove CSRF disable** in SecurityConfig
2. **Add CSRF token repository**
3. **Configure WebSocket CSRF**
4. **Update all forms** to include CSRF token
5. **Update AJAX requests** to send CSRF token

### Priority 2: Token Implementation

#### For Thymeleaf Forms
```html
<form th:action="@{/endpoint}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
</form>
```

#### For AJAX Requests
```javascript
var token = document.querySelector('meta[name="_csrf"]').content;
var header = document.querySelector('meta[name="_csrf_header"]').content;
xhr.setRequestHeader(header, token);
```

#### For WebSocket/STOMP
```java
registry.addEndpoint("/darkhold-websocket")
    .setAllowedOrigins("*")  // Configure properly
    .withSockJS();
```

### Priority 3: Testing

- Test all 35+ HTTP endpoints with CSRF enabled
- Test all 12 WebSocket message mappings
- Verify tokens are properly validated
- Test AJAX form submissions
- Test file uploads with CSRF

## Impact Assessment

**Without CSRF Protection:**
- ✅ Attackers can delete quizzes
- ✅ Attackers can create/modify users
- ✅ Attackers can disrupt live games
- ✅ Attackers can kick players
- ✅ Attackers can steal moderator privileges

**With CSRF Protection:**
- ✅ All state-changing operations require valid token
- ✅ Tokens tied to user session
- ✅ Prevents unauthorized actions
- ✅ Meets OWASP security standards

## Timeline

1. **Immediate** (Today): Enable CSRF in SecurityConfig
2. **Day 1**: Update all Thymeleaf templates
3. **Day 2**: Update all JavaScript/AJAX requests
4. **Day 3**: Test all endpoints
5. **Day 4**: Security testing & validation

## References

- [OWASP CSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [Spring Security CSRF Documentation](https://docs.spring.io/spring-security/reference/features/exploits/csrf.html)
- [Spring WebSocket Security](https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html)
