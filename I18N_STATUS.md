# Internationalization (i18n) Status

Darkhold supports multiple languages. This document tracks translation progress and provides guidance for contributors.

## Current Status

| Language | Code | Status | Progress | Contributor Needed? |
|----------|------|--------|----------|---------------------|
| English | `en` | ✅ Complete | 427/427 (100%) | No |
| Spanish | `es` | ✅ Complete | 427/427 (100%) | No |
| French | `fr` | ✅ Complete | 427/427 (100%) | No |
| German | `de` | ✅ Complete | 427/427 (100%) | No |

**Last Updated**: 2026-02-16

## Quick Stats

- **Total message keys**: 427
- **Completed languages**: 4 (English, Spanish, French, German)
- **Languages needing help**: 0
- **Framework**: Spring MessageSource with `.properties` files

## For Contributors

### Check Translation Status

Use the helper script to check progress:

```bash
./translation_helper.sh fr   # Check French
./translation_helper.sh de   # Check German
```

### Files to Translate

Translation files are located in `src/main/resources/`:

- `messages.properties` - English (source)
- `messages_es.properties` - Spanish (complete)
- `messages_fr.properties` - French (needs completion)
- `messages_de.properties` - German (needs completion)

### Translation Guidelines

1. **Use formal tone**: "vous" (French), "Sie" (German)
2. **Don't translate**:
   - Brand name: `app.name=Darkhold`
   - Technical terms: PIN, URL, Excel, CSV, YouTube
   - Property keys (left side of `=`)

3. **Keep property keys in English**, only translate values:
   ```properties
   # ✅ Correct
   game.title=Jeu
   
   # ❌ Wrong
   jeu.titre=Jeu
   ```

4. **Test your translations**:
   ```bash
   ./gradlew bootRun
   # Change language in navbar to verify
   ```

### How to Contribute

See detailed instructions in [CONTRIBUTING.md](CONTRIBUTING.md#-contributing-translations)

## Priority Languages

Help us reach more users! We're looking for native speakers to contribute new languages:

1. 🇵🇹 **Portuguese** - New language (0% - needs 427 messages)
2. 🇯🇵 **Japanese** - New language (0% - needs 427 messages)
3. 🇨🇳 **Chinese** - New language (0% - needs 427 messages)
4. 🇮🇹 **Italian** - New language (0% - needs 427 messages)
5. 🇳🇱 **Dutch** - New language (0% - needs 427 messages)

## Technical Details

### Message Categories

The 427 messages cover:
- Navigation & UI (nav.*, button.*)
- Authentication (login.*, registration.*)
- Game play (game.*, scoreboard.*, finalscore.*)
- Challenge management (challenge.*)
- Analytics (gameresult.*, pastgames.*, activegames.*)
- Admin panel (admin.*, user.*)
- Common messages (message.*, error.*, language.*)

### Adding a New Language

1. Create `messages_XX.properties` (XX = language code)
2. Copy structure from `messages_es.properties`
3. Translate all 427 messages
4. Update `language-manager.js` to add language to selector
5. Submit PR with title: `i18n: Add [Language] translations`

### Testing

All templates use Thymeleaf's `#{key}` expression:
```html
<!-- In HTML templates -->
<h1 th:text="#{home.title}">Title</h1>
```

Language switching is handled by `language-manager.js` with localStorage persistence.

## Questions?

Open an issue with the `i18n` label for translation-related questions!
