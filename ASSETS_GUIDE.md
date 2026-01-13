# Assets Guide - Milestone 10: UI/UX Polish

## Overview
This guide will help you source sound effects and generate PWA icons for the Darkhold Quiz application.

---

## 1. Sound Effects (7 files needed)

### Required Directory
Create: `/src/main/resources/static/sounds/`

```bash
mkdir -p src/main/resources/static/sounds
```

### Sound Files Needed

#### 1. **correct.mp3** - Success chime
- **Description**: Positive, bright success sound
- **Duration**: 0.5-1 second
- **Recommended sources**:
  - freesound.org: Search "correct answer chime"
  - zapsplat.com: "UI Success" category
  - mixkit.co: "Game Success Sound"
- **Example**: https://freesound.org/people/LittleRobotSoundFactory/sounds/270319/

#### 2. **incorrect.mp3** - Error buzz
- **Description**: Short negative feedback sound
- **Duration**: 0.3-0.8 seconds
- **Recommended sources**:
  - freesound.org: Search "error buzz"
  - zapsplat.com: "UI Error" category
- **Example**: https://freesound.org/people/distillerystudio/sounds/327736/

#### 3. **tick.mp3** - Clock tick
- **Description**: Subtle clock tick sound (optional - currently commented out)
- **Duration**: 0.1-0.2 seconds
- **Recommended sources**:
  - freesound.org: Search "clock tick subtle"
  - zapsplat.com: "Clock Tick" category
- **Note**: This is commented out in code as it may be annoying every second

#### 4. **countdown.mp3** - Final 5 seconds warning
- **Description**: Urgent beep or alert sound
- **Duration**: 0.5-1 second
- **Recommended sources**:
  - freesound.org: Search "countdown beep"
  - zapsplat.com: "Alert Beep" category
- **Example**: https://freesound.org/people/rhodesmas/sounds/342756/

#### 5. **victory.mp3** - Win celebration
- **Description**: Triumphant fanfare or celebration
- **Duration**: 2-4 seconds
- **Recommended sources**:
  - freesound.org: Search "victory fanfare"
  - zapsplat.com: "Victory Jingle" category
  - mixkit.co: "Win Sound Effect"
- **Example**: https://freesound.org/people/FunWithSound/sounds/456966/

#### 6. **defeat.mp3** - Game over
- **Description**: Disappointed or neutral game over sound
- **Duration**: 1-2 seconds
- **Recommended sources**:
  - freesound.org: Search "game over"
  - zapsplat.com: "Game Over" category
- **Example**: https://freesound.org/people/Bertrof/sounds/351565/

#### 7. **join.mp3** - Player joined lobby
- **Description**: Subtle notification ping
- **Duration**: 0.3-0.6 seconds
- **Recommended sources**:
  - freesound.org: Search "notification ping"
  - zapsplat.com: "UI Notification" category
- **Example**: https://freesound.org/people/AdamWeeden/sounds/401748/

### Quick Download Guide (freesound.org)

1. Go to https://freesound.org
2. Create free account (required for downloads)
3. Search for sound type (e.g., "correct answer")
4. Filter by:
   - **License**: CC0 (public domain) or CC-BY
   - **Duration**: < 2 seconds (for most sounds)
   - **File type**: MP3 or WAV
5. Download and rename to match filenames above
6. If WAV, convert to MP3 using:
   ```bash
   # Using ffmpeg (install via brew/apt)
   ffmpeg -i sound.wav -codec:a libmp3lame -b:a 128k sound.mp3
   ```

### Alternative: Use Free Sound Libraries

**Option 1: Zapsplat.com** (requires free account)
- High-quality professional sounds
- Download pre-packaged UI sound packs
- Already in MP3 format

**Option 2: Mixkit.co** (no account needed)
- Free game sounds
- Direct MP3 download
- Limited selection but high quality

**Option 3: Pixabay** (no account needed)
- Search "sound effects"
- CC0 license
- Download as MP3

### Testing Sounds

After placing sounds in `/src/main/resources/static/sounds/`, test them:

1. Start your Spring Boot app
2. Open browser dev console
3. Run:
   ```javascript
   AudioManager.playCorrect();
   AudioManager.playIncorrect();
   AudioManager.playVictory();
   ```

---

## 2. PWA Icons (8 sizes needed)

### Required Directory
Create: `/src/main/resources/static/images/icons/`

```bash
mkdir -p src/main/resources/static/images/icons
```

### Icon Sizes Needed

| Filename | Size | Purpose |
|---|---|---|
| icon-72.png | 72x72px | iOS/Android app icon |
| icon-96.png | 96x96px | Android app icon |
| icon-128.png | 128x128px | Desktop PWA |
| icon-144.png | 144x144px | Android home screen |
| icon-152.png | 152x152px | iOS home screen |
| icon-192.png | 192x192px | **Required** - Android |
| icon-384.png | 384x384px | Splash screen |
| icon-512.png | 512x512px | **Required** - High-res |

### Method 1: Use Existing Favicon

If you have a favicon at `/src/main/resources/static/images/favicon.png`:

