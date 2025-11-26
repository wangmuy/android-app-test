# Android Kotlin AI Coding Agents Setup - Complete

This directory contains a **self-contained AI coding agent assisted setup** for Android development. Works with Claude Code, Gemini CLI, Cline, RooCode, and other AI coding assistants.

**Copy this entire directory to your project to get started!**

**Tech Stack:**
- **Language:** Kotlin with Coroutines & Flow
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM with Clean Architecture
- **Libraries:** Retrofit, Room, Koin
- **Persistence:** Room Database (SQLite)
- **Min SDK:** 30, **Target SDK:** 34
- **Backend:** None (Client-only - API calls via Retrofit)

---

## 📦 What's Included

### 📘 COMPATIBILITY.md - Start Here!

**New!** Explains how this setup works with multiple AI coding agents:
- ✅ **All agents:** Tech guide, architecture patterns, code examples
- ⚠️ **Claude Code only:** Agents, hooks, skills, configuration

**[Read COMPATIBILITY.md](./COMPATIBILITY.md) to understand what works with your AI agent!**

### AI Coding Assistant Documentation (`dev/android/`)

**ANDROID_KOTLIN_GUIDE.md (comprehensive guide)** - Works with any AI coding agent!
- ✅ Architecture patterns (Clean Architecture + MVVM)
- ✅ Room Database setup (entities, DAOs, migrations)
- ✅ Retrofit networking (API services, interceptors)
- ✅ State management with StateFlow and SharedFlow
- ✅ Jetpack Compose best practices
- ✅ Repository pattern with offline-first caching
- ✅ Error handling strategies
- ✅ Koin dependency injection (lightweight, no code generation)
- ✅ Complete code examples for all layers
- ✅ Common patterns (pagination, search, offline-first)
- ✅ Testing strategies
- ✅ Project structure recommendations

### Claude Code Infrastructure (Optional, `.claude/`)

*Note: These are Claude Code specific features. Other AI agents will ignore this directory.*

**Agents (11 specialized assistants):**
- `auth-route-debugger` - Debugs authentication route issues
- `auth-route-tester` - Tests authentication routes
- `auto-error-resolver` - Automatically resolves common errors
- `code-architecture-reviewer` - Reviews code architecture
- `code-refactor-master` - Assists with code refactoring
- `documentation-architect` - Creates project documentation
- `frontend-error-fixer` - Fixes Android/Compose errors
- `plan-reviewer` - Reviews implementation plans
- `refactor-planner` - Plans refactoring strategies
- `web-research-specialist` - Conducts web research
- `claude-code-guide` - Answers questions about Claude Code

**Hooks (2 registered):**
- `skill-activation-prompt` - Suggests relevant skills automatically
- `post-tool-use-tracker` - Tracks file changes for context

**Active Skill:**
- `skill-developer` - Meta-skill for creating custom Android development skills

### Configuration (`.claude/settings.json`)

Claude Code configuration with your Android project specifications.

### Structure

```
├── CLAUDE.md            # This file - your main documentation
├── SETUP.md             # Quick setup verification
├── README.md            # Redirects to CLAUDE.md
├── .claude/             # Claude Code configuration
│   ├── agents/          # 11 specialized AI agents
│   ├── hooks/           # skill-activation-prompt, post-tool-use-tracker
│   ├── skills/          # skill-developer (meta-skill)
│   └── settings.json    # Your custom Android configuration
└── dev/
    ├── README.md        # Dev docs methodology
    └── android/
        └── ANDROID_KOTLIN_GUIDE.md  # Complete tech stack guide
```

---

## 🚀 Quick Start

### 1. Copy to Your Android Project

Since this directory is **self-contained**, simply copy it to your project:

```bash
cp -r * ~/path/to/your/android-project/
```

### 2. Make Hooks Executable

```bash
cd ~/path/to/your/android-project
chmod +x .claude/hooks/*.sh
```

### 3. Verify Setup

1. Open your Android project in Android Studio
2. Check that `.claude/settings.json` exists
3. Load project in Claude Code
4. Test with a simple prompt:

```
Help me create a basic ViewModel with StateFlow
```

### 4. Read the Guide

Open `dev/android/ANDROID_KOTLIN_GUIDE.md` for complete reference.

---

## 💡 How to Use

### With Any AI Coding Agent (Claude Code, Gemini CLI, Cline, RooCode, etc.)

**Ask for implementation help:**

