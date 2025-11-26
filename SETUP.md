# Android Kotlin AI Coding Agents Setup - Quick Verification

Verify your AI coding agent assisted Android development environment is properly configured for client-only (no backend) development.

**Compatible with:** Claude Code, Gemini CLI, Cline, RooCode, and other AI coding assistants.

---

## ✅ Verification Steps

### 1. Confirm Files Are Present

Your project should have:

```bash
✓ .claude/agents/                # 11 agent files
✓ .claude/hooks/                 # 2 hooks
✓ .claude/skills/                # skill-developer
✓ .claude/settings.json          # Android configuration (client-only)
✓ dev/android/
  └─ ANDROID_KOTLIN_GUIDE.md     # Complete tech patterns
✓ dev/README.md                  # Dev docs methodology
✓ CLAUDE.md                      # Main documentation
✓ CODING_AGENT_COMPATIBILITY.md  # AI Coding agent general compatibility guide
✓ SETUP.md                       # This file
```

### 2. Verify Hooks Are Executable

```bash
cd ~/your/android-project
ls -la .claude/hooks/
```

You should see:
```
-rwxr-xr-x  .claude/hooks/post-tool-use-tracker.sh      ✓ Executable
-rwxr-xr-x  .claude/hooks/skill-activation-prompt.js    ✓ Executable
```

**If not executable:**
```bash
chmod +x .claude/hooks/*.sh
```

### 3. Validate Configuration

Check `.claude/settings.json`:

```bash
cat .claude/settings.json
```

Expected structure:
```json
{
  "hooks": {
    "UserPromptSubmit": ["skill-activation-prompt"],
    "PostToolUse": ["post-tool-use-tracker"]
  },
  "permissions": {
    "allow": ["Skill(skill-developer)"],
    "deny": [],
    "ask": []
  },
  "project": {
    "type": "android",
    "backend": "none",
    "framework": "Android (Kotlin + Jetpack Compose, MVVM)",
    "specs": {
      "language": "Kotlin",
      "ui_framework": "Jetpack Compose",
      "architecture": "MVVM with Clean Architecture",
      "libraries": ["Retrofit", "Room", "Coroutines & Flow", "Koin"],
      "min_sdk": 30,
      "target_sdk": 34,
      "compile_sdk": 34,
      "testing": "JUnit",
      "build_tool": "Gradle Kotlin DSL",
      "networking": true,
      "database": "Room",
      "dependency_injection": "Hilt",
      "design_system": "Material 3"
    }
  }
}
```

**Key checks:**
- ✓ `"type": "android"`
- ✓ `"backend": "none"` (client-only)
- ✓ Libraries include Retrofit, Room, Coroutines, Hilt
- ✓ Modern stack: Compose, Kotlin, MVVM

### 4. Test AI Coding Agent Integration

Open your AI coding agent and test:

```
Help me create a ViewModel for user settings using StateFlow
```

**Expected behavior (any agent):**
- Responds with Kotlin code
- Mentions Android/Kotlin best practices
- References your tech stack (Compose, Coroutines, etc.)
- Suggests proper ViewModel injection
- Mentions StateFlow and collectAsStateWithLifecycle()

**Claude Code specific:** May reference `.claude/settings.json` config

### 5. Test Android-Specific Knowledge

```
What architecture pattern should I use for my Android app?
```

**Expected:** Mentions MVVM with Clean Architecture, Repository pattern, etc.

### 6. Test Repository Pattern

```
How do I implement offline-first caching with Room and Retrofit?
```

**Expected:** Provides pattern:
1. Fetch from cache first
2. Then make API call
3. Update cache
4. Handle errors gracefully
5. Works offline

### 7. Test Agent Activation (Optional)

To verify agents work:

```
Code review: Is this ViewModel following best practices?

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()
}
```

**Expected:** Agent reviews code and provides architecture feedback.

---

## 🚀 Quick Test Commands

### Test 1: Create a Simple Component

