# Threat Model: Private Cloud File Server

## 1. Overview
This document presents a threat model for the Private Cloud File Server. The system is an HTTP-based server that allows a client to authenticate with a username and password, receive a session token, and then use that token to access protected file operations such as listing files, uploading files, and downloading files.

The system exposes endpoints for health checking, authentication, file listing, upload, and download. Authentication is required for file-related operations, and session tokens are used to authorize those requests. Uploaded files are stored in server memory during runtime, and the application runs as a Java-based service.

## 2. Assets to Protect
The main assets in this system are listed below. These assets are important because unauthorized disclosure, modification, or disruption would directly affect the confidentiality, integrity, and availability of the system.

- User credentials submitted during authentication
- Issued session tokens
- Uploaded file contents
- File names and file listings
- Availability of the service
- Integrity of stored file data

## 3. Potential Threat Actors
The following threat actors are relevant to this system:

### 3.1 Unauthenticated or Unauthorized Remote User
An unauthenticated or unauthorized remote user may attempt to access protected resources without valid authentication.

### 3.2 Brute-Force Attacker
An attacker may repeatedly submit login attempts in order to guess valid credentials or trigger a denial of service against legitimate users.

### 3.3 Network Eavesdropper
An attacker with access to network traffic may attempt to observe credentials, session tokens, or file contents in transit.

### 3.4 Malicious Authenticated User
A valid and legitimate user may intentionally misuse the system by overwriting files, abusing file access, or sending harmful or unexpected input.

### 3.5 Opportunistic Attacker
An attacker may probe the API using malformed JSON, invalid methods, missing headers, or simply repeated requests in order to identify weak points.

## 4. Potential Attack Surfaces
The main attack surfaces in the current implementation are listed below. These represent the primary ways an attacker may interact with or attempt to abuse the system.

- The authentication interface
- Endpoints of protected file operations
- Session token handling through HTTP headers
- JSON request parsing and validation
- In-memory file storage

## 5. Key Threats
### 5.1 Credential Exposure
Since credentials are submitted over HTTP, an attacker monitoring network traffic may be able to capture usernames and passwords in transit.

<ins>Impact</ins>: Captured credentials could be reused to authenticate and obtain a valid session token.

### 5.2 Session Token Theft or Reuse
Since only a session token is sufficient to access protected file endpoints, if a token is exposed or intercepted, an attacker may reuse it until expiration.

<ins>Impact</ins>: Unauthorized listing, upload, or download of files.

### 5.3 Brute-Force Authentication Attempts
An attacker may repeatedly try different username and password combinations in order to gain access.

<ins>Impact</ins>: Unauthorized access if credentials are guessed, or temporary denial of access if lockout is triggered against a legitimate user.

### 5.4 Unauthorized File Access
An attacker who bypasses or abuses authentication could access file listings or file contents without authorization.

<ins>Impact</ins>: Disclosure of stored data and loss of confidentiality.

### 5.5 File Tampering or Overwrite
The system stores files by name, and an uploaded file can replace an existing file with the same name.

<ins>Impact</ins>: Loss of integrity of stored data.

### 5.6 Malicious or Unexpected Upload Content
The upload feature accepts any file content the client provides, which allows an attacker to submit malicious or unusually large content.

<ins>Impact</ins>: Storage of questionable content or unnecessary strain on the server.

### 5.7 Denial of Service
An attacker may repeatedly call endpoints or submit excessive requests in order to exhaust limited server resources.

<ins>Impact</ins>: Temporary reduced service availability.

## 6. STRIDE Analysis
### 6.1 (S)poofing threats involve pretending to be a legitimate user, including:
- Guessing valid credentials through repeated authentication attempts
- Reusing a stolen session token to act as an authenticated client

### 6.2 (T)ampering threats involve unauthorized modification of data, including:
- Overwriting an existing file by uploading new content with the same file name
- Modifying requests in transit if network traffic is not protected

### 6.3 (R)epudiation threats involve the ability to deny having performed an action, including:
- A user denying that they uploaded or downloaded a file
- Difficulty proving who performed an action if logging is limited

### 6.4 (I)nformation Disclosure threats involve unauthorized exposure of sensitive information, including:
- Disclosure of credentials or session tokens over plaintext HTTP
- Unauthorized access to file listings or file contents
- Exposure of file names or content that may reveal sensitive information

### 6.5 (D)enial of Service threats involve reducing or preventing normal system availability, including:
- Repeated login attempts
- Repeated requests to protected endpoints
- Resource exhaustion through excessive API use

### 6.6 (E)levation of Privilege threats involve gaining capabilities beyond those intended for the attacker, including:
- Obtaining authenticated access through stolen credentials
- Using a stolen or reused session token to perform protected operations

## 7. Existing Security Controls
The current system includes the following basic controls. These help reduce simple misuse, but they do not eliminate the main risks identified above.

- Authentication is required before protected file operations can be performed
- File-related endpoints reject missing or invalid session tokens
- Session tokens expire after a fixed period
- Repeated failed authentication attempts can cause a temporary lockout
- Request methods are validated
- Basic JSON and input validation is performed

## 8. Risk Analysis Summary
Overall, the highest risks are concentrated around authentication, session handling, confidentiality of transmitted data, and integrity of stored files. The most significant risks in the current system are, in no particular order:

- Credential and session exposure due to plaintext network communication
- Unauthorized access through stolen or reused session tokens
- Weak authentication security due to simple credential handling
- File integrity issues caused by overwrite behavior
- Reduced accountability if user actions cannot be strongly tied to an identity