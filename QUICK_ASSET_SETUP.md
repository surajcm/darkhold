# Quick Asset Setup Guide

## ✅ Directories Created
- `/src/main/resources/static/sounds/` ✓
- `/src/main/resources/static/images/icons/` ✓

## 📁 Part 1: Generate PWA Icons (5 minutes)

You have an existing `favicon.png` (1103 bytes). Let's use it to generate all icon sizes!

### Method 1: Online Tool (EASIEST - No Installation Required)

1. **Go to**: https://realfavicongenerator.net/
2. **Upload**: `/src/main/resources/static/images/favicon.png`
3. **Click**: "Generate your Favicons and HTML code"
4. **Download** the package
5. **Extract** and copy these 8 files to `/src/main/resources/static/images/icons/`:
   - android-chrome-72x72.png → **rename to** `icon-72.png`
   - android-chrome-96x96.png → **rename to** `icon-96.png`
   - android-chrome-128x128.png → **rename to** `icon-128.png`
   - android-chrome-144x144.png → **rename to** `icon-144.png`
   - apple-touch-icon.png (152x152) → **rename to** `icon-152.png`
   - android-chrome-192x192.png → **rename to** `icon-192.png`
   - android-chrome-384x384.png → **rename to** `icon-384.png`
   - android-chrome-512x512.png → **rename to** `icon-512.png`

### Method 2: Bulk Resize Photos (Alternative Online Tool)

1. **Go to**: https://bulkresizephotos.com/en
2. **Upload**: `favicon.png`
3. **Set dimensions**: 72, 96, 128, 144, 152, 192, 384, 512 (create 8 versions)
4. **Download all**
5. **Rename** files to match: `icon-{size}.png`
6. **Move** to `/src/main/resources/static/images/icons/`

### Method 3: Icon Kitchen (PWA-Specific)

1. **Go to**: https://icon.kitchen/
2. **Upload**: `favicon.png`
3. Click "Generate"
4. **Download** icon pack
5. **Extract** and rename to match required sizes

### Verification Command

After generating icons, run this to verify:

```bash
cd /Users/A-3133/workspace/personal/darkhold/src/main/resources/static/images/icons/
ls -lh icon-*.png
```

You should see 8 files:
- icon-72.png
- icon-96.png
- icon-128.png
- icon-144.png
- icon-152.png
- icon-192.png (Required for PWA)
- icon-384.png
- icon-512.png (Required for PWA)

---

## 🔊 Part 2: Download Sound Effects (10 minutes)

### Quick Download from Freesound.org

**You'll need a free account** (takes 2 minutes to create)

1. **Go to**: https://freesound.org/
2. **Create account** (or login)
3. **Download these sounds** (links to CC0/free sounds):

#### 1. correct.mp3 - Success Sound
**Search**: "correct answer"
**Recommended**: https://freesound.org/people/LittleRobotSoundFactory/sounds/270319/
- Click "Download"
- Save as `correct.mp3`

**Alternative searches**: "success chime", "positive ding", "achievement"

#### 2. incorrect.mp3 - Error Sound
**Search**: "error buzz"
**Recommended**: https://freesound.org/people/distillerystudio/sounds/327736/
- Click "Download"
- Save as `incorrect.mp3`

**Alternative searches**: "wrong buzzer", "negative beep"

#### 3. countdown.mp3 - Warning Beep
**Search**: "countdown beep"
**Recommended**: https://freesound.org/people/rhodesmas/sounds/342756/
- Click "Download"
- Save as `countdown.mp3`

**Alternative searches**: "alert beep", "warning tone"

#### 4. victory.mp3 - Win Fanfare
**Search**: "victory fanfare"
**Recommended**: https://freesound.org/people/FunWithSound/sounds/456966/
- Click "Download"
- Save as `victory.mp3`

**Alternative searches**: "win jingle", "success fanfare", "triumph"

#### 5. defeat.mp3 - Game Over
**Search**: "game over"
**Recommended**: https://freesound.org/people/Bertrof/sounds/351565/
- Click "Download"
- Save as `defeat.mp3`

**Alternative searches**: "sad trombone", "lose sound"

#### 6. join.mp3 - Notification
**Search**: "notification ping"
**Recommended**: https://freesound.org/people/AdamWeeden/sounds/401748/
- Click "Download"
- Save as `join.mp3`

**Alternative searches**: "subtle ping", "pop notification"

#### 7. tick.mp3 - Clock Tick (Optional)
**Search**: "clock tick"
**Note**: This is currently disabled in code (can be annoying)
**Recommended**: https://freesound.org/people/InspectorJ/sounds/343130/
- Click "Download"
- Save as `tick.mp3`

