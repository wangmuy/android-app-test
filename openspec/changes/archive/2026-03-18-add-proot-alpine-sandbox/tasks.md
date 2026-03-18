## 1. ShellExecutor Modifications

- [x] 1.1 Add asset names for proot and alpine rootfs in ShellExecutor
- [x] 1.2 Create extractProot() function to extract proot binary from assets
- [x] 1.3 Create extractAlpineRootfs() function to extract rootfs from assets
- [x] 1.4 Create proot_tmp/ directory in files directory
- [x] 1.5 Set PROOT_TMP_DIR environment variable to proot_tmp/
- [x] 1.6 Modify startShell() to check and extract proot before launch
- [x] 1.7 Modify startShell() to check and extract alpine rootfs before launch
- [x] 1.8 Update process launch command to use proot with Alpine environment
- [x] 1.9 Add fallback to native sh if proot fails
