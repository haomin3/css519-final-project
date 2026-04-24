# Unknown Unknowns: Private Cloud File Server
This document summarizes potential risks that are not yet fully understood or tested in the current version of the Private Cloud File Server. These are not confirmed issues, but areas where additional weaknesses may exist and may become more important as the project evolves.

## Potential Unknown Unknowns
- Upstream dependencies such as Java libraries and runtime components may contain vulnerabilities or unexpected behavior that have not yet been identified.
- Future HTTPS/TLS support may introduce new risks related to certificate handling, protocol configuration, or secure-channel configurations.
- A future UI may add new attack surfaces, including client-side token handling and input validation, as well as other web-related issues not present in the current commandline workflow.
- Side-channel risks may exist beyond currently known examples such as timing differences during authentication.
- Additional availability or concurrency issues may appear under heavier load or more realistic multi client usage than has been previously tested so far.