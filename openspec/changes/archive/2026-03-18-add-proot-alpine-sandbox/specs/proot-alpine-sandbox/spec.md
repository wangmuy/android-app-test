## ADDED Requirements

### Requirement: Proot binary extraction
The system SHALL extract the proot static binary from assets to the files directory and make it executable.

#### Scenario: Proot extracted on first use
- **WHEN** shell is started and proot does not exist in files dir
- **THEN** proot is extracted from assets and set executable

#### Scenario: Proot not re-extracted if exists
- **WHEN** proot already exists in files dir
- **THEN** existing proot binary is used

### Requirement: Alpine rootfs extraction
The system SHALL extract the Alpine rootfs from assets to alpine/ subdirectory in files dir.

#### Scenario: Rootfs extracted on first use
- **WHEN** shell is started and alpine/ directory does not exist
- **THEN** alpine rootfs is extracted to files/alpine/

#### Scenario: Rootfs not re-extracted if exists
- **WHEN** alpine/ directory already exists
- **THEN** existing rootfs is used

### Requirement: Proot-based shell launch
The system SHALL launch a sandboxed shell using proot with Alpine environment.

#### Scenario: Shell starts with proot
- **WHEN** shell is started with proot support
- **THEN** command executed is equivalent to: `PATH=... HOME=root ./proot -r ./alpine/ -0 -w / -b /dev -b /proc -b /sys /bin/sh`

#### Scenario: Proot fallback on failure
- **WHEN** proot fails to start
- **THEN** fallback to native sh is attempted
