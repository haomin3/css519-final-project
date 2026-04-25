# Initial Security Test Documentation: Cloudsploitable

## 1. Overview
This document records the initial security-related testing performed against Cloudsploitable. The purpose of these tests is to verify that the system enforces authentication for protected operations, rejects malformed or unauthorized requests, and responds correctly to both valid and invalid input.

The current test set focuses on authentication, session-based access control, request validation, and basic abuse resistance. These tests are meant to confirm the current security behavior of the system through direct HTTP request and response testing.

## 2. Test Approach
The initial security tests were performed by sending HTTP requests to the running server and reviewing the returned HTTP status codes and JSON responses. The tests focused on the following areas:

- Authentication success and failure behavior
- Access control for protected endpoints
- Input validation for malformed or incomplete JSON requests
- Request method validation
- Session token enforcement
- Repeated failed login handling

## 3. Test Environment
The tests were performed against the server running locally on port 8080.

The base URL is `http://localhost:8080`, and requests were sent manually using `curl` from the command line, while automated tests are planned for future updates.

## 4. Initial Security Test Cases

### Test 1: Health Endpoint Responds Successfully
**Objective:** Verify that the health endpoint is reachable and returns a successful status response.

**Request:**
```bash
curl -i http://localhost:8080/health
```

**Expected Result:**  
The server should return HTTP 200 with a JSON body indicating success and status `ok`.

**Actual Result:**
```http
HTTP/1.1 200 OK
Date: Sat, 11 Apr 2026 08:47:30 GMT
Content-type: application/json; charset=UTF-8
Content-length: 30

{"success":true,"status":"ok"}
```

**Status:** Pass

-----

### Test 2: Authentication Succeeds with Valid Credentials
**Objective:** Verify that valid credentials are accepted and that a session token is issued.

**Request:**
```bash
curl -i -X POST http://localhost:8080/auth \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

**Expected Result:**  
The server should return HTTP 200 and include a session token in the JSON response.

**Actual Result:**
```http
HTTP/1.1 200 OK
Date: Sat, 11 Apr 2026 08:47:39 GMT
Content-type: application/json; charset=UTF-8
Content-length: 70

{"sessionToken":"b980230e-868f-43f8-930d-7bd1643831b6","success":true}
```

**Status:** Pass

-----

### Test 3: Authentication Fails with Invalid Credentials
**Objective:** Verify that invalid credentials are rejected.

**Request:**
```bash
curl -i -X POST http://localhost:8080/auth \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrongpassword"}'
```

**Expected Result:**  
The server should return HTTP 401 with an error indicating invalid credentials.

**Actual Result:**
```http
HTTP/1.1 401 Unauthorized
Date: Sat, 11 Apr 2026 08:47:52 GMT
Content-type: application/json; charset=UTF-8
Content-length: 47

{"error":"invalid_credentials","success":false}
```

**Status:** Pass

-----

### Test 4: Repeated Failed Authentication Causes Temporary Block
**Objective:** Verify that repeated failed login attempts trigger temporary blocking behavior.

**Request:**  
Send the following request multiple times with the same username and wrong password:
```bash
curl -i -X POST http://localhost:8080/auth \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrongpassword"}'
```

**Expected Result:**  
After repeated failed attempts, the server should return a response indicating the username is temporarily blocked.

**Actual Result:**  
On the first few failed attempts, the server returned:
```http
HTTP/1.1 401 Unauthorized
Date: Sat, 11 Apr 2026 08:48:11 GMT
Content-type: application/json; charset=UTF-8
Content-length: 47

{"error":"invalid_credentials","success":false}
```

After the failed-attempt threshold was reached, the server returned:
```http
HTTP/1.1 403 Forbidden
Date: Sat, 11 Apr 2026 08:48:39 GMT
Content-type: application/json; charset=UTF-8
Content-length: 47

{"error":"temporarily_blocked","success":false}
```

**Status:** Pass

-----

### Test 5: Authentication Fails for Malformed JSON
**Objective:** Verify that malformed JSON is rejected.

**Request:**
```bash
curl -i -X POST http://localhost:8080/auth \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"'
```

**Expected Result:**  
The server should return HTTP 400 with an error indicating invalid JSON.

**Actual Result:**
```http
HTTP/1.1 400 Bad Request
Date: Sat, 11 Apr 2026 08:48:45 GMT
Content-type: application/json; charset=UTF-8
Content-length: 40

