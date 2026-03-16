## 1. ShellCallback Interface

- [x] 1.1 Create ShellCallback interface with call(request: String): Pair<Int, String> method

## 2. UnixSocketServer Implementation

- [x] 2.1 Create UnixSocketServer class with socket path configuration
- [x] 2.2 Implement startServer() function to create and bind Unix socket
- [x] 2.3 Implement accept loop to handle client connections
- [x] 2.4 Implement request reading and response writing
- [x] 2.5 Wire callback to process requests
- [x] 2.6 Implement stopServer() function for cleanup

## 3. Shell Script

- [x] 3.1 Create socket_bridge.sh script in assets/
- [x] 3.2 Implement request sending via nc -U
- [x] 3.3 Implement response parsing (first line = return code, remaining = body)
- [x] 3.4 Set script return code based on response

## 4. Script Extraction

- [x] 4.1 Create ScriptExtractor utility to copy script from assets to files directory
- [x] 4.2 Implement timestamp-based extraction logic (only extract if needed)
- [x] 4.3 Set executable permissions on extracted script

## 5. Integration

- [x] 5.1 Modify MainViewModel to implement ShellCallback interface
- [x] 5.2 Initialize socket server in MainActivity on shell start
- [x] 5.3 Stop socket server when shell stops
- [x] 5.4 Extract script on app first start
