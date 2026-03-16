## Context

This is a new standalone feature for an existing Android app. The app uses Jetpack Compose for UI, MVVM architecture, and Koin for dependency injection. The feature adds an interactive shell terminal allowing users to execute shell commands directly within the app.

## Goals / Non-Goals

**Goals:**
- Provide a persistent shell interface where users can start `sh` once and send multiple commands
- Support pipe operations in commands as shell redirection
- Properly handle stdout and stderr output in real-time
- Keep the UI responsive during command execution
- Allow stopping the shell process when needed

**Non-Goals:**
- Full terminal emulator with interactive stdin (user types directly in shell)
- Command history persistence
- Tab completion or advanced shell features
- Remote command execution (local shell only)

## Decisions

1. **Persistent shell process with stdin command input**
   - Start `sh` process once using ProcessBuilder
   - Keep process running until user clicks "Stop sh"
   - Append sentinel echo to each command: `; echo <UUID> FINISHED: $?`
   - Parse stdout for sentinel to detect completion and extract exit code
   - Alternative: Spawn new process per command - inefficient, loses shell state
   - Decision: Persistent shell maintains state between commands

2. **ProcessBuilder with `sh` as shell**
   - Using `ProcessBuilder("sh")` provides a POSIX-compatible shell on Android
   - Alternative: Runtime.exec() - less flexible, no ability to set working directory
   - Decision: ProcessBuilder for better control over stdin/stdout/stderr streams

3. **Separate read threads for stdout and stderr**
   - Each stream gets its own thread with BufferedReader
   - Read lines asynchronously and append to UI state in real-time
   - Decision: Prevents deadlock from unbuffered streams

4. **StateFlow for UI state management**
   - StateFlows for: shellOutput (String), isShellRunning (Boolean), isCommandExecuting (Boolean)
   - ViewModel exposes state, UI collects and renders
   - Decision: Matches existing app architecture using StateFlow

5. **Execute button state with sentinel detection**
   - Disable Execute button when sending a command
   - Parse stdout for sentinel to detect command completion
   - On sentinel: output exit code on new line, then re-enable Execute button
   - Alternative: Always keep button enabled - could cause concurrent command issues
   - Decision: Disable during execution prevents command interleaving

## Risks / Trade-offs

- [Risk] Long-running commands could hang
  - → Mitigation: User can stop process with "Stop sh" button
- [Risk] Commands requiring user input (like `sudo -S`) won't work
  - → Mitigation: Only supports non-interactive commands
- [Risk] Output buffer could grow large
  - → Mitigation: Consider adding output limit in future
- [Risk] Process not properly terminated on app backgrounding
  - → Mitigation: ViewModel onCleared() handles process destruction
- [Risk] Shell process becomes unresponsive
  - → Mitigation: "Stop sh" button allows forceful process termination
