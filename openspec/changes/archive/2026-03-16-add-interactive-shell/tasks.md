## 1. Shell Execution Utility

- [x] 1.1 Create ShellExecutor utility class with persistent ProcessBuilder("sh")
- [x] 1.2 Implement stdout reader thread with BufferedReader
- [x] 1.3 Implement stderr reader thread with BufferedReader
- [x] 1.4 Add startShell() function to launch persistent sh process
- [x] 1.5 Add stopShell() function to destroy the process
- [x] 1.6 Add sendCommand() function to write command to stdin

## 2. MainViewModel Updates

- [x] 2.1 Add shellOutput StateFlow to MainViewModel
- [x] 2.2 Add isShellRunning StateFlow to MainViewModel
- [x] 2.3 Add startShell() function to MainViewModel
- [x] 2.4 Add stopShell() function to MainViewModel
- [x] 2.5 Add executeShellCommand() function to MainViewModel
- [x] 2.6 Add clearShellOutput() function to MainViewModel
- [x] 2.7 Wire up ShellExecutor output callbacks to update shellOutput

## 3. MainActivity UI Updates

- [x] 3.1 Add Row with Start sh button and Stop sh button
- [x] 3.2 Add Row with EditText, Execute button, Clear button
- [x] 3.3 Add scrollable TextView for shell output
- [x] 3.4 Connect Start sh button to MainViewModel.startShell()
- [x] 3.5 Connect Stop sh button to MainViewModel.stopShell()
- [x] 3.6 Connect Execute button to MainViewModel.executeShellCommand()
- [x] 3.7 Connect Clear button to MainViewModel.clearShellOutput()
- [x] 3.8 Disable Start sh button when shell is running
- [x] 3.9 Disable Stop sh button when shell is not running
- [x] 3.10 Disable Execute button when shell is not running
