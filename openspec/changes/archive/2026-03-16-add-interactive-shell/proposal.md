## Why

Android apps typically lack direct shell access for debugging, testing CLI tools, or running system commands. Adding an interactive shell feature enables users to execute shell commands with proper output handling and a native Android UI, solving the need for on-device command execution without requiring external terminal apps.

## What Changes

- Add interactive shell UI components in existing MainActivity:
  - 'Start sh' button to launch persistent shell process
  - 'Stop sh' button to terminate the shell process
  - Command input (EditText)
  - Execute button to send command to running shell
  - Clear Output button to clear the output area
  - Scrollable output area (TextView) to display command results
- Add shell execution state and logic in existing MainViewModel
- Implement ProcessBuilder-based shell execution running `sh` (persistent, kept running between commands)
- Use separate threads for reading stdout and stderr streams
- Send commands to running shell via stdin with sentinel echo method (`; echo <UUID> FINISHED: $?`) to detect completion and capture exit codes
- Display exit code in output when sentinel is detected
- Track command execution state and re-enable execute button when sentinel is detected
- Handle button state management (disable/enable based on shell state)
- **IMPORTANT**: Modify existing MainViewModel and MainActivity directly, DO NOT create new ViewModel or Activity files

## Capabilities

### New Capabilities
- `interactive-shell`: Full interactive shell feature with UI and ProcessBuilder-based command execution

### Modified Capabilities
- None

## Impact

- Modified: `MainActivity` - Add shell UI components (Start/Stop sh buttons, EditText, Execute, Clear, TextView)
- Modified: `MainViewModel` - Add shell state management (process lifecycle, command execution)
- New utility: `ShellExecutor` - Process management for persistent shell with stdin/stdout/stderr handling
