# Operational Runbook: Private Cloud File Server
This document describes how an oncall engineer should respond when the Private Cloud File Server experiences a security-related failure, a serious operational issue, or other suspicious behavior that may require immediate investigation. The goal is to provide a practical process for identifying the problem, gathering relevant data, mitigating impact, restoring expected behavior, and confirming that the issue has been addressed. The runbook focuses on authentication, session control, request validation, file operations, and broader signs of service instability.

## 1. Common Investigation Workflow
### 1.1 Identify Problem
The first step is to identify what failed and how the behavior differs from what was expected. This may come from a failed automated test, a manual test, or an operational anomaly shown in the OE dashboard.

Examples could include:
- A protected endpoint accepts a request without a valid session token
- Authentication succeeds when invalid credentials are provided
- Repeated failed logins do not trigger temporary blocking
- Upload or download behavior does not match the expected API response
- Error rates for invalid JSON, missing session tokens, or unsupported methods increase unexpectedly

### 1.2 Data Gathering
Once the issue is identified, the oncall engineer should gather enough information to determine whether the issue is isolated, repeated, or part of a broader problem. For this project, the most useful sources are recent test output, API responses, server behavior, and OE dashboard data such as health status, blocked-user count, authentication trends, error trends, and recent operational events.

The oncall engineer should check:
- Which endpoint is involved
- Which request type and input caused the failure
- Which HTTP status code and JSON error were returned
- Whether the problem affects one request or multiple requests
- Whether related dashboard values changed, such as failed logins, blocked users, or recent error events

### 1.3 Mitigation and Recovery
After enough information is gathered, the next step is to reduce the impact of the issue and restore expected behavior. Since this project is fairly small in scale, mitigation may be simple and may involve temporarily limiting use of a feature, correcting a validation or session-related issue, restarting the service if needed, or restoring expected server behavior through a code or configuration fix.

The main goals are to:
- Stop the incorrect behavior from continuing
- Restore the affected endpoint or control
- Reduce the chance of further misuse while the issue is being corrected

### 1.4 Continued Monitoring
After mitigation and recovery, the oncall engineer should verify that the fix worked and that the problem is not still occurring. This can be done by running the failed test again, repeating the affected workflow, and checking the OE dashboard for continued abnormal behavior.

The oncall engineer should confirm:
- The original test cases now pass successfully
- The API now returns the expected result
- Related error counts or suspicious events are no longer increasing
- No new issues were introduced by the fix

---

## 2. Runbook Scenarios

### 2.1 Authentication or Session-Control Failure
#### 2.1.1 Identify Problem
This scenario applies when authentication or session-related protections are not working as expected. Examples include invalid credentials being accepted, protected endpoints accessible without a valid session token, or temporary blocking not triggering after repeated failed login attempts.

Typical signs include:
- A failed automated security test related to `/auth`, `/files`, `/upload`, or `/download`
- Unexpected HTTP 200 responses where HTTP 401 or HTTP 403 should have been returned
- A dashboard spike in failed authentication attempts or blocked users without normal control behavior

#### 2.1.2 Data Gathering
The oncall engineer should determine whether the issue is with credential validation, token validation, or failed-login handling. For this project, the most relevant checks are recent authentication-related tests, recent endpoint responses, blocked-user count, and authentication trend data. Authentication attempts and denied access attempts should also be treated as useful incident response signals.

The oncall engineer should gather:
- The exact request that triggered the issue
- The returned HTTP status code and JSON response
- Whether the session token was missing, invalid, or expired
- Whether repeated failures were recorded before the issue occurred
- Whether the issue is affecting only one endpoint or all protected endpoints

#### 2.1.3 Mitigation and Recovery
If authentication controls are failing, the immediate goal is to restore expected access control behavior. Depending on the issue, this may involve correcting auth logic, fixing session-token validation, restoring temporary blocking behavior, or restarting the affected service after a verified fix.

Possible actions include:
- Disable or avoid the affected workflow until the issue is corrected
- Fix credential-check or token-validation logic
- Restore repeated-failure handling if temporary blocking is not working
- Re-test protected endpoints after the change

