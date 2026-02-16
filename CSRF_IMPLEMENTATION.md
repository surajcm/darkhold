# CSRF Protection Implementation Guide

**Status**: ✅ Enabled (Milestone 12.1 - Security Hardening)
**Date**: 2026-02-16

## Overview

Cross-Site Request Forgery (CSRF) protection is now **ENABLED** in Darkhold. This prevents attackers from performing unauthorized actions on behalf of authenticated users.

## What Changed

### 1. SecurityConfig.java - CSRF Enabled

**Before** (VULNERABLE):
```java
http.csrf(AbstractHttpConfigurer::disable);  // TODO: we need to enable CSRF
```

**After** (PROTECTED):
```java
CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
requestHandler.setCsrfRequestAttributeName("_csrf");

http.csrf((csrf) -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .csrfTokenRequestHandler(requestHandler)
        .ignoringRequestMatchers("/h2-console/**")  // Dev only
);
```

### 2. Token Storage

- **Method**: Cookie-based (`XSRF-TOKEN`)
- **HttpOnly**: False (allows JavaScript access)
- **SameSite**: Lax (default, prevents CSRF)
- **Path**: `/`
- **Validation**: Automatic by Spring Security

### 3. WebSocket CSRF Protection

**WebSocketConfig.java** updated with documentation:
- CSRF token must be sent in CONNECT frame
- Token validated by Spring Security automatically
- Uses same `XSRF-TOKEN` cookie

## Implementation Details

### For HTTP Endpoints (Forms)

#### Thymeleaf Forms (Automatic)
Spring Security automatically adds CSRF token to Thymeleaf forms:
```html
<form th:action="@{/endpoint}" method="post">
    <!-- CSRF token automatically added by Thymeleaf -->
    <button type="submit">Submit</button>
</form>
```

#### Manual HTML Forms
For non-Thymeleaf forms, use hidden input:
```html
<form action="/endpoint" method="post">
    <input type="hidden" name="_csrf" value="[token]" />
    <button type="submit">Submit</button>
</form>
```

### For AJAX Requests

#### Using CsrfManager (Recommended)
```javascript
// Include csrf-manager.js in page
<script src="/scripts/csrf-manager.js"></script>

// For XMLHttpRequest
let xhr = new XMLHttpRequest();
xhr.open('POST', '/endpoint', true);
CsrfManager.addTokenToXHR(xhr);
xhr.send(data);

// For Fetch API
fetch('/endpoint', {
    method: 'POST',
    headers: CsrfManager.addTokenToFetch({
        'Content-Type': 'application/json'
    }),
    body: JSON.stringify(data)
});
```

#### Manual AJAX
```javascript
// Read token from cookie
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
}

const token = getCookie('XSRF-TOKEN');
xhr.setRequestHeader('X-XSRF-TOKEN', token);
```

### For WebSocket/STOMP Connections

#### Pattern (All WebSocket connections must follow this)
```javascript
// Include csrf-manager.js first
<script src="/scripts/csrf-manager.js"></script>

// In your connection code
let socket = new SockJS('/darkhold-websocket');
stompClient = Stomp.over(socket);

// Get CSRF headers
let headers = {};
if (typeof CsrfManager !== 'undefined') {
    headers = CsrfManager.getHeadersForStomp();
}

// Connect with CSRF token
stompClient.connect(headers, function (frame) {
    console.log('Connected: ' + frame);
    // ... rest of connection logic
});
```

## Files Updated

### Core Security
- ✅ `SecurityConfig.java` - CSRF enabled with cookie repository
- ✅ `WebSocketConfig.java` - Added CSRF documentation

### JavaScript Utilities
- ✅ `csrf-manager.js` - NEW: CSRF token manager
  - Auto-protects forms on page load
  - Provides helpers for AJAX/WebSocket
  - Reads from `XSRF-TOKEN` cookie

### Updated Scripts (WebSocket CSRF)
- ✅ `game-wait-scripts.js` - Player lobby connection
- ⏳ `game-scripts.js` - 2 connections (showScoreboard, connect)
- ⏳ `scoreboard-scripts.js` - 2 connections
- ⏳ `publish-scripts.js` - 2 connections
- ⏳ `question-scripts.js` - 1 connection

