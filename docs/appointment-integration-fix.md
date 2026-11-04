# Appointment integration correction

AI-assisted integration correction coordinated by the repository owner. The
member's earlier commits remain in history; the replacement should be reviewed
and understood by the appointment owner before the viva.

- Replaced the standalone com.appoiment demo with com.realestate.appointment.
- Removed HTML role-selector/dashboard endpoints and text-file persistence.
- Added the supplied model, DTOs, repository/service interfaces and implementations,
  plus JSON /api/appointments CRUD and PATCH /{id}/status.
- Uses existing appointments columns and shared CurrentUser/PropertyLookup helpers.
  New buyer identity comes from the session, not request input; no AGENT role.
- Added COMPLETED, future Sri Lanka date/time checks, filtered private reads,
  owner/role checks, pending-only deletion and terminal-status protection.
- Confirmation locks property and appointment and uses a current locking slot read
  in one transaction, allowing pending duplicates but only one confirmed exact slot.
- Added canonical 04_appointments.sql for schema reference. It was not executed
  against the leader's existing database and should not be rerun there.

Verification: Java21 Maven test succeeded: five appointment HTTP regression tests
and one existing startup test; zero failures/errors. Tests use disposable H2 MySQL
mode and a test-only session fixture under src/test. That fixture is NOT production
login. Real MySQL and the actual User/Admin login/frontend still need integration
verification. No production credentials or sample appointment text records remain.

Merge destination is development, not main. Do not merge before the owner reviews
the updated PR and coordinates its test schema with other integration tests.