#### 2.1.4 Continued Monitoring
After the issue is corrected, the oncall engineer should confirm that invalid credentials are rejected, protected endpoints once again require a valid session token, and repeated failed login attempts trigger temporary blocking as expected.

The oncall engineer should verify:
- Related automated security tests pass
- Authentication trends return to expected behavior
- Blocked-user counts and denied requests appear reasonable
- No protected endpoint is accessible without valid authentication

---

### 2.2 Malformed Request or API Misuse Spike
#### 2.2.1 Identify Problem
This scenario applies when the service begins receiving a large number of malformed requests, unsupported methods, or other invalid API usage. This may indicate client misuse, probing, or low-effort attack traffic rather than normal use.

Typical signs include:
- Numerous `invalid_json` errors
- Numerous `method_not_allowed` errors
- A sudden rise in rejected requests in the OE dashboard error graph

#### 2.2.2 Data Gathering
The oncall engineer should determine whether the traffic is coming from a broken client, a user mistake, or a suspicious request pattern. For this project, the main indicators are the specific error types being returned, which endpoints are involved, whether the issue is concentrated around one workflow, and whether recent operational events show repeated similar failures.

The oncall engineer should gather:
- Which error types increased
- Which endpoints are receiving the bad requests
- Whether the requests are malformed JSON, wrong methods, or missing required fields
- Whether the pattern appears isolated or repeated
- Whether there are related signs of probing or abuse

#### 2.2.3 Mitigation and Recovery
The immediate goal is to keep the service stable and make sure invalid requests are still being rejected properly. If the issue is caused by a client error, the client behavior should be corrected. If the pattern appears suspicious, the oncall engineer should continue rejecting the traffic and focus on preserving service availability.

Possible actions include:
- Confirm that request validation is still working
- Correct any client or test script sending bad requests
- Review whether repeated bad requests are reducing availability
- Restart the service only if it becomes unstable or validation behavior unexpectedly changes 

#### 2.2.4 Continued Monitoring
After mitigation, the oncall engineer should confirm that invalid requests are still rejected correctly and that the error spike is no longer growing abnormally.

The oncall engineer should verify:
- The affected tests now pass
- Error rates for malformed requests or unsupported methods return to normal
- Service health and response behavior remain stable
- No related issue has spread to other endpoints

---

### 2.3 Suspicious File Access or Modification Issue
#### 2.3.1 Identify Problem
This scenario applies when file-related behavior becomes suspicious or incorrect. Examples include unexpected upload behavior, incorrect file listings, unexpected download failures, or indications that files may have been overwritten, replaced, or accessed in an unintended way.

Typical signs include:
- A file workflow test fails unexpectedly
- Users report missing or changed file content
- The dashboard shows unusual upload, download, or file-not-found activity

#### 2.3.2 Data Gathering
The oncall engineer should determine whether the issue is related to authentication, file existence checks, overwrite behavior, or incorrect client input. Since the current system stores files in memory and allows files to be overwritten by filename, the investigation should focus on whether the file state changed unexpectedly and whether the requesting client was properly authenticated.

The oncall engineer should gather:
- The file name involved
- Whether the issue occurred during listing, upload, or download
- Whether the request was authenticated correctly
- Whether the file existed before the issue occurred
- Whether recent file-related events suggest overwrite or misuse

#### 2.3.3 Mitigation and Recovery
The immediate goal is to restore correct file behavior and prevent further unintended modification or access. In this small project, recovery may involve correcting request validation, restoring expected file handling logic, restarting the service if in-memory state is no longer trustworthy, or temporarily avoiding the affected operation until the issue is resolved.

Possible actions would include:
- Confirm that only authenticated requests can access file operations
- Correct file existence or upload/download handling if needed
- Restore expected behavior through a code fix or service restart
- Re-check whether overwrite behavior needs tighter control

#### 2.3.4 Continued Monitoring
After recovery, the oncall engineer should confirm that file listing, upload, and download behavior are once again operating as expected and that suspicious file-related events are no longer increasing.

The oncall engineer should verify:
- Related functional and security tests now pass
- File-related endpoints return expected responses
- No unusual increase remains in file-related errors or events
- Normal upload and download behavior has been restored