1. **Online Tool** (easiest):
   - Go to https://realfavicongenerator.net/
   - Upload your favicon
   - Generate all sizes
   - Download and extract to `/static/images/icons/`

2. **ImageMagick** (command line):
   ```bash
   cd src/main/resources/static/images

   # Resize to all needed sizes
   convert favicon.png -resize 72x72 icons/icon-72.png
   convert favicon.png -resize 96x96 icons/icon-96.png
   convert favicon.png -resize 128x128 icons/icon-128.png
   convert favicon.png -resize 144x144 icons/icon-144.png
   convert favicon.png -resize 152x152 icons/icon-152.png
   convert favicon.png -resize 192x192 icons/icon-192.png
   convert favicon.png -resize 384x384 icons/icon-384.png
   convert favicon.png -resize 512x512 icons/icon-512.png
   ```

3. **Online Batch Resizer**:
   - https://bulkresizephotos.com/
   - Upload favicon
   - Select sizes: 72, 96, 128, 144, 152, 192, 384, 512
   - Download and rename

### Method 2: Create New Icon

1. **Design tool** (Figma, Canva, Photoshop):
   - Create 512x512px canvas
   - Design your icon (simple, recognizable at small sizes)
   - Export as PNG
   - Use Method 1 tools to resize

2. **Icon generators**:
   - https://icon.kitchen/ (Free PWA icon generator)
   - https://www.favicon-generator.org/ (All sizes)

### Icon Design Tips

- **Simple**: Works at small sizes
- **High contrast**: Visible on any background
- **Square safe area**: Important content in center 80%
- **Transparent background**: OR solid color
- **Test**: View at 72x72 to check legibility

### Testing Icons

1. Place icons in `/src/main/resources/static/images/icons/`
2. Restart Spring Boot app
3. Open in Chrome/Edge
4. DevTools → Application → Manifest
5. Verify all icons load
6. Test install prompt (mobile or desktop)

---

## 3. Quick Start Commands

### Install ffmpeg (for audio conversion)

**macOS:**
```bash
brew install ffmpeg
```

**Ubuntu/Debian:**
```bash
sudo apt install ffmpeg
```

**Windows:**
Download from https://ffmpeg.org/download.html

### Install ImageMagick (for image resizing)

**macOS:**
```bash
brew install imagemagick
```

**Ubuntu/Debian:**
```bash
sudo apt install imagemagick
```

**Windows:**
Download from https://imagemagick.org/script/download.php

---

## 4. Verification Checklist

### Sounds Checklist
- [ ] Created `/src/main/resources/static/sounds/` directory
- [ ] Downloaded/placed `correct.mp3`
- [ ] Downloaded/placed `incorrect.mp3`
- [ ] Downloaded/placed `tick.mp3`
- [ ] Downloaded/placed `countdown.mp3`
- [ ] Downloaded/placed `victory.mp3`
- [ ] Downloaded/placed `defeat.mp3`
- [ ] Downloaded/placed `join.mp3`
- [ ] Tested sounds in browser console
- [ ] Adjusted volume levels if needed

### PWA Icons Checklist
- [ ] Created `/src/main/resources/static/images/icons/` directory
- [ ] Generated `icon-72.png`
- [ ] Generated `icon-96.png`
- [ ] Generated `icon-128.png`
- [ ] Generated `icon-144.png`
- [ ] Generated `icon-152.png`
- [ ] Generated `icon-192.png` (**Required**)
- [ ] Generated `icon-384.png`
- [ ] Generated `icon-512.png` (**Required**)
- [ ] Verified manifest in DevTools
- [ ] Tested PWA install prompt

---

## 5. Troubleshooting

### Sounds Not Playing
- Check browser console for 404 errors
- Verify file paths match exactly (case-sensitive)
- Check file permissions: `chmod 644 src/main/resources/static/sounds/*.mp3`
- Test with `AudioManager.isEnabled()` in console
- Browser may block autoplay - click page first

### Icons Not Showing
- Clear browser cache (Ctrl/Cmd + Shift + R)
- Verify manifest.json is accessible: `http://localhost:8080/manifest.json`
- Check file paths are correct (no typos)
- Ensure PNG format (not JPG or other)
- Check file permissions

### PWA Not Installing
- Must be served over HTTPS (or localhost)
- Service worker must be registered
- Manifest must have required fields
- Icons must exist (192px and 512px minimum)
- Check Lighthouse PWA audit in Chrome DevTools

---

## 6. Optional Enhancements

### Sound Volume Adjustments
Edit `/scripts/audio-manager.js` DEFAULT_VOLUME:
```javascript
const DEFAULT_VOLUME = 0.5; // Change to 0.3 for quieter, 0.7 for louder
```

### Custom Background Patterns
Add to `/static/images/backgrounds/`:
- grid.png
- geometric.png
- gradient.png
- dots.png

(See `MILESTONE_10_PROGRESS.md` for implementation details)

---

## Need Help?

- **Sounds**: https://freesound.org/help/
- **Icons**: https://web.dev/add-manifest/
- **PWA**: https://developer.mozilla.org/en-US/docs/Web/Progressive_web_apps

All assets should use CC0 or CC-BY licenses for commercial use.
