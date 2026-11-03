# Setup progress

## Completed shared infrastructure

* Existing Java 21 / Spring Boot / Maven backend foundation retained.
* Six-member React/Vite placeholder shell created; no business screens implemented.
* Frontend dependencies and package-lock.json installed/generated.
* Frontend production build passed and browser rendering was checked.
* Development /api proxy targets backend port 8081.
* Spring JDBC, MySQL driver and test-only H2 dependencies configured.
* Startup test passed: 1 test, 0 failures, 0 errors.
* Isolated test datasource added; tests do not use local MySQL credentials.
* Non-destructive empty database script and local provisioning helper prepared.
* Local credential files verified ignored by Git.

## Pending local verification

* Run bash scripts/setup-local-mysql.sh from the repository root and enter the
local MySQL administrator password. Database and app-account creation and
real MySQL connectivity are not verified until this succeeds.
* Reload Maven in IntelliJ after the dependency changes.
* Review changes and publish a setup PR. No setup changes were committed or pushed automatically.

## Pending team implementation

* Review/clarify the separate database/API proposal, including each member's
explicit CRUD scope, three screens, deletion rules and appointment status values.
* Members implement all module models, table scripts, DAOs, services, API endpoints,
authentication/authorization, business tests and functional screens themselves.
* The Maven Wrapper's first-download issue remains documented; bundled Maven works.

## Branch

Shared setup work is on setup/shared-frontend-mysql, based on development.
The existing contract proposal remains on docs/database-api-contract.



\## Additional verification completed



\- MySQL provisioning succeeded and the app account connection was verified.

\- Maven was reloaded in IntelliJ.

\- Backend started successfully using Java 21 on port 8081.

