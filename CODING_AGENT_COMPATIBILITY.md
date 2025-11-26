# AI Coding Agent Compatibility Guide

This Android development setup is designed to work with **multiple AI coding agents**, not just Claude Code.

## Compatible AI Coding Agents

✅ **Claude Code** (Anthropic) - Full feature support
✅ **Gemini CLI** (Google) - Tech docs & patterns
✅ **Cline** - Tech docs & patterns
✅ **RooCode** - Tech docs & patterns
✅ **GitHub Copilot CLI** - Tech docs & patterns
✅ **Any AI assistant** that can read Markdown documentation

---

## What Works With All Agents

### 📚 ANDROID_KOTLIN_GUIDE.md
**Location:** `dev/android/ANDROID_KOTLIN_GUIDE.md`

This comprehensive guide works with **any AI coding agent** and includes:

- ✅ Architecture patterns (MVVM + Clean Architecture)
- ✅ Room Database setup (entities, DAOs, migrations)
- ✅ Retrofit networking (API services, interceptors)
- ✅ StateFlow/SharedFlow state management
- ✅ Jetpack Compose best practices
- ✅ Repository pattern with offline-first caching
- ✅ Error handling strategies
- ✅ Koin dependency injection
- ✅ Complete code examples for all layers
- ✅ Common patterns (pagination, search, offline-first)
- ✅ Testing strategies

**Usage with any agent:**
```
Help me implement a Room database following the patterns in
 dev/android/ANDROID_KOTLIN_GUIDE.md
```

### 🎯 Architecture & Patterns

The architectural patterns documented work with any AI agent:

- Clean Architecture + MVVM
- Repository pattern
- Offline-first design
- State management with StateFlow
- Dependency injection with Koin
- Error handling strategies

### 💡 Code Examples

All code examples in the guide are **framework-agnostic** and can be used with any AI assistant.

---

## Claude Code Exclusive Features

These features **only work with Claude Code** and are safely ignored by other agents:

### 🤖 Agents (Specialized Assistants)
**Location:** `.claude/agents/`

11 specialized agents for complex tasks:
- code-architecture-reviewer
- code-refactor-master
- documentation-architect
- frontend-error-fixer
- plan-reviewer
- refactor-planner
- web-research-specialist
- And more...

**Usage:**
```
Use code-architecture-reviewer to check my repository pattern
```

### ⚓ Hooks (Event Automation)
**Location:** `.claude/hooks/`

- `skill-activation-prompt` - Auto-suggests relevant skills
- `post-tool-use-tracker` - Tracks file changes for context

### 🎓 Skills (Context-Aware Guidance)
**Location:** `.claude/skills/`

- `skill-developer` - Meta-skill for creating custom skills
- Can be extended with custom skills

### ⚙️ Configuration
**Location:** `.claude/settings.json`

Project-specific configuration that tells Claude Code about:
- Tech stack (Kotlin, Compose, etc.)
- Libraries in use
- Architecture patterns
- Project structure

---

## Setup Guide for Different Agents

### Claude Code (Full Features)

1. Copy entire directory to your project
2. Make hooks executable: `chmod +x .claude/hooks/*.sh`
3. Claude Code will use `.claude/settings.json` automatically
4. Access all 11 agents via natural language

**Commands that work:**
- "Help me create a ViewModel"
- "Use code-architecture-reviewer to check my code"
- "Help me create a custom skill"

### Gemini CLI / Cline / RooCode (Documentation Only)

1. Copy entire directory to your project
2. **Ignore `.claude/` directory** - it won't interfere
3. Reference the tech guide in your prompts:

**Commands that work:**
- "Follow the patterns in dev/android/ANDROID_KOTLIN_GUIDE.md"
- "Help me implement offline-first caching as described in the guide"
- "Create a Room entity following the examples in the guide"

**Commands that won't work (Claude-specific):**
- "Use code-architecture-reviewer..."
- "Create a skill for..."
- Any command referencing `.claude/` features

---

## Project Structure Explained

```
output/android/
├── CLAUDE.md                          ✅ Works with all agents
├── SETUP.md                           ✅ Works with all agents
├── COMPATIBILITY.md                   ✅ Works with all agents (this file)
├── .claude/                           ⚠️  Claude Code only
│   ├── agents/                        ⚠️  Claude agents
│   ├── hooks/                         ⚠️  Claude hooks
│   ├── skills/                        ⚠️  Claude skills
│   └── settings.json                  ⚠️  Claude configuration
└── dev/
    ├── README.md                      ✅ Works with all agents
    └── android/
        └── ANDROID_KOTLIN_GUIDE.md    ✅ Works with all agents ⭐
```

**Key:**
- ✅ Works with any AI coding agent
- ⚠️ Claude Code specific (ignored by others)
- ⭐ Primary documentation file

---

## FAQ

### Q: Can I use this with AI agents other than Claude Code?
**A:** Yes! The main tech guide (`ANDROID_KOTLIN_GUIDE.md`) works with any AI assistant. Only the `.claude/` directory is Claude-specific.

### Q: Will the `.claude/` directory interfere with other AI agents?
**A:** No. Other AI agents simply ignore this directory. It doesn't cause any conflicts.

### Q: Which AI agent gives me the most features?
**A:** Claude Code provides the most features with 11 specialized agents and automated hooks. However, all agents can use the comprehensive tech guide.

### Q: Should I delete the `.claude/` directory if I don't use Claude Code?
**A:** You can, but it's not necessary. It takes minimal space and doesn't interfere with other tools. Keeping it makes it easy to try Claude Code later.

### Q: What if my AI agent doesn't understand references to the guide?
**A:** You can:
1. Paste relevant sections directly into your prompt, OR
2. Ask the agent to read the file: "Read dev/android/ANDROID_KOTLIN_GUIDE.md and help me..."

---

## Quick Reference

### For Any AI Agent
```bash
# The tech guide is your best resource
cat dev/android/ANDROID_KOTLIN_GUIDE.md

# Ask questions referencing the guide
"Help me implement [feature] following the patterns in the guide"
```

### For Claude Code
```bash
# Make hooks executable (required for Claude Code)
chmod +x .claude/hooks/*.sh

# Use specialized agents
"Use code-architecture-reviewer to review my code"

# Check configuration
cat .claude/settings.json
```

---

## Summary

| Feature | Claude Code | Gemini CLI | Cline | RooCode | Other Agents |
|---------|-------------|------------|-------|---------|--------------|
| Tech Guide (ANDROID_KOTLIN_GUIDE.md) | ✅ | ✅ | ✅ | ✅ | ✅ |
| Architecture Patterns | ✅ | ✅ | ✅ | ✅ | ✅ |
| Code Examples | ✅ | ✅ | ✅ | ✅ | ✅ |
| Specialized Agents | ✅ | ❌ | ❌ | ❌ | ❌ |
| Hooks & Automation | ✅ | ❌ | ❌ | ❌ | ❌ |
| Custom Skills | ✅ | ❌ | ❌ | ❌ | ❌ |
| `.claude/settings.json` | ✅ | Ignored | Ignored | Ignored | Ignored |

**Bottom line:** The architecture patterns and tech documentation work with any AI agent. Claude Code adds specialized agents and automation on top.

---

**Questions?** See `CLAUDE.md` for full documentation or `SETUP.md` for verification steps.
