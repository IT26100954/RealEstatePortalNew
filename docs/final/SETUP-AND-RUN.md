# Setup and running guide

## Existing leader computer
The existing database and ADMIN already exist. Do not recreate tables or run bootstrap-admin again.
Open C:\OOP PROJECT\RealEstatePortalNew in IntelliJ. Select development and reload backend/pom.xml.
Use Java 21 for Project SDK and Maven. Ensure MySQL is running.
Open PortalApplication and run its main method. Active profiles must be blank for normal startup.
Remove any old BOOTSTRAP_ADMIN_* run variables; keep your own DB settings private.
Look for Tomcat on 8081 and Started PortalApplication.

In IntelliJ's Git Bash terminal:
```bash
cd "/c/OOP PROJECT/RealEstatePortalNew/frontend"
npm run dev
```
Open http://127.0.0.1:5173 and keep both processes running.
Use 127.0.0.1 consistently; changing between localhost and 127.0.0.1 separates browser cookies.

## Fresh clone for another computer
Install Git, JDK 21, IntelliJ, Node (meeting package.json), MySQL Server and optionally Workbench/Postman.
```bash
git clone https://github.com/IT26100954/RealEstatePortalNew.git
cd RealEstatePortalNew
git fetch origin
git switch development
git pull --ff-only origin development
```
This is a private repository: the user must have GitHub access. Open the full cloned folder in IntelliJ.
Right-click backend/pom.xml -> Add as Maven Project if it is not already loaded; reload Maven.
Do not create another pom.xml or a second project inside a Java package.

## Database on a fresh computer only
1. Connect to your local MySQL server in Workbench with an administrator account.
2. Run database/00_create_database.sql, then 01_users.sql, 02_properties.sql,
   03_moving_companies.sql, 04_appointments.sql, 05_offers.sql, 06_reviews.sql in that order.
3. These CREATE TABLE scripts are initial setup scripts, not repeatable migrations.
   If a table exists, inspect it; do not drop it or rerun the script to fix an unrelated error.
4. Create a local runtime account with SELECT, INSERT, UPDATE and DELETE on real_estate_portal_new.*.
   Keep administrator privileges for schema setup only. Do not use another member's credentials.
5. Copy backend/.env.example to backend/.env and fill in your own local DB_URL, DB_USER and DB_PASSWORD.
   This .env is Java properties syntax, not a Bash script. Never commit it.
6. Run database/check_tables.sql to inspect the six tables and columns.

The repository setup-local-mysql.sh depends on ignored machine-local files; a new clone does not contain them.
Do not assume that script can provision a fresh computer without following the database steps above.

## First ADMIN on a fresh empty installation only
Public registration creates BUYER or SELLER, never ADMIN. For an installation with zero ADMIN accounts,
the controlled bootstrap runner uses active profile bootstrap-admin and local variables:
BOOTSTRAP_ADMIN_USERNAME, BOOTSTRAP_ADMIN_PASSWORD, BOOTSTRAP_ADMIN_EMAIL, BOOTSTRAP_ADMIN_PHONE.
Choose private values locally. After it creates the first ADMIN, stop the app, clear the profile and
these four variables, then run normally. Never run this against the existing leader account as a routine step.

## Build and regression checks
```bash
cd backend
./mvnw test
cd ../frontend
npm ci
npm run build
npm run dev
```
The wrapper downloads Maven on first use and needs network/certificate access. On this computer,
the first wrapper download previously failed with CRYPT_E_REVOCATION_OFFLINE. IntelliJ bundled Maven
works as an alternative; do not disable certificate verification as a project fix.
From the repository root in Git Bash, the verified local alternative is:
```bash
"/c/Program Files/JetBrains/IntelliJ IDEA 2026.1/plugins/maven/lib/maven3/bin/mvn.cmd" -f backend/pom.xml test
```
This executable path is machine-specific. Tests use H2 from src/test/resources, not your MySQL database.
In IntelliJ use Maven Lifecycle -> test, or run the test classes. Backend and frontend are separate processes.

## Working on your own module
Use your assigned feature branch. Fetch and merge origin/development into it before new work.
Preserve unfinished changes by committing them appropriately or using a named stash including untracked files.
Never reset --hard to solve uncertainty. Inspect conflicts and build after resolving them.
Stage explicit files, inspect git diff --cached --stat, then commit and push your feature branch.
Create PRs with base development. Only the agreed reviewer merges. Main promotion is a separate action.
Do not commit .idea, *.iml, target, node_modules, dist, backend/.env or backend/.mysql.local.cnf.
Role-based workflow agreements do not enforce branch restrictions: check whether the GitHub plan actually enforces rules.

## Troubleshooting
- Port 8081/5173 occupied: stop the earlier process, not a random unrelated program.
- Database access denied: verify private local credentials and stale IntelliJ environment overrides.
- No database selected: select real_estate_portal_new or use the script's USE statement.
- Table already exists: do not rerun CREATE TABLE; inspect the existing schema.
- API root returns 404: there is no required root API page; use /api/auth/me or the frontend.
- API write returns 403: check login/role and X-CSRF-TOKEN. Refresh /auth/me; login changes the token.
- Record change returns 409: inspect references, terminal state or concurrent change; this can be expected protection.
- UI empty list: no permanent sample records may exist. The tests deliberately removed their temporary records.
- Other module folders missing: verify branch and full cloned folder; reload/synchronize IntelliJ after Git changes.
