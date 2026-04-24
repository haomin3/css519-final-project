# Automated Test Documentation: Private Cloud File Server
This document describes the automated tests for the Private Cloud File Server. The automated test set is divided into security tests and functional tests. Security tests verify that the system enforces protection mechanisms identified in the threat model, while functional tests verify that the main API workflows work as intended.

## 1. Test Approach and Environment
The automated tests are implemented in JUnit as part of the Java project. The test source files are located under `product-docker/src/test/java` and are run through Maven. These tests are intended to verify representative security and functional behavior of the API without repeating the full manual test set.

## 2. Automated Security Tests
### Test 1: Invalid Credentials Are Rejected
**Objective:** Verify that authentication fails when incorrect credentials are provided.

**Expected Result:**  
The server should return HTTP 401 with an `invalid_credentials` error.

-----

### Test 2: Protected Endpoint Rejects Missing Session Token
**Objective:** Verify that protected file operations require authentication.

**Expected Result:**  
The server should return HTTP 401 with an `invalid_or_missing_session` error.

-----

### Test 3: Malformed JSON Is Rejected
**Objective:** Verify that malformed JSON requests are not accepted.

**Expected Result:**  
The server should return HTTP 400 with an `invalid_json` error.

-----

### Test 4: Repeated Failed Authentication Causes Temporary Block
**Objective:** Verify that repeated failed login attempts trigger blocking behavior.

**Expected Result:**  
After the failed-attempt threshold is reached, the server should return HTTP 403 with a `temporarily_blocked` error.

## 3. Automated Functional Tests
### Test 1: Health Endpoint Responds Successfully
**Objective:** Verify that the health endpoint is reachable.

**Expected Result:**  
The server should return HTTP 200 with a success response.

-----

### Test 2: Authentication Succeeds with Valid Credentials
**Objective:** Verify that valid credentials are accepted.

**Expected Result:**  
The server should return HTTP 200 and issue a session token.

-----

### Test 3: File Listing Succeeds with Valid Session Token
**Objective:** Verify that an authenticated client can retrieve the file list.

**Expected Result:**  
The server should return HTTP 200 with a list of files.

-----

### Test 4: Upload Succeeds with Valid Session Token and Valid Input
**Objective:** Verify that an authenticated client can upload a file.

**Expected Result:**  
The server should return HTTP 200 and confirm the uploaded file name.

-----

### Test 5: Download Succeeds with Valid Session Token and Existing File
**Objective:** Verify that an authenticated client can download an existing file.

**Expected Result:**  
The server should return HTTP 200 and return the requested file content.

-----

### Test 6: Invalid HTTP Method Is Rejected
**Objective:** Verify that endpoints reject unsupported HTTP methods.

**Expected Result:**  
The server should return HTTP 405 with a `method_not_allowed` error when an unsupported method is used.
