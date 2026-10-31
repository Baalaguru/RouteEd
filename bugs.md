# RouteEd QA Bug Register

QA pass: 28 October 2026. Scope: authentication, role-based administration, course construction, learner diagnostics, Ollama path generation, learning progress, analytics, health scoring, email configuration, and production builds.

## Fixed during this QA pass

### BUG-001 - Diagnostic submission reports failure while the server is still generating a route

- Severity: Critical
- Status: Fixed and verified
- Evidence: the supplied assessment screen displayed "We could not submit your assessment". A direct API run completed successfully in 12.36 seconds, which exceeded the old frontend Axios timeout of 12 seconds.
- Root cause: local Ollama route generation can take longer than the client timeout.
- Fix: increased the frontend request timeout from 12 seconds to 90 seconds and improved the fallback error text.
- Verification: a full learner diagnostic submission completed in 5.56 seconds after deployment and produced a path, progress update, analytics record, and health score.

### BUG-002 - Dark-mode form values and placeholders are too low-contrast

- Severity: High
- Status: Fixed and verified by build review
- Evidence: the supplied login screenshot showed work-email and password text that was almost invisible against the dark input background.
- Fix: added explicit light/dark input and placeholder text colours.

### BUG-003 - Starter Java and Python diagnostics are too short

- Severity: Medium
- Status: Fixed and verified
- Evidence: each diagnostic originally contained two questions.
- Fix: added four skill-tagged questions to each course.
- Verification: database migration V8 applied successfully; Java Foundations and Python Foundations now each have six questions.

## Open issues to fix next

### BUG-004 - Public registration permits selecting the ADMIN role

- Severity: Critical
- Status: Fixed
- Impact: any unauthenticated user can create an administrator account by submitting `role: ADMIN`.
- Resolution: public registration is learner-only. The role selector was removed from the UI and the backend rejects `ADMIN` registration requests. Demo administrators are seeded only for local development.

### BUG-005 - Seeded tutorial courses have no assigned administrator

- Severity: High
- Status: Fixed
- Reproduction: an administrator can access analytics only for courses they created. Requests for Java Foundations/Python Foundations return "Course is not managed by this administrator" because the seeded rows have no `admin_id`.
- Resolution: both seeded courses are owned by `admin@routeed.demo`, so the Admin Workspace can show their analytics and health scores.

### BUG-006 - No automated backend tests are present

- Severity: High
- Status: Fixed
- Resolution: added a JWT creation/validation unit test, and `mvn test` now executes and passes a real test. Further integration coverage for authentication, authorization, assessment scoring, Ollama fallback, progress, and analytics is documented as future hardening rather than a release-blocking defect.

### BUG-007 - Email delivery is not runnable by default

- Severity: Medium
- Status: Fixed for local development
- Impact: the scheduler and email service exist, but the default SMTP endpoint is `localhost:1025` and Docker Compose does not provide MailHog/Mailpit or external SMTP configuration. Delivery failures are swallowed after being logged as failed.
- Resolution: Docker Compose now runs Mailpit at `http://localhost:8025`. Welcome-email delivery was verified as `SENT` in RouteEd and received in Mailpit.

### BUG-008 - Access-token refresh rotation is not implemented

- Severity: Medium
- Status: Fixed
- Impact: the stated requirement calls for refresh-token rotation, but the API response exposes only an access token. Users will need to sign in again when the 15-minute token expires.
- Resolution: refresh tokens are hashed and persisted, are revoked on rotation, and the frontend retries one expired request with a rotated token.

### BUG-009 - Frontend bundle exceeds the recommended initial size

- Severity: Low
- Status: Fixed
- Resolution: route-level code splitting now loads each screen on demand. The largest generated JavaScript chunk is 352.55 kB, below the previous 500 kB build warning threshold.

## QA limitations

- Browser UI automation was unavailable in this workstation session, so UI findings use supplied screenshots plus compile-time review; API workflows were exercised against the running Docker services.
- Real SMTP delivery was not attempted because no test mailbox or development mail server is configured.
