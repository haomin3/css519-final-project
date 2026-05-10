# Customer Disruption Risks: Cloudsploitable
This document identifies the highest operational risks that could disrupt normal customer use of Cloudsploitable. The selected risks are login disruption, session or protected endpoint access disruption, and file operation disruption.

These risks were selected because they directly affect whether valid customers can log in, access protected endpoints with a valid session, and complete file listing/upload/download operations. They also map cleanly to early alert metrics in the OE dashboard and automated tests in the CI/CD pipeline. A failure in any of these areas could prevent customers from using the product even if the basic `/health` endpoint still indicates that the server is online.

## 1. Login Disruption
A valid customer may be unable to log in and receive a session token. Since authentication is the first step before using protected file operations, login disruption would block normal use of the product immediately. This risk could be caused by authentication logic errors, credential-checking bugs, unexpected lockout behavior, or a failure in the `/auth` endpoint.

**Customer impact:**
- Valid users cannot authenticate
- Users cannot receive session tokens
- File listing, upload, and download are unavailable because authentication never succeeds

**New OE dashboard metric:**
- Valid Login Success Rate

**OE Dashboard alert rule:**
- OK: 90% or higher
- Warning: 75% to 89%
- Critical: below 75%

**New related automated test:**
- `blockedUserDoesNotPreventOtherUsersFromLoggingIn()`: Verifies that lockout behavior for one username does not accidentally prevent other valid users from logging in.

---

## 2. Session or Protected Endpoint Access Disruption

A customer may successfully log in but still be unable to access protected endpoints using a valid session token. This would create a confusing customer experience because authentication appears successful, but file operations still fail. This risk could be caused by broken session-token validation, inconsistent protected endpoint behavior, or incorrect session handling across `/files`, `/upload`, and `/download`.

**Customer impact:**
- Users can log in but cannot use the product
- Protected endpoints may reject valid session tokens
- Customers may see unauthorized errors even when their session should be valid

**New OE dashboard metric:**
- Protected Endpoint Access Success Rate

**OE Dashboard alert rule:**
- OK: 90% or higher
- Warning: 75% to 89%
- Critical: below 75%

**New related automated test:**
- `validSessionCanAccessAllProtectedFileEndpoints()`: Verifies that a valid session token works across the protected file endpoints.

---

## 3. File Operation Disruption
The service may be running and authentication may work, but core file operations may fail. This is a high customer risk because file listing, upload, and download are the main purpose of the product. This risk is different from general service health, in that a simple health check may still return success while one or more file operations are failing.

**Customer impact:**
- Users cannot reliably list files
- Users cannot upload files
- Users cannot download files that should be available
- The product appears online but does not perform its main function

**New OE dashboard metric:**
- File Operation Success Rate

**OE Dashboard alert rule:**
- OK: 90% or higher
- Warning: 75% to 89%
- Critical: below 75%

**New related automated test:**
- `fileOperationFlowWithValidSessionSucceeds()`: Verifies that a valid user can list files, upload a file, confirm it appears in the file list, and download the expected content.