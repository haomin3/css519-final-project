# OE Dashboard Metrics: Cloudsploitable
This document describes the main data Cloudsploitable should emit so the OE dashboard can work properly. For this homework, the dashboard data is currently hard-coded, but the structure already exists for the dashboard to be connected to Cloudsploitable later if needed. The following metrics and event fields are the data needed for that connection.

## Dashboard Data Needed
### Top Summary Cards
The dashboard should receive data for:
- Health status
- Average response time
- Storage used
- Active sessions
- Blocked users

### Authentication Trend Graph
The dashboard should receive time-based authentication data showing:
- Successful authentication attempts
- Failed authentication attempts

The dashboard currently supports the following time ranges:
- Hourly
- Daily
- Weekly

### Error Trend Graph
The dashboard should receive time-based error data grouped by type.

The dashboard currently tracks the following error types:
- `invalid_json`
- `invalid_or_missing_session`
- `temporarily_blocked`
- `file_not_found`
- `method_not_allowed`

The dashboard currently supports the following time ranges:
- Hourly
- Daily
- Weekly

### Recent Operational Events Table
The dashboard should also receive recent operational events. Each event should include:

- Timestamp
- Severity
- Source
- Event name
- Short details

The recent events table currently displays entries such as:
- Health check succeeded
- Login failed
- User temporarily blocked
- File uploaded
- File downloaded
- Invalid JSON request
- File not found
- Unauthorized request rejected