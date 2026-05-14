# Live OE Dashboard Questions and Remaining Gaps: Cloudsploitable
This document describes the operational questions the live OE dashboard should help answer now that it receives product metrics through `GET /oe`.

Some summary values, including the current timestamp, storage usage, active sessions, and blocked users, now come from the running product. Other dashboard values, including average response time, authentication and error trend charts, customer disruption alert metrics, and recent operational events, are still simulated, but they are now returned by the Java product instead of being stored only in the dashboard JavaScript. These simulated values could be replaced with collected runtime metrics later, but fully supporting that would require the product to save metric history over time, which would add unnecessary complexity for the current project scope.

## 1. Questions the Dashboard Should Be Able To Answer
- Is the product currently reachable?
- Is the product reporting a healthy operational state?
- Is average response time staying within a reasonable range?
- Is storage usage becoming a risk?
- How many sessions are currently active?
- How many users are currently blocked after failed login attempts?
- Are valid users able to log in?
- Are failed logins increasing?
- Are protected endpoints accepting valid sessions?
- Are unauthorized or missing-session requests increasing?
- Are file listing, upload, and download operations succeeding?
- Are file-related errors, such as missing files, increasing?
- Are malformed requests or invalid API usage increasing?
- Which error types are appearing most often?
- Are customer disruption risk areas currently OK, warning, or critical?
- What recent operational events help explain the current dashboard state?

## 2. Questions the Dashboard Can Partially Answer
- **Is the product getting slower over time?**  
  The dashboard shows average response time, but this value is still simulated instead of calculated from real request timing.

- **Are authentication failures increasing over time?**  
  The dashboard shows hourly, daily, and weekly authentication trends, but these trends are still simulated instead of being built from saved authentication history.

- **Are error responses increasing over time?**  
  The dashboard shows error trends by type, but these are still simulated instead of being built from saved error history.

- **Is one endpoint causing most failures?**  
  The dashboard shows error types such as `invalid_json`, `invalid_or_missing_session`, `temporarily_blocked`, `file_not_found`, and `method_not_allowed`, but it does not yet break those errors down by endpoint.

- **Are login disruption, session access disruption, or file operation disruption becoming customer-impacting problems?**  
  The dashboard includes customer disruption alert metrics, but those values are currently simulated rather than calculated from real success and failure rates.

- **Are file overwrites suspicious or expected?**  
  The dashboard can show file-related activity examples, but the product does not yet keep version history or detailed overwrite audit records.

- **Is traffic abnormal compared to normal usage?**  
  The dashboard can show trends and recent events, but it does not yet track real request volume, client IPs, or normal baseline behavior.

## 3. Remaining Gaps
- Some dashboard values are still simulated, including average response time, authentication trend data, error trend data, customer disruption alert metrics, and recent operational events.
- Runtime product state and file data are stored in memory and reset when the product restarts.
- Dashboard metrics are useful for current runtime state but not long-term analysis because the product does not yet save metric history over time.
- The product does not yet collect real CPU, memory, thread, or container resource metrics.
- The dashboard does not identify client IPs or request sources.
- The dashboard does not yet break error rates down by individual endpoint.
- File overwrite visibility is still limited because there is no version history.
- The dashboard does not yet support alert notifications outside the UI.