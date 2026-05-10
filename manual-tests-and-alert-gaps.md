# Manual Tests and Alert Gaps: Cloudsploitable
This document lists manual tests that are not currently automated in the CI/CD pipeline and proactive alert metrics that are not yet included in the OE dashboard. The current automated tests cover the main API behavior, and the dashboard includes several operational indicators, but some risks are still better handled through manual validation or future monitoring improvements.

## 1. Manual Tests Not Currently Automated
| Manual Test | Why It Is Not Automated Yet | What It Would Check |
|---|---|---|
| OE dashboard visual review | The current CI/CD pipeline runs Java/JUnit tests and does not run browser-based UI tests. | Confirms that the OE dashboard loads correctly, including the summary cards, charts, customer disruption alert table, status badges, event table, and search box. |
| Docker runtime smoke test | The current automated tests run the Java server during testing, but they do not fully validate the packaged Docker runtime. | Confirms that the container can be built and run successfully, and that `/health`, `/auth`, `/files`, `/upload`, and `/download` work through the running container. |
| Sustained request or resource-use test | Load-style tests can be slow, environment-dependent, and potentially flaky in a normal CI pipeline. | Checks whether repeated valid login, file listing, upload, and download requests cause slower responses, failed requests, or unstable behavior. |
| File overwrite auditability review | Basic overwrite behavior can be automated, but the current product does not include file version history or detailed overwrite audit events. Because of that, a test cannot fully determine whether an overwrite was expected, accidental, or suspicious. | Checks whether an overwrite can be identified and investigated using available file name, upload time, and uploaded-by information. |

## 2. Proactive Alert Metrics Not Yet in the OE Dashboard
| Missing Metric | Why It Would Help | Current Limitation |
|---|---|---|
| 95th percentile response time | Average response time can hide slow individual requests. A percentile metric would better show whether some customers are experiencing poor performance. | The dashboard currently shows average response time only. |
| Request rate by endpoint | Helps identify whether unusual traffic is concentrated on `/auth`, `/files`, `/upload`, or `/download`. | The dashboard does not yet show request volume grouped by endpoint. |
| Error rate by endpoint | Helps determine whether failures are concentrated in login, session access, file listing, upload, or download. | The dashboard currently groups errors by error type, not by endpoint. |
| Consecutive failed health checks | Helps distinguish a brief temporary failure from an ongoing service outage. | The dashboard currently shows current health status but not a count of repeated health check failures. |
| Runtime resource usage | Helps detect resource pressure before customers experience failures. | The dashboard does not yet track CPU usage, memory usage, active request count, or thread usage. |