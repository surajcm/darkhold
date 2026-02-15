# Contributing

We are open to, and grateful for, any contributions made by the community.

## Contributor License Agreement

By contributing you agree to the [LICENSE](LICENSE) of this repository.

## Contributor Code of Conduct

By contributing you agree to respect the [Code of Conduct](CODE_OF_CONDUCT.md) of this repository.

## Commit Messages

Please follow [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/)

## Testing

Please update the tests to reflect your code changes. Pull requests will not be accepted if they are failing on GitHub actions.

## 🌍 Contributing Translations

Darkhold supports multiple languages, and we welcome translation contributions! Here's how you can help:

### Current Translation Status

| Language | Status | Progress |
|----------|--------|----------|
| English (en) | ✅ Complete | 427/427 messages |
| Spanish (es) | ✅ Complete | 427/427 messages |
| French (fr) | ⚠️ Partial | 55/427 messages (13%) |
| German (de) | ⚠️ Partial | 55/427 messages (13%) |

**We especially need help with French and German translations!**

### How to Contribute Translations

#### Option 1: Complete/Update an Existing Language

1. **Fork the repository**
2. **Find the translation file** in `src/main/resources/`
   - French: `messages_fr.properties`
   - German: `messages_de.properties`
   - Or create a new one: `messages_XX.properties` (where XX is the language code)

3. **Translate the messages**
   - Use `messages.properties` (English) as the source
   - Use `messages_es.properties` (Spanish) as a reference for structure
   - **Keep keys in English**, only translate values after the `=` sign
   - **Don't translate**: `app.name=Darkhold`, technical terms (PIN, URL, Excel, CSV)
   - **Use formal tone**: "vous" (French), "Sie" (German), "usted" (Spanish)

4. **Example**:
   ```properties
   # English (messages.properties)
   game.title=Game
   game.waiting=Waiting for players...

   # French (messages_fr.properties)
   game.title=Jeu
   game.waiting=En attente des joueurs...

   # German (messages_de.properties)
   game.title=Spiel
   game.waiting=Warten auf Spieler...
   ```

5. **Test your translations**:
   ```bash
   ./gradlew bootRun
   # Navigate to the app and change language in the navbar
   ```

6. **Submit a Pull Request**
   - Title: `i18n: Complete French translations` or `i18n: Add Portuguese translations`
   - Description: Mention which language and how many messages were translated

#### Option 2: Start a New Language

1. Copy `messages_es.properties` to `messages_XX.properties` (XX = your language code)
2. Update the header comment to reflect your language
3. Translate all 427 messages
4. Update `language-manager.js` to add your language to the selector:
   ```javascript
   // In src/main/resources/static/scripts/language-manager.js
   // Add your language to the available languages
   ```
5. Submit a PR!

#### Translation Guidelines

- **Natural & Idiomatic**: Translate meaning, not word-for-word
- **Consistent Terminology**: Use the same translation for repeated terms
- **Context Matters**: Consider where the message appears (button, heading, error, etc.)
- **Test in UI**: Always test your translations in the actual interface
- **Character Limits**: Some UI elements have space constraints, keep translations concise
- **Preserve Formatting**: Keep line breaks (`\n`), placeholders, and HTML entities

#### Questions?

Open an issue with the `i18n` label if you have questions about translations!

### Priority Languages

We're particularly looking for native speakers to help with:
- 🇫🇷 **French** (needs 372 more messages)
- 🇩🇪 **German** (needs 372 more messages)
- 🇵🇹 **Portuguese** (new language)
- 🇯🇵 **Japanese** (new language)
- 🇨🇳 **Chinese** (new language)

Thank you for helping make Darkhold accessible to more people! 🌍