```
Create a simple Kotlin data class for a User with id, name, and email.
```

Expected: Should generate a Kotlin data class with proper structure.

### Test 2: Ask About Your Tech Stack

```
What libraries are configured in my Android project?
```

Expected: Should mention:
- Retrofit for networking
- Room for database
- Coroutines/Flow for async
- Jetpack Compose for UI
- Koin for DI (lightweight)
- Kotlin language

### Test 3: Feature Implementation Pattern

```
Outline the steps to implement a product catalog with offline support.
```

Expected: Should provide 4-5 steps specific to Android:
- Data models
- Repository with caching
- ViewModel
- Compose UI
- Error handling

---

## 📋 Common Issues

*Note: Issues marked "(Claude Code)" only apply to Claude Code users. Other AI agents ignore `.claude/` directory.*

### Issue: Hooks Not Running (Claude Code)

**Symptom:** `.claude/hooks/*.js` files exist but don't trigger

**Solution:**
```bash
# Ensure JavaScript files are executable too
chmod +x .claude/hooks/*.js

# Verify Claude Code can execute hooks
cat .claude/settings.json | jq .hooks
```

### Issue: Skills Not Activating (Claude Code)

**Symptom:** Claude Code doesn't suggest relevant skills automatically

**Solution:**
```bash
# Check skill-developer is in allow list
cat .claude/settings.json | grep -A 5 "permissions"

# Should show: "allow": ["Skill(skill-developer)"]
```

### Issue: "File not found" Errors (Claude Code)

**Symptom:** Claude Code can't find `.claude/settings.json`

**Solution:**
- Verify you're in the project root directory
- Check files copied correctly: `ls -la .claude/`

### Issue: Wrong Tech Stack Detected (Claude Code)

**Symptom:** Claude Code suggests wrong patterns (e.g., Express.js instead of Android)

**Solution:**
```bash
# Check configuration
cat .claude/settings.json

# Verify:
#   "type": "android"
#   "backend": "none"
#   "framework": "Android (Kotlin..."
```

---

## 🎯 Next Steps After Verification

Once verified:

1. **Read the guide:**
   ```bash
   cat dev/android/ANDROID_KOTLIN_GUIDE.md | head -100
   ```
   - Start with "Quick Start"
   - Read "Architecture Overview"
   - Bookmark patterns you'll use

2. **Try a small feature:**
   ```
   Help me create a User data class and Room entity.
   ```

3. **Set up project structure:**
   ```
   Help me create the package structure for my app:
   - data/api/ for network
   - data/local/ for database
   - data/repository/ for repos
   - ui/screens/ for compose
   - di/ for Hilt modules
   ```

4. **Implement first screen:**
   ```
   Create a simple user list screen with:
   - ViewModel
   - Repository mock
   - Compose UI
   - Loading state
   ```

5. **Create a skill:**
   ```
   Using skill-developer, help me create a skill for
   my error handling pattern with Result<T> wrapper.
   ```

---

## 📊 Tech Stack Overview (Client-Only)

Your Android project uses:

| Component | Technology | Purpose | Version/Notes |
|-----------|-----------|---------|---------------|
| **Language** | Kotlin | App logic | 1.9+, Coroutines |
| **UI** | Jetpack Compose | Declarative UI | Material 3 |
| **Architecture** | MVVM | Separation of concerns | Clean Architecture |
| **State** | StateFlow/SharedFlow | Reactive streams | Lifecycle-aware |
| **Database** | Room | Local SQLite | Type-safe |
| **Networking** | Retrofit | HTTP client | Gson converter |
| **DI** | Koin | Dependency injection | Compile-time safe |
| **Build** | Gradle Kotlin DSL | Build system | Modern, type-safe |
| **Min SDK** | API 30 | Android 11+ | 94% market |
| **Target SDK** | API 34 | Android 14 | Latest |

**Backend:** None! Your app is client-only and makes API calls directly via Retrofit.

---

