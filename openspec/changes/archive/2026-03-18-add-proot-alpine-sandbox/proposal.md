## Why

The current shell runs directly on the Android system, which has limited available commands. Adding proot with Alpine Linux rootfs provides a sandboxed Linux environment with a full set of standard Linux commands (ls, cat, grep, etc.), enabling richer shell interaction within the app.

## What Changes

- Add proot static binary and alpine-minirootfs-aarch64.tar.gz to assets
- Modify ShellExecutor to:
  - Extract proot binary to files dir if not present, set executable
  - Extract Alpine rootfs to alpine/ subdirectory if not present
  - Launch shell using proot with Alpine environment instead of native sh
- Use equivalent command: `PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin HOME=/root ./proot -r ./alpine/ -0 -w / -b /dev -b /proc -b /sys /bin/sh`

## Capabilities

### New Capabilities
- `proot-alpine-sandbox`: Sandboxed Alpine Linux environment via proot

### Modified Capabilities
- None

## Impact

- Modified: ShellExecutor.startShell() - add proot and rootfs extraction and launch logic
- New assets: proot static binary, alpine-minirootfs-aarch64.tar.gz