### HTTP AJAX Updates
- ✅ `home-scripts.js` - PIN validation (enterGame)
- ⏳ Other AJAX calls need review

## Testing Checklist

### HTTP Endpoints
- [ ] Registration form submission
- [ ] Login form submission
- [ ] Quiz creation
- [ ] Quiz deletion
- [ ] Question CRUD operations
- [ ] User management operations
- [ ] Team creation/assignment

### AJAX Requests
- [x] PIN validation (`/enterGame`)
- [ ] Email validation
- [ ] File uploads
- [ ] Challenge import/export

### WebSocket Operations
- [x] Player join lobby (`/app/user`)
- [ ] Game start trigger (`/app/start`)
- [ ] Question fetch (`/app/question_fetch`)
- [ ] Answer submission (via HTTP, not WebSocket)
- [ ] Pause/Resume game
- [ ] Skip question
- [ ] End game early
- [ ] Kick player
- [ ] Team assignment

## Migration Path for Developers

### Step 1: Add csrf-manager.js to page
```html
<script type="text/javascript" src="/scripts/csrf-manager.js"></script>
```

### Step 2: Update AJAX calls
Before:
```javascript
xhr.open('POST', '/endpoint', true);
xhr.send(data);
```

After:
```javascript
xhr.open('POST', '/endpoint', true);
CsrfManager.addTokenToXHR(xhr);  // Add this line
xhr.send(data);
```

### Step 3: Update WebSocket connections
Before:
```javascript
stompClient.connect({}, function (frame) {
    // connection logic
});
```

After:
```javascript
let headers = CsrfManager ? CsrfManager.getHeadersForStomp() : {};
stompClient.connect(headers, function (frame) {
    // connection logic
});
```

## Troubleshooting

### Issue: CSRF Token Missing
**Symptoms**: 403 Forbidden on POST/PUT/DELETE
**Solution**: Ensure csrf-manager.js is loaded before making requests

### Issue: WebSocket Connection Fails
**Symptoms**: WebSocket fails to connect, 403 in network tab
**Solution**: Add CSRF headers to STOMP connect() call

### Issue: Form Submission Fails
**Symptoms**: 403 on form submit
**Solution**:
1. For Thymeleaf: Use `th:action` instead of plain `action`
2. For HTML: Add hidden `_csrf` input field

### Issue: Token Expired
**Symptoms**: Intermittent 403 errors
**Solution**: Call `CsrfManager.refresh()` after long idle periods

## H2 Console (Development)

H2 console is **excluded** from CSRF protection:
```java
.ignoringRequestMatchers("/h2-console/**")
```

**Production**: Remove H2 console entirely or secure with separate auth.

## Security Best Practices

✅ **DO**:
- Use `th:action` for all Thymeleaf forms
- Include csrf-manager.js on all pages with AJAX
- Add CSRF headers to all WebSocket connections
- Test CSRF protection in development

❌ **DON'T**:
- Disable CSRF globally
- Skip CSRF on state-changing operations
- Expose CSRF tokens in URLs
- Use GET for state-changing operations

## Performance Impact

- **Minimal**: Token stored in cookie, validated once per request
- **No database lookups**: Token validation is cryptographic
- **Caching**: Works normally with CSRF enabled
- **WebSocket**: Single token validation on CONNECT

## Compliance

✅ **OWASP Top 10**: Addresses A01:2021 - Broken Access Control
✅ **CWE-352**: Cross-Site Request Forgery
✅ **PCI DSS**: Requirement 6.5.9
✅ **GDPR**: Protects user actions and data integrity

## Next Steps

1. **Complete WebSocket Updates**: Update remaining 7 WebSocket connections
2. **Test All Endpoints**: Comprehensive testing of 35+ HTTP endpoints
3. **Update Documentation**: Add CSRF examples to API docs
4. **Audit Templates**: Ensure all forms use Thymeleaf `th:action`
5. **Security Testing**: Attempt CSRF attacks to verify protection

## References

- [Spring Security CSRF](https://docs.spring.io/spring-security/reference/features/exploits/csrf.html)
- [OWASP CSRF Prevention](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [WebSocket Security](https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html)

---

**Last Updated**: 2026-02-16
**Status**: 🟡 Partially Complete - Core protection enabled, frontend updates in progress
