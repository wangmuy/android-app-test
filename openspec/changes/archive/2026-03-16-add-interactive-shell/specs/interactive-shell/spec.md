## ADDED Requirements

### Requirement: Start shell process
The system SHALL allow users to start a persistent shell process by clicking "Start sh" button.

#### Scenario: Start shell successfully
- **WHEN** user clicks "Start sh" button
- **THEN** a sh process starts, output shows shell startup messages, and "Start sh" button becomes disabled

#### Scenario: Start button disabled when shell running
- **WHEN** shell is already running
- **THEN** "Start sh" button is disabled

### Requirement: Stop shell process
The system SHALL allow users to stop the shell process by clicking "Stop sh" button.

#### Scenario: Stop shell successfully
- **WHEN** user clicks "Stop sh" button while shell is running
- **THEN** the sh process is terminated and "Stop sh" button becomes disabled

#### Scenario: Stop button disabled when shell not running
- **WHEN** shell is not running
- **THEN** "Stop sh" button is disabled

### Requirement: Shell command execution
The system SHALL allow users to execute shell commands by entering them in an input field and pressing Execute.

#### Scenario: Execute simple command
- **WHEN** user enters "echo hello" and clicks Execute while shell is running
- **THEN** the command runs, output displays in the output area

#### Scenario: Execute command with pipe
- **WHEN** user enters "cat /proc/version | head -1" and clicks Execute
- **THEN** the piped command executes correctly and output displays

#### Scenario: Execute button disabled when shell not running
- **WHEN** shell is not running
- **THEN** Execute button is disabled

### Requirement: Real-time output streaming
The system SHALL display command output in real-time as it is produced, not waiting for completion.

#### Scenario: Long-running command shows progress
- **WHEN** user runs a command that produces output over time
- **THEN** output appears incrementally in the output area

### Requirement: Exit code notification via sentinel echo
The system SHALL append a sentinel echo command (`; echo <UUID> FINISHED: $?`) to each command to detect completion and capture the exit code.

#### Scenario: Successful command shows exit code 0
- **WHEN** user runs "echo test" and it succeeds
- **THEN** output includes "command finished with exit code 0" and Execute button becomes enabled

#### Scenario: Failing command shows non-zero exit code
- **WHEN** user runs "exit 1" or a failing command
- **THEN** output includes the non-zero exit code and Execute button becomes enabled

#### Scenario: Execute button disabled during command execution
- **WHEN** user clicks Execute button
- **THEN** Execute button becomes disabled until command completes

### Requirement: Clear output
The system SHALL allow users to clear the output area.

#### Scenario: Clear button empties output
- **WHEN** user clicks Clear Output button
- **THEN** the output text area becomes empty
