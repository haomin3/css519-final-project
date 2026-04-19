# Oncall Dashboard Analysis: Private Cloud File Server
This document explains how an oncall engineer could use the OE dashboard if something goes wrong with the Private Cloud File Server. The dashboard is meant to help with quick triage by showing current health, response time, storage usage, active session and blocked user counts, authentication trends, error trends, and recent operational events.

## How an Oncall Engineer Could Use the Dashboard
### 1. Check the top summary cards first
The oncall engineer should first look at the top summary cards to get a quick sense of whether the issue is more related to availability, performance, storage, or authentication behavior. This includes:
- Health status
- Average response time
- Storage used
- Active sessions
- Blocked users

### 2. Review the authentication graph
The authentication graph helps show whether login behavior is staying normal or changing in a suspicious way, which can help separate normal usage from repeated failures or possible abuse. For example:
- A spike in failed logins may suggest brute-force activity or repeated user login mistakes
- A drop in successful logins may suggest a broader authentication problem

### 3. Review the error graph
The error graph helps show whether one type of error is increasing more than others, which makes it easier to narrow the issue to a smaller part of the product instead of treating it like a general failure. For example:
- More `invalid_json` errors may suggest malformed requests or client misuse
- More `invalid_or_missing_session` errors may suggest session problems or unauthorized requests
- More `temporarily_blocked` errors may suggest repeated failed login attempts
- More `file_not_found` errors may suggest incorrect download requests
- More `method_not_allowed` errors may suggest incorrect API usage

### 4. Review the recent operational events table
The recent operational events table gives more detail than the summary cards and graphs alone. It helps the oncall engineer quickly check what has happened recently, and the search bar can also be used to quickly search through logs if they want to look for something more specific.

The oncall engineer can use it to quickly look for:
- Repeated login failures
- Temporary blocks
- Invalid JSON requests
- File-related errors
- Unusual upload or download activity
- Recent event patterns that help explain what changed