```
Help me create a complete user profile feature:

1. Data Layer:
   - Room User entity and DAO
   - Retrofit API service for user endpoints
   - Data models for API and database

2. Repository:
   - Fetch from API, cache in Room
   - Offline-first strategy
   - Error handling

3. Presentation:
   - ViewModel with StateFlow
   - Sealed interface for UI state

4. UI:
   - Compose screen with user list
   - Loading, error, and success states
   - Pull-to-refresh

Follow MVVM architecture and patterns from the guide.
Let's start with Step 1: Data Layer...
```

**Claude Code Only - Use agents explicitly:**

*If you're using Claude Code, you can use specialized agents:*

```
I've finished my UserRepository implementation. Use code-architecture-reviewer
to check if it follows Android best practices, especially for error handling
and the offline-first caching pattern.
```

*Other AI coding agents: Simply ask for a code review directly.*

**Create documentation:**

```
Create comprehensive documentation for my offline-first caching strategy
using Room and Retrofit, following the patterns in the guide.
```

**For complex features:**

```
/dev-docs implement product catalog with search, filtering, and offline support
```

### Architecture Overview

Your setup uses **Clean Architecture + MVVM**:

```
UI Layer (Compose)
    ↕️
ViewModel (StateFlow)
    ↕️
Repository Pattern
    ↕️
Data Sources (Room + Retrofit)
```

### Key Tech Stack Components

| Component | Technology | Usage |
|-----------|-----------|-------|
| **Language** | Kotlin 1.9+ | Modern, concise, null-safe |
| **UI** | Jetpack Compose | Declarative UI |
| **Architecture** | MVVM + Clean Architecture | Separation of concerns |
| **State Management** | StateFlow/SharedFlow | Reactive streams |
| **Database** | Room | SQLite abstraction |
| **Networking** | Retrofit | HTTP client |
| **DI** | Koin | Dependency injection |
| **Async** | Coroutines & Flow | Asynchronous operations |
| **Design** | Material 3 | Modern design system |

### State Management Pattern

**ViewModels expose StateFlow:**

```kotlin
class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    // ... logic
}
```

