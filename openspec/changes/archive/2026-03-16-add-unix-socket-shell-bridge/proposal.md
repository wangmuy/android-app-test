## Why

The interactive shell feature needs to communicate with the Android app's Java/Kotlin code to perform app-specific operations. Using Unix Domain Socket provides a clean, bidirectional communication channel between shell scripts and the app, enabling scripts to invoke app logic and receive responses.

## What Changes

- Add Unix Domain Socket server in Kotlin that listens on a predefined socket path
- Create `ShellCallback` interface with method `Pair<Int, String> call(String request)`
- Implement socket server that reads requests, invokes the callback, and writes responses
- Add shell script (`socket_bridge.sh`) in assets that communicates via `nc -U`
- Extract script to app's internal directory on first start
- Script sends request string and parses response (first line = return code, remaining = response body)
- Script uses the return code from response as its own exit code
- **Note**: Request is a plain string, not necessarily JSON

## Capabilities

### New Capabilities
- `unix-socket-shell-bridge`: Unix Domain Socket communication between shell scripts and app Kotlin code

### Modified Capabilities
- None

## Impact

- New: `UnixSocketServer` class for socket communication
- New: `ShellCallback` interface for request handling
- New: `socket_bridge.sh` shell script in assets
- Modified: `MainApplication` or `MainActivity` to initialize socket server and script extraction
- Modified: Existing `MainViewModel` to implement `ShellCallback` interface
