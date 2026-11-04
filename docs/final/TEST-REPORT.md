# Verification report
Date: 4 November 2026. Final merged source: 4aef4b56657c13fee5f19befe995195a378abe3a.

## Final merged checks
28 backend regression tests passed, zero failures/errors.
23 browser checks for User/Admin, Property, Moving, Appointments and Offer passed against the merged backend.
20 Review browser checks passed against the same merged backend. Total final browser checks: 43.
Frontend production build passed. Temporary MySQL records were removed; all six tables matched the starting snapshots.
The real ADMIN was not used for destructive tests and was not modified.

## Regression suite
- AppointmentIntegrationTest.java: 5 test methods.
- MovingCompanyIntegrationTest.java: 3 test methods.
- OfferIntegrationTest.java: 1 test methods.
- PortalApplicationTests.java: 1 test methods.
- PropertyIntegrationTest.java: 3 test methods.
- ReviewIntegrationTest.java: 4 test methods.
- UserAdminIntegrationTest.java: 11 test methods.

## Retained execution evidence
The JSON files below are copied from actual run outputs and contain check names, not passwords or real user records.
- Frontend CRUD, first five modules: 23 passed groups; test-evidence/frontend-five-results.json.
- Review browser CRUD on merged tree: 20 passed groups; test-evidence/merged-six-review-results.json.
- Property MySQL: 18 passed groups; test-evidence/property-mysql-results.json.
- Moving + Appointments MySQL: 25 passed groups; test-evidence/remaining-mysql-results.json.
- Offer MySQL: 19 passed groups; test-evidence/offer14-mysql-results.json.
- Review MySQL: 18 passed groups; test-evidence/review20-mysql-results.json.

## Scope and limits
Checks cover CRUD, role/ownership rules, status transitions, CSRF, malformed input, references, sessions and concurrent decisions.
Concurrent appointment confirmations resulted in one success and one conflict; accepted/withdrawn offer race similarly serialized.
User password changes invalidated multiple sessions; old password rejected and new password accepted.
Tests do not establish load capacity, accessibility certification, every browser/device, external network security or hosted operation.
Last-admin protection was checked in isolated tests, not by removing the real ADMIN from the live database.
An early Offer API run reported a snapshot mismatch; disposable records were absent, and the repeat run matched all tables. Cause was not established.
These counts are separate test groups/cases; do not combine earlier overlapping checks into an invented coverage percentage.
Do not use the live QA automation against unrelated data without preserving its temporary-account isolation and cleanup.

## Public hosted deployment verification - 4 November 2026
43 public-website browser checks passed against Railway + real Aiven MySQL, plus production session-cookie, CSRF and hosted ADMIN login/logout verification. Evidence: test-evidence/railway-*-results.json. Temporary test rows were removed; deliberate demo rows were added separately afterward. See HOSTED-DEPLOYMENT.md.