**UI collects state:**

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is UserUiState.Loading -> LoadingIndicator()
        is UserUiState.Success -> UserList(uiState.users)
        is UserUiState.Error -> ErrorMessage(uiState.message)
    }
}
```

### Repository Pattern (Offline-First)

```kotlin
class UserRepositoryImpl(
    private val api: UserApiService,
    private val dao: UserDao
) : UserRepository {

    override fun getUsers(): Flow<Result<List<User>>> = flow {
        // Emit cached data first
        dao.getAllUsers().collect { entities ->
            emit(Result.Success(entities.map { it.toDomain() }))
        }

        // Then refresh from network
        try {
            val apiUsers = api.getUsers()
            val entities = apiUsers.map { it.toEntity() }
            dao.insertUsers(entities)
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}
```

---

## 🎓 Recommended Workflow

### When Starting a New Feature

1. **For complex features (3+ days):** Use `/dev-docs` command
2. **For simple features:**
   - Read relevant sections in `ANDROID_KOTLIN_GUIDE.md`
   - Start with data layer (API models, entities, DAOs)
   - Implement repository with caching
   - Create ViewModel with state management
   - Build Compose UI
   - Test each layer independently

### Example Implementation Flow

**Feature:** User Profile Management

**Step 1: Data Layer** (30 minutes)
```
Help me create the data layer for user profiles:

1. Create UserApiModel for Retrofit
2. Create UserEntity for Room
3. Create UserDao with suspend functions
4. Create mapping extensions

Use suspend functions for async operations.
```

**Step 2: Repository** (20 minutes)
```
Now create UserRepository that:
- Fetches users from API
- Caches them in Room
- Returns Flow<List<User>>
- Handles errors gracefully
- Works offline-first
```

**Step 3: ViewModel** (15 minutes)
```
Create UserViewModel with:
- StateFlow for UI state
- Loading, Success, Error states
- Load and refresh functions
- Handle user actions
```

**Step 4: UI** (30 minutes)
```
Create compose screens:
- User list with loading state
- User detail view
- Pull-to-refresh
- Error messages with retry
```

---

## 🔧 Customization

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

Available hooks:
- `error-handling-reminder` - Reminds about error handling

### Create Custom Skills

```
Using skill-developer, help me create a skill for my company's
standard error handling pattern with Result<T> wrapper.
```

### Add Libraries

To add libraries (like WorkManager, DataStore, Sentry):

```
Help me add documentation for:
- WorkManager for background tasks
- DataStore for settings persistence
- Sentry for error tracking

Update the guide with best practices for these.
```

---

## 📚 Additional Resources

### Android Development

- [Android Developer Guides](https://developer.android.com/guide)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Room Persistence](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)

### Claude Code

- [Claude Code Guide](showcase/.claude/agents/claude-code-guide.md)
- [Skill Developer](showcase/.claude/skills/skill-developer/SKILL.md)
- [Agents README](showcase/.claude/agents/README.md)

### Architecture Resources

- [Guide to app architecture](https://developer.android.com/topic/architecture)
- [UI layer guide](https://developer.android.com/topic/architecture/ui-layer)
- [Data layer guide](https://developer.android.com/topic/architecture/data-layer)

---

## ✅ Setup Verification

Confirm setup is complete:

- [x] `.claude/` directory with agents, hooks, skills, settings.json
- [x] `.claude/settings.json` configured for Android (client-only)
- [x] `dev/android/ANDROID_KOTLIN_GUIDE.md` created
- [x] `SETUP.md` with verification steps
- [x] Hooks registered in settings.json
- [x] Configuration matches Android tech stack
- [x] All 11 agents available
- [x] skill-developer available for creating custom skills

---

## 🎯 Next Steps

### Right Now (10 minutes)

1. ✅ Copy setup to your project
2. ✅ Make hooks executable: `chmod +x .claude/hooks/*.sh`
3. ✅ Verify Claude Code integration (run test prompt)
4. 🎯 Read first 3 sections of `ANDROID_KOTLIN_GUIDE.md`

### Today

1. 📖 Read entire `ANDROID_KOTLIN_GUIDE.md` (45 min)
2. 💬 Try simple prompts with Claude Code
3. 🛠️ Implement a small feature (e.g., User model + Room)
4. 📝 Try a simple agent: "Use code-architecture-reviewer to review this"

### This Week

1. 🏗️ Set up complete project architecture
2. 📱 Implement 2-3 core screens
3. 🔌 Integrate API with Retrofit
4. 🗄️ Set up Room database
5. 📝 Create your first custom skill

### This Month

1. ✅ Full app architecture
2. ✅ All screens implemented
3. ✅ Offline-first caching working
4. ✅ Custom skills for common patterns
5. ✅ Comprehensive documentation
6. ✅ Tests for critical paths

---

## 📱 Project Ideas

Your setup is perfect for:

- **Product Catalog** with offline search and filtering
- **Task Manager** with sync and local caching
- **Social App** with user profiles and content
- **News Reader** with offline reading
- **E-commerce** with product browsing
- **Note-taking** with cloud sync

All work completely offline-first with API sync!

---

## 💬 Getting Help

### Ask Claude Code

```
"What can you help me with?"
"Help me create a ViewModel with StateFlow"
"How do I implement offline-first caching?"
```

### Use Agents (Claude Code Only)

*If using Claude Code, you can use specialized agents:*

```
"Use code-architecture-reviewer to review my UserRepository"
"Use documentation-architect to document my caching strategy"
"Use frontend-error-fixer to debug this compose error"
```

*Other AI agents: Ask for help directly (e.g., "Review my UserRepository")*

### Create Skills (Claude Code Only)

*If using Claude Code, you can create custom skills:*

```
"Help me create a skill for my company's error handling pattern"
```

### Resources

- **Main Guide:** `dev/android/ANDROID_KOTLIN_GUIDE.md`
- **Setup:** `SETUP.md`
- **Agents:** `.claude/agents/README.md`
- **Hooks:** `.claude/hooks/README.md`

---

## 🎉 You're Ready to Build!

Your AI coding agent assisted Android development environment is fully configured!

**Key Advantages:**
- ✅ Modern stack (Compose, Kotlin, Coroutines)
- ✅ Clean Architecture + MVVM
- ✅ Offline-first design
- ✅ Comprehensive tech guide included (works with any AI agent!)
- ✅ Optional: 11 Claude Code agents ready
- ✅ Optional: Custom skill development enabled (Claude Code)

**Start building:**
```
Help me create the data layer for a product catalog with Room and Retrofit.
Let's start with entities and network models.
```

Happy coding! 🚀

---

**Created:** 2025-11-25
**Template Version:** 1.0
**Platform:** Android (Client-only, No Backend)
**Tech Stack:** Kotlin + Jetpack Compose + MVVM + Room + Retrofit + Coroutines + Koin