## 🔧 Configuration Customization

### Add More Hooks

Edit `.claude/settings.json`:

```json
{
  "hooks": {
    "UserPromptSubmit": ["skill-activation-prompt"],
    "PostToolUse": ["post-tool-use-tracker"],
    "Stop": ["error-handling-reminder"]
  }
}
```

### Adjust Permissions

Create `.claude/settings.local.json`:

```json
{
  "permissions": {
    "allow": ["Skill(skill-developer)"],
    "deny": [],
    "ask": ["Skill(web-research-specialist)"]
  }
}
```

### Update Tech Specs

If your setup changes, update `.claude/settings.json`:

```json
{
  "project": {
    "type": "android",
    "backend": "none",
    "framework": "Android (Kotlin + Jetpack Compose)",
    "specs": {
      "libraries": ["Your", "Libraries", "Here"]
    }
  }
}
```

---

## 📚 Resources

### Primary Documentation

- **Start Here:** `dev/android/ANDROID_KOTLIN_GUIDE.md` - Complete reference
- **Overview:** `CLAUDE.md` - Main documentation, `CODING_AGENT_COMPATIBILITY.md` - AI Coding agent general compatibility guide
- **Dev Docs:** `dev/README.md` - Documentation patterns

### Agent Documentation

- **All Agents:** `.claude/agents/README.md`
- **Hook Config:** `.claude/hooks/README.md`
- **Skill Developer:** `.claude/skills/skill-developer/SKILL.md`

### Android Resources

- [Android Developer Guides](https://developer.android.com/guide)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Retrofit Documentation](https://square.github.io/retrofit/)

---

## ✅ Verification Checklist

**Required (All AI Agents):**
- [ ] `dev/android/ANDROID_KOTLIN_GUIDE.md` available
- [ ] `CLAUDE.md` and `CODING_AGENT_COMPATIBILITY.md` and `SETUP.md` present
- [ ] `dev/android/ANDROID_KOTLIN_GUIDE.md` is readable

**Claude Code Specific (Optional):**
- [ ] All `.claude/` files present (agents, hooks, skills, settings.json)
- [ ] Hooks executable: `chmod +x .claude/hooks/*.sh`
- [ ] Configuration matches Android client-only setup
- [ ] `.claude/settings.json` is valid JSON
- [ ] Claude Code responds to Android prompts
- [ ] Tech stack correctly detected (Kotlin, Compose, MVVM)

---

## 📞 Getting Help

If something isn't working:

### General (All AI Agents)

**Ensure Android Studio:**
- Open in Android Studio
- Project synced with Gradle
- No build errors

### Claude Code Specific

If using Claude Code and having issues with agents/hooks:

1. **Check configuration:**
   ```bash
   cat .claude/settings.json | jq .
   ```

2. **Verify hooks:**
   ```bash
   ls -la .claude/hooks/
   ```

3. **Test agents:**
   ```
   Use code-architecture-reviewer to review my code
   ```

**Resources:**
- **All agents:** `CLAUDE.md` - Full documentation, `CODING_AGENT_COMPATIBILITY.md` - AI Coding agent general compatibility guide
- **This guide:** `SETUP.md` - Verification steps
- **Claude Code agents:** `.claude/agents/README.md`
- **Claude Code troubleshooting:** `.claude/skills/skill-developer/TROUBLESHOOTING.md`

---

**Setup Complete!** ✅

Once all checks pass, you're ready!

**Next Steps:**
1. Read `dev/android/ANDROID_KOTLIN_GUIDE.md` (Start with Quick Start)
2. Try simple prompts with your AI coding agent (Claude Code, Gemini CLI, etc.)
3. Implement a small feature
4. Build amazing Android apps! 🚀

---

**Configuration Date:** 2025-11-25
**Platform:** Android (Client-only, No Backend)
**Tech Stack:** Kotlin + Jetpack Compose + MVVM + Room + Retrofit + Coroutines + Koin