{"error":"invalid_json","success":false}
```

**Status:** Pass

-----

### Test 6: Authentication Fails When Required Fields Are Missing
**Objective:** Verify that missing username or password fields are rejected.

**Request:**
```bash
curl -i -X POST http://localhost:8080/auth \
  -H "Content-Type: application/json" \
  -d '{"username":"admin"}'
```

**Expected Result:**  
The server should return HTTP 400 with an error indicating missing username or password.

**Actual Result:**
```http
HTTP/1.1 400 Bad Request
Date: Sat, 11 Apr 2026 08:48:51 GMT
Content-type: application/json; charset=UTF-8
Content-length: 56

{"error":"missing_username_or_password","success":false}
```

**Status:** Pass

-----

### Test 7: File Listing Is Rejected Without Session Token
**Objective:** Verify that protected file operations cannot be accessed without authentication.

**Request:**
```bash
curl -i http://localhost:8080/files
```

**Expected Result:**  
The server should return HTTP 401 with an error indicating a missing or invalid session.

**Actual Result:**
```http
HTTP/1.1 401 Unauthorized
Date: Sat, 11 Apr 2026 08:49:11 GMT
Content-type: application/json; charset=UTF-8
Content-length: 54

{"error":"invalid_or_missing_session","success":false}
```

**Status:** Pass

-----

### Test 8: File Listing Succeeds with Valid Session Token
**Objective:** Verify that a valid session token allows access to protected file listing.

**Request:**
```bash
curl -i http://localhost:8080/files \
  -H "Session-Token: b980230e-868f-43f8-930d-7bd1643831b6"
```

**Expected Result:**  
The server should return HTTP 200 and a JSON list of available files.

**Actual Result:**
```http
HTTP/1.1 200 OK
Date: Sat, 11 Apr 2026 08:49:47 GMT
Content-type: application/json; charset=UTF-8
Content-length: 63

{"files":["notes.txt","hello.txt","readme.txt"],"success":true}
```

**Status:** Pass

-----

### Test 9: Upload Is Rejected Without Session Token
**Objective:** Verify that upload requires authentication.

**Request:**
```bash
curl -i -X POST http://localhost:8080/upload \
  -H "Content-Type: application/json" \
  -d '{"name":"test.txt","content":"sample"}'
```

**Expected Result:**  
The server should return HTTP 401 with an invalid or missing session error.

**Actual Result:**
```http
HTTP/1.1 401 Unauthorized
Date: Sat, 11 Apr 2026 08:49:53 GMT
Content-type: application/json; charset=UTF-8
Content-length: 54

{"error":"invalid_or_missing_session","success":false}
```

**Status:** Pass

-----

### Test 10: Upload Fails for Missing Required Fields
**Objective:** Verify that upload requests with incomplete JSON are rejected.

**Request:**
```bash
curl -i -X POST http://localhost:8080/upload \
  -H "Content-Type: application/json" \
  -H "Session-Token: b980230e-868f-43f8-930d-7bd1643831b6" \
  -d '{"name":"test.txt"}'
```

**Expected Result:**  
The server should return HTTP 400 with an error indicating missing file name or content.

**Actual Result:**
```http
HTTP/1.1 400 Bad Request
Date: Sat, 11 Apr 2026 08:50:12 GMT
Content-type: application/json; charset=UTF-8
Content-length: 51

{"error":"missing_name_or_content","success":false}
```

**Status:** Pass

-----

### Test 11: Upload Fails for Empty File Name or Content
**Objective:** Verify that upload requests with blank values are rejected.

**Request:**
```bash
curl -i -X POST http://localhost:8080/upload \
  -H "Content-Type: application/json" \
  -H "Session-Token: b980230e-868f-43f8-930d-7bd1643831b6" \
  -d '{"name":"","content":""}'
```

**Expected Result:**  
The server should return HTTP 400 with an error indicating empty name or content.

**Actual Result:**
```http
HTTP/1.1 400 Bad Request
Date: Sat, 11 Apr 2026 08:50:34 GMT
Content-type: application/json; charset=UTF-8
Content-length: 49

