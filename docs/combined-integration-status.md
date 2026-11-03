# User, moving and appointment integration status

Prepared from the leader's saved local User/Admin, generated frontend and canonical
SQL scripts, plus moving fix 5687c7e and appointment fix 5393bc8. Changes are
AI-assisted; existing member commits remain in history. No real local credentials
are included. The original leader working copy is not modified by this branch.

## Included

- Complete User/Admin Java source, JPA/crypto dependencies and safe runtime settings.
- Shared frontend CRUD screens for six modules, session/token handling and Vite proxy.
- Canonical six table scripts and read-only schema inspection script.
- Reviewed moving-services and appointment implementations, with shared common code.
- Combined HTTP regression tests using disposable H2 databases.

## Verification

- Java21 / Spring Boot4.1.1: Maven test passed, 18 tests, zero failures/errors.
- Tests include real User registration/login/logout, password hashes and session
  invalidation, account permissions/role safeguards, moving CRUD, appointment
  workflow, validation, CSRF and concurrent appointment confirmation.
- Member-module fixture sessions exist only under src/test, never runtime login.
- Frontend production build passed (27 modules).
- Test fixtures now use full user columns and explicit column lists; Hibernate
  DDL is disabled in tests so it cannot replace the canonical test schema.

## Still pending — not a main release

- Actual combined MySQL8.4 and browser end-to-end checks.
- Property, offer and review backend implementations are not present in this branch.
  Their frontend screens and tables are provided, but related API calls cannot yet
  succeed. Missing endpoints are displayed as errors, not simulated successful data.
- Existing PR target branches must be development, not main.
- Do not rerun CREATE TABLE scripts against the existing leader database; inspect
  missing tables first and preserve its existing ADMIN account.
- Finish the leader's real-password rotation and CRUD verification locally.

This branch prepares source for review. It does not modify main/development or
claim the entire six-module project is ready for release.
