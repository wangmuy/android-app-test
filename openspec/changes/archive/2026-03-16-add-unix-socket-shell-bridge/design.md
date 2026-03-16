## Context

This is a new communication bridge for the existing Android app. The app uses Jetpack Compose for UI, MVVM architecture, and Koin for dependency injection. The Unix Domain Socket bridge will allow shell scripts to invoke app logic and receive responses, enabling the interactive shell feature to call back into the app.

## Goals / Non-Goals

**Goals:**
- Provide Unix Domain Socket server listening on a predefined path
- Create callback interface for handling requests from shell scripts
- Extract shell script from assets to internal storage on app start
- Enable bidirectional communication: script sends request, app returns response

**Non-Goals:**
- Multiple socket connections (single client at a time)
- Authentication/authorization on socket connections
- Persistent connection (each request is a new connection)
- Binary protocol (text-based plain string)

## Decisions

1. **Unix Domain Socket over TCP localhost**
   - Unix Domain Socket stays within the device, no network exposure
   - Android file permissions can restrict access
   - Alternative: TCP on localhost - simpler but less secure
   - Decision: Unix Domain Socket for better isolation

2. **Socket path in app's internal files directory**
   - Path: `{filesDir}/unix_socket_bridge.sock`
   - Alternative: Cache directory - may be cleared
   - Alternative: External storage - requires permissions
   - Decision: Internal files directory for persistence and security

3. **Text-based protocol with newline delimiter**
   - Request: single line plain string (not necessarily JSON)
   - Response: first line = return code, remaining = response body
   - Alternative: Length-prefixed binary - more complex
   - Decision: Simple text protocol for shell compatibility with `nc -U`

4. **Blocking socket accept in background thread**
   - Server runs in single background thread accepting connections
   - Each client connection handled in its own thread
   - Decision: Simple thread-per-connection model sufficient for single-client use

5. **Script extraction on first start with timestamp check**
   - Check if extracted script exists and matches asset timestamp
   - Alternative: Always re-extract - wasteful
   - Decision: Timestamp-based extraction to avoid unnecessary file operations

6. **Script exit code from socket response**
   - Script parses first line as return code, uses it as `exit` code
   - Remaining lines are printed to stdout
   - Decision: Direct pass-through of return code for shell script integration

## Risks / Trade-offs

- [Risk] Socket file persists after crash
  - → Mitigation: Delete socket file on server start
- [Risk] Script not executable
  - → Mitigation: Set executable permissions after extraction
- [Risk] Socket path too long (Unix domain socket path limit ~100 chars)
  - → Mitigation: Use short path in internal directory
- [Risk] Concurrent requests cause race conditions
  - → Mitigation: Currently single client; add mutex if needed in future
- [Risk] App process killed, socket not cleaned up
  - → Mitigation: Socket file removed on next start
