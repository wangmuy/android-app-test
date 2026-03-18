## Context

This modifies the existing ShellExecutor to use proot instead of native sh. The app already has ShellExecutor for running shell commands. Adding proot with Alpine Linux provides a full Linux environment with standard utilities.

## Goals / Non-Goals

**Goals:**
- Extract proot binary from assets to files directory on first use
- Extract Alpine rootfs from assets to alpine/ subdirectory on first use
- Launch sandboxed shell using proot with appropriate environment

**Non-Goals:**
- Multiple proot sessions (single session at a time)
- Persistence of proot environment modifications
- Network access within sandbox (requires additional bind mounts)

## Decisions

1. **Extraction check before launch**
   - Check if proot exists in files dir, extract if not
   - Check if alpine/ directory exists, extract rootfs if not
   - Use existence check to avoid re-extraction

2. **Proot command construction**
   - PATH set to standard Linux paths
   - HOME set to /root
   - PROOT_TMP_DIR set to proot_tmp/ in files directory
   - Proot flags: -r (rootfs), -0 (enter as root), -w (working dir), -b (bind mounts)
   - Bind mounts: /dev, /proc, /sys for basic functionality

3. **Proot tmp directory**
   - Create proot_tmp/ subdirectory in files directory
   - Set PROOT_TMP_DIR environment variable pointing to this directory for proot temporary files

4. **Use ProcessBuilder environment()**
   - Set PATH, HOME, and PROOT_TMP_DIR via environment()
   - Command uses proot directly with arguments

## Risks / Trade-offs

- [Risk] Proot binary not executable after extraction
  - → Mitigation: Call setExecutable(true) after extraction
- [Risk] Proot fails on certain Android versions
  - → Mitigation: Fallback to native sh if proot fails
