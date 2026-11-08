# Local evaluation verification - 8 November 2026

Result: the six-module website runs from the packaged Java application at http://127.0.0.1:8081, connected to the existing local MySQL database. Railway and Aiven are not used in this mode.

## Checks completed

- React production build passed and its bundled HTML, JavaScript and CSS were served by Spring Boot.
- Maven: 28 backend tests, zero failures, zero errors, zero skipped. These tests use isolated H2 configuration.
- Real local MySQL: 56 HTTP checks passed using disposable BUYER, SELLER and ADMIN accounts and test records.
- Verified User/Admin, Property, Moving, Appointment, Offer and Review CRUD operations; account permissions, invalid review rejection, protected referenced deletion, appointment confirmation, password change/session invalidation and logout.
- Existing table counts were restored after cleanup; existing ADMIN account fingerprints were identical before/after. No existing account password was reset.
- Browser: registration, login, profile update, all five business-module list screens, account/profile screen and logout passed. No browser JavaScript errors were observed. The temporary browser account was removed.
- Launcher: repeated start reused its existing process; STOP-LOCAL stopped that marked process; restart restored the website. The final website is left running.

## Changes

- START-LOCAL.cmd and STOP-LOCAL.cmd provide simple local start/stop entry points.
- scripts/start-local.ps1 builds when required, locates Java 21/Maven, uses local configuration, checks readiness and records only its own process.
- README.md and LOCAL-EVALUATION.md document the local evaluation workflow.
- Business Java code, SQL schema scripts and hosted production configuration did not need changes for local operation.

## Limits

The browser check covers registration/profile flows and module rendering. The full CRUD paths were verified through real HTTP APIs; this is not a claim that every form interaction was repeated in the browser today. MySQL and the local Java process must remain running. The first build may require internet for dependencies; GitHub access during the viva also needs internet. Use the existing local credentials, which are separate from hosted accounts.

No commit, push, branch merge or hosting migration was performed by this local setup. Generated launcher assistance should be described accurately in any later commit/review.
