## ADDED Requirements

### Requirement: ShellCallback interface
The system SHALL provide a `ShellCallback` interface with method `call(request: String): Pair<Int, String>` that handles requests from shell scripts.

#### Scenario: Callback returns success response
- **WHEN** callback receives request and returns Pair(0, "success")
- **THEN** response written to socket is "0\nsuccess"

#### Scenario: Callback returns error response
- **WHEN** callback receives request and returns Pair(1, "error message")
- **THEN** response written to socket is "1\nerror message"

### Requirement: Unix Domain Socket server
The system SHALL run a Unix Domain Socket server that listens on a predefined path in the app's internal directory.

#### Scenario: Server starts successfully
- **WHEN** app starts and socket server initializes
- **THEN** socket file exists at {filesDir}/unix_socket_bridge.sock

#### Scenario: Server accepts client connection
- **WHEN** shell script connects to Unix socket
- **THEN** server accepts connection and reads request

#### Scenario: Server writes response to client
- **WHEN** callback completes processing request
- **THEN** server writes "returnCode\nresponseBody" to socket

### Requirement: Script extraction from assets
The system SHALL extract the shell script from assets to app's internal directory on first start.

#### Scenario: Script extracted on first start
- **WHEN** app starts for the first time
- **THEN** socket_bridge.sh exists in internal files directory and is executable

#### Scenario: Script not re-extracted if already exists
- **WHEN** app starts and script already exists
- **THEN** existing script is used (no re-extraction)

### Requirement: Shell script sends request and parses response
The system SHALL provide a shell script that sends a plain string request to Unix socket and parses the response.

#### Scenario: Script sends request successfully
- **WHEN** script is called with a request string
- **THEN** request is sent via nc -U to the socket

#### Scenario: Script parses return code and response
- **WHEN** socket returns "0\nsuccess response"
- **THEN** script outputs "success response" and exits with code 0

#### Scenario: Script uses return code as exit code
- **WHEN** socket returns a return code in the first line
- **THEN** script exits with that return code as its exit value

#### Scenario: Script handles error return code
- **WHEN** socket returns "1\nerror message"
- **THEN** script outputs "error message" and exits with code 1