{"error":"empty_name_or_content","success":false}
```

**Status:** Pass

-----

### Test 12: Upload Succeeds with Valid Session and Valid Input
**Objective:** Verify that an authenticated client can upload a file.

**Request:**
```bash
curl -i -X POST http://localhost:8080/upload \
  -H "Content-Type: application/json" \
  -H "Session-Token: b980230e-868f-43f8-930d-7bd1643831b6" \
  -d '{"name":"test.txt","content":"123 this is a test file"}'
```

**Expected Result:**  
The server should return HTTP 200 and confirm the uploaded file name.

**Actual Result:**
```http
HTTP/1.1 200 OK
Date: Sat, 11 Apr 2026 08:50:42 GMT
Content-type: application/json; charset=UTF-8
Content-length: 34

{"name":"test.txt","success":true}
```

**Status:** Pass

-----

### Test 13: Download Is Rejected Without Session Token
**Objective:** Verify that download requires authentication.

**Request:**
```bash
curl -i -X POST http://localhost:8080/download \
  -H "Content-Type: application/json" \
  -d '{"name":"readme.txt"}'
```

**Expected Result:**  
The server should return HTTP 401 with an invalid or missing session error.

**Actual Result:**
```http
HTTP/1.1 401 Unauthorized
Date: Sat, 11 Apr 2026 08:50:51 GMT
Content-type: application/json; charset=UTF-8
Content-length: 54

{"error":"invalid_or_missing_session","success":false}
```

**Status:** Pass

-----

### Test 14: Download Fails for Nonexistent File
**Objective:** Verify that downloading a file that does not exist is rejected.

**Request:**
```bash
curl -i -X POST http://localhost:8080/download \
  -H "Content-Type: application/json" \
  -H "Session-Token: b980230e-868f-43f8-930d-7bd1643831b6" \
  -d '{"name":"does-not-exist.txt"}'
```

**Expected Result:**  
The server should return HTTP 404 with a file not found error.

**Actual Result:**
```http
HTTP/1.1 404 Not Found
Date: Sat, 11 Apr 2026 08:51:12 GMT
Content-type: application/json; charset=UTF-8
Content-length: 42

{"error":"file_not_found","success":false}
```

**Status:** Pass

-----

### Test 15: Download Succeeds with Valid Session and Existing File
**Objective:** Verify that an authenticated client can download an existing file.

**Request:**
```bash
curl -i -X POST http://localhost:8080/download \
  -H "Content-Type: application/json" \
  -H "Session-Token: b980230e-868f-43f8-930d-7bd1643831b6" \
  -d '{"name":"readme.txt"}'
```

**Expected Result:**  
The server should return HTTP 200 and include the file content in the JSON response.

**Actual Result:**
```http
HTTP/1.1 200 OK
Date: Sat, 11 Apr 2026 08:51:17 GMT
Content-type: application/json; charset=UTF-8
Content-length: 78

{"success":true,"name":"readme.txt","content":"This is a sample readme file."}
```

**Status:** Pass

-----

### Test 16: Invalid HTTP Method Is Rejected
**Objective:** Verify that handlers reject unsupported HTTP methods.

**Request Example 1:**
```bash
curl -i -X GET http://localhost:8080/auth
```

**Actual Result:**
```http
HTTP/1.1 405 Method Not Allowed
Date: Sat, 11 Apr 2026 08:51:24 GMT
Content-type: application/json; charset=UTF-8
Content-length: 46

{"error":"method_not_allowed","success":false}
```

**Request Example 2:**
```bash
curl -i -X POST http://localhost:8080/files \
  -H "Session-Token: b980230e-868f-43f8-930d-7bd1643831b6"
```

**Actual Result:**
```http
HTTP/1.1 405 Method Not Allowed
Date: Sat, 11 Apr 2026 08:51:29 GMT
Content-type: application/json; charset=UTF-8
Content-length: 46

{"error":"method_not_allowed","success":false}
```

**Expected Result (for both examples):**  
The server should return HTTP 405 for unsupported methods.

**Status:** Pass