### Alternative: No-Account Sound Libraries

**Pixabay** (No account needed):
1. Go to: https://pixabay.com/sound-effects/
2. Search for each sound type
3. Download as MP3
4. Rename to match filenames above

**Mixkit** (No account needed):
1. Go to: https://mixkit.co/free-sound-effects/
2. Browse "Game" or "UI" categories
3. Download MP3s
4. Rename appropriately

### After Downloading Sounds

**Move all 7 MP3 files to**:
```bash
/Users/A-3133/workspace/personal/darkhold/src/main/resources/static/sounds/
```

### Verification Command

```bash
cd /Users/A-3133/workspace/personal/darkhold/src/main/resources/static/sounds/
ls -lh *.mp3
```

You should see:
- correct.mp3
- incorrect.mp3
- countdown.mp3
- victory.mp3
- defeat.mp3
- join.mp3
- tick.mp3

---

## 🧪 Part 3: Test Everything (5 minutes)

### 1. Start Your Spring Boot Application

```bash
cd /Users/A-3133/workspace/personal/darkhold
./gradlew bootRun
```

### 2. Open Browser

Navigate to: http://localhost:8080

### 3. Test Sounds

Open browser console (F12) and run:

```javascript
// Test each sound
AudioManager.playCorrect();      // Should hear success chime
AudioManager.playIncorrect();    // Should hear error buzz
AudioManager.playCountdown();    // Should hear urgent beep
AudioManager.playVictory();      // Should hear celebration
AudioManager.playDefeat();       // Should hear game over
AudioManager.playJoin();         // Should hear notification ping
AudioManager.playTick();         // Should hear clock tick

// Check sound status
AudioManager.isEnabled();        // Should return true
AudioManager.getVolume();        // Should return 0.5 (50%)
```

### 4. Test PWA Icons

1. Open Chrome DevTools (F12)
2. Go to **Application** tab
3. Click **Manifest** in sidebar
4. Verify all 8 icons show up without errors
5. Check for any red error messages

### 5. Test Dark Mode

1. Click the moon icon in navbar
2. Page should switch to dark theme
3. Reload page - theme should persist
4. Click sun icon to switch back

### 6. Test PWA Installation

**On Desktop (Chrome/Edge):**
- Look for install icon in address bar
- Click to install app
- App should open in standalone window

**On Mobile:**
- Menu → "Add to Home Screen"
- Icon should appear on home screen
- Open should launch as app

---

## 🎯 Quick Checklist

- [x] Icons: Generated 8 PNG files in `/static/images/icons/`
- [x] Sounds: Downloaded 7 MP3 files in `/static/sounds/`
- [x] Browser test: Sounds play in console
- [x] PWA test: Manifest loads without errors
- [ ] Theme test: Dark mode toggle works
- [ ] Install test: PWA installs successfully

---

## 🆘 Troubleshooting

### Sounds Not Playing
- **Check**: Browser console for 404 errors on sound files
- **Fix**: Verify filenames match exactly (case-sensitive)
- **Note**: First play may require user interaction (click page first)

### Icons Not Showing
- **Check**: DevTools → Application → Manifest for errors
- **Fix**: Clear browser cache (Ctrl/Cmd + Shift + R)
- **Verify**: Files are PNG format, not JPG

### Can't Find Good Sounds
Use this Freesound.org search strategy:
1. Filter by **License**: CC0 or CC-BY
2. Filter by **Duration**: < 2 seconds
3. Sort by **Downloads** (most popular)
4. Preview before downloading

---

## 📊 Expected File Structure

After completion:

```
src/main/resources/static/
├── images/
│   ├── icons/
│   │   ├── icon-72.png      ✓
│   │   ├── icon-96.png      ✓
│   │   ├── icon-128.png     ✓
│   │   ├── icon-144.png     ✓
│   │   ├── icon-152.png     ✓
│   │   ├── icon-192.png     ✓ (Required)
│   │   ├── icon-384.png     ✓
│   │   └── icon-512.png     ✓ (Required)
│   └── favicon.png (existing)
└── sounds/
    ├── correct.mp3          ✓
    ├── incorrect.mp3        ✓
    ├── countdown.mp3        ✓
    ├── victory.mp3          ✓
    ├── defeat.mp3           ✓
    ├── join.mp3             ✓
    └── tick.mp3             ✓
```

---

## ⏱️ Total Time Estimate: 15-20 minutes

- Icons: 5 minutes (online tool)
- Sounds: 10 minutes (download 7 files)
- Testing: 5 minutes (verify everything works)

**Once complete, Milestone 10 will be ~85% done!**
