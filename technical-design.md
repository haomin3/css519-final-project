# Technical Design Document: Private Cloud File Server

## 1. Project Overview
This project is a simplified private cloud file server that allows authorized clients to upload and download files. The system is centered around a cloud server that manages client authentication and file operations. Before a client is allowed to perform uploads or downloads, it must first be authenticated by the server.

The project is intentionally limited in scope so it can be completed within the course timeline. It is also intentionally designed with certain security flaws so that it can be better used for future testing and analysis. Instead of trying to build a full cloud platform, the design focuses on a smaller set of core features that are realistic to implement and relevant to the course's incident response focus.

### 1.1 Language and Environment
The project is currently planned for `Java SE 21`, though another LTS version of Java would likely work as well. The cloud server is also the main target for Docker containerization so that the environment is easier to run consistently and test against. The design uses a client-server model because it matches the goal of centralized authentication and file handling while keeping the overall implementation manageable.

### 1.2 Incident Response Relevance
This project supports incident response analysis because the cloud server acts as a central observation point. Authentication attempts, failed access requests, and file operations can all be logged and reviewed. This makes it possible to discuss suspicious patterns such as repeated failed logins or unusual access behavior. Since the project is relatively small, security-relevant events should also be easier to track and interpret.

### 1.3 Future Improvements
To keep the project manageable, the following items are intentionally excluded from the initial design, but can be part of future improvements or expansions:

- Communication via secure channel rather than plaintext
- Proper authentication with asymmetric keys
- Key exchange, rotation or revocation support
- Full web or GUI interface
- Distributed or replicated storage via RAID array
- Automated intrusion detection features
- Load balancing and fault tolerance
- More detailed audit logging and monitoring tools

## 2. Motivation
I chose this project because it is a simplified version of something I have previously worked on, which makes it more realistic to implement within the available time. It also connects well to incident response because the server acts as a central point where authentication attempts, file access, and suspicious behavior can be observed and logged.

Since the course is more focused on incident response than large-scale software engineering, I want the project to be straightforward enough to build while still being useful for security analysis.

## 3. Core Components
### 3.1 Cloud Server
The cloud server is the main component of the system. It continuously listens for a limited number of valid client connections in order to conserve resources. The server is responsible for receiving client requests, performing authentication, managing active sessions, handling upload and download operations, and storing files on the server side.

The cloud server also acts as the central enforcement point for access control. If authentication fails, the server denies access and records the failed attempt. If a client fails authentication multiple times, the server may temporarily block additional attempts.

### 3.2 Client
The client connects to the cloud server and requests operations such as querying, uploading, or downloading files. A client is not allowed to access protected operations until it has successfully completed the authentication process. For the purposes of this project, the client will be simulated through `curl` requests from the command line.

During the authentication stage, the client sends a password along with a client ID. If authentication succeeds, the client is issued a session token for later communication with the server to perform other functions such as query, upload, and download.

### 3.3 Authentication Component
The authentication component is responsible for verifying that a client is authorized before file access is granted. Currently, only a client ID and password is needed for authentication.

This design could be improved later with a multi-step authentication process based on asymmetric cryptography. In this improved design, the client sends a password and timestamp encrypted with the cloud's public key. The password is used as an initial credential and to identify which client key should be associated with the request. After decrypting the message, the cloud checks whether the timestamp falls within a small allowed time window in order to reduce replay attacks. If the initial password check succeeds, the cloud generates a cryptographic challenge and encrypts it using the client's public key. The client must respond by signing the challenge with its private key, allowing the cloud to verify the client's identity. The cloud may also respond after a fixed delay regardless of success or failure in order to reduce timing-based information leakage.

### 3.4 Session Management
After successful authentication, the cloud issues a session token for the client. This session token is used for later requests such as querying, uploading, or downloading files. Once the session token is issued, the client is considered trusted for the duration of that session.

To reduce long-term exposure, the session key expires after a period of inactivity. A new authentication process is then required before protected operations can continue.

### 3.5 File Storage
The server stores uploaded files and retrieves them when authorized clients request downloads. To keep the design manageable, the initial version will likely use a simple local storage structure on the server rather than a more advanced storage architecture.

For the initial prototype, file data may persist across basic container restarts as long as the same container instance is reused, which may be sufficient for testing. However, this is not a form of reliable persistence, as recreating the container can remove that data. If stronger persistence is needed, a Docker volume or bind mount can be used.

### 3.6 Logging
The system will log important events such as successful authentication attempts, failed authentication attempts, blocked clients, upload requests, download requests, and denied access attempts. These logs will support the incident response focus of the project and make it easier to review suspicious or abnormal behavior.

### 3.7 API Endpoints
The initial version of the project will expose callable HTTP API endpoints for testing and mock implementation purposes. Responses will be returned in JSON format.

- `GET /health`
  Returns a simple status response indicating that the server is running.

- `POST /auth`
  Accepts client credentials and returns either a successful authentication response or an error response.

- `GET /files`
  Returns a list of available files for an authenticated client, or an error response if the client is not authenticated.

- `POST /files/upload`
  Accepts an authenticated file upload request and returns either a success response or an error response.

- `GET /files/download/{filename}`
  Returns the requested file for an authenticated client if it exists, or an error response if it does not exist or if the client is not authenticated.

## 4. End-to-End Workflows
### 4.1 Authenticate and List Files
A client sends credentials to the authentication endpoint. If authentication succeeds, the client requests a list of available files. After that, the client does not find the file they want and exits.

- Valid input: Correct client credentials
- Invalid input: Incorrect credentials or missing fields
- Error handling: Returns an authentication failure or bad request response in JSON format

### 4.2 Authenticate and Upload a File
A client first authenticates, then sends a file upload request. If the request is accepted, the server stores the file or simulates storing it and returns a success response. The client exits after successfully stores the target file onto the server.

- Valid input: Authenticated request with a valid file name and content
- Invalid input: Missing authentication, missing file name, or empty content
- Error handling: Returns an unauthorized or bad request response in JSON format

### 4.3 Authenticate and Download a File
A client first authenticates, then requests a file by name. If the file exists and the client is authorized, the server returns the file or simulated file content. The client then exits after receiving the target file.

- Valid input: Authenticated request for an existing file
- Invalid input: Missing authentication or nonexistent file name
- Error handling: Returns an unauthorized, bad request, or not found response in JSON format
