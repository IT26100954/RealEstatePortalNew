# Real Estate Portal - final project guide

Date: 4 November 2026. Verified source: `4aef4b56657c13fee5f19befe995195a378abe3a` (`development`).

## What the application does
A six-member real estate portal supports buyer/seller/admin accounts, property listings,
moving-company listings, viewing appointments, offers, and reviews. Each module has create,
read, update and delete operations, subject to ownership, role and record-state rules.
Some records are deliberately retained: for example accepted offers and confirmed appointments
cannot be deleted through normal CRUD. This protects transaction history.

## Technology actually used
- Java 21; Spring Boot 4.1.1; Maven 3.9.11 through the project wrapper or IntelliJ's bundled Maven.
- Spring Web MVC and embedded Tomcat handle HTTP requests.
- User persistence uses Spring Data JPA/Hibernate; the other five modules use JdbcTemplate.
- Spring Security Crypto provides PBKDF2 password hashing. This is not a full Spring Security filter-chain setup.
- MySQL stores persistent application records; H2 is used by backend regression tests.
- React 19.3.0 and Vite 8.3.4 provide the browser interface. package.json requires Node >=22.12.0.
- Git/GitHub provides branches, commits and pull-request review. No Groovy or Gradle build is used.

## Request flow
Browser -> Vite /api proxy -> Spring controller -> service -> repository/DAO -> MySQL.
Controller maps URLs and JSON. Service decides permissions and business rules.
Repository/DAO reads and writes records. DTOs define allowed inputs and safe outputs.
Constructor injection supplies dependencies; the controller does not construct a service with new.

## Real design differences
UserRepository extends JpaRepository; Spring Data supplies its implementation at runtime.
There is no hand-written UserRepositoryImpl. UserService and AuthService have concrete implementations.
Property, Appointment, Offer and Review use repository interfaces and JdbcTemplate implementations.
Moving uses a concrete MovingCompanyService and MovingCompanyDAO, not an invented service interface.
MovingCompany is a Java record; it has component accessors such as id(), not getId().
Role is an enum with BUYER, SELLER and ADMIN. ADMIN is a User role, not a separate account entity.

## Authentication and safety
Login verifies a PBKDF2 hash, replaces the session and returns a CSRF token.
The session identifies the account by userId; CurrentUser reloads its role from the database.
Every API write requires the session CSRF token in X-CSRF-TOKEN, including register/login/logout.
Use GET /api/auth/me first and refresh the token after login. Password changes invalidate tracked sessions.
The cookie is HttpOnly with SameSite=Lax; inactive sessions expire after 30 minutes.
UserResponse never returns the password hash. Ownership comes from the authenticated account.
The last ADMIN, self-deletion of ADMIN, referenced users and referenced records have protections.

## Current completion
All six backend modules are present in development. The merged tree passed 28 backend tests,
43 browser checks using real MySQL (23 for the first five modules plus 20 for Review), and the frontend build.
These are executed checks, not proof of every possible production scenario.
Tests used disposable accounts/data and verified original table contents after cleanup.
Existing ADMIN credentials are not part of the documentation or test pack.

## Release boundaries
The application currently runs locally with frontend, backend and MySQL processes.
Deployment still requires a chosen hosting account, a separate hosted database, HTTPS,
environment settings and deployed URL/session tests. Vite preview is not the configured API hosting solution.
The old accidental Offer merges in main remain separate from the tested development release.
The prepared revert proposal is not part of this documentation task; review GitHub before a main release.

## Project structure
backend/src/main/java/com/realestate: application, common helpers and six module packages.
backend/src/test: seven integration/startup test classes and isolated H2 settings/schema.
frontend/src/modules: user screens and module configuration; shared contains forms, session and API handling.
database: numbered initial schema scripts 00-06 and a read-only inspection script.
docs/final: this final reference pack. Earlier setup notes describe previous stages and may be historical.
