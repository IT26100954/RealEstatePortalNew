# Shared frontend and MySQL setup

## What this setup contains

Minimal React/Vite shell, six module placeholders, development API proxy,
Spring JDBC and MySQL driver configuration, and an isolated H2 test datasource.
No models, DAOs, services, API controllers, authentication or module tables are implemented.
Setup code is shared infrastructure, not individual module implementation.

## Frontend

From Git Bash at the repository root:

```bash
cd frontend
npm ci
npm run dev
```

Open http://127.0.0.1:5173. Use that same hostname consistently for the development
frontend, so future session/CSRF cookies are not split across localhost and 127.0.0.1.
The /api proxy targets port 8081. Endpoints return 404 until members implement them.
The production deployment/API arrangement will be a separate reviewed change.

## This computer's MySQL provisioning

The setup created ignored local credentials and a local provisioning script.
MySQL Server 8.4 is already installed. From the repository root in Git Bash run:

```bash
bash scripts/setup-local-mysql.sh
```

Enter the MySQL administrator password locally. Do not send it in chat or put it
in Git. The script creates the empty real_estate_portal_new database and a uniquely
named app account, grants only SELECT/INSERT/UPDATE/DELETE, then verifies that
account. There are no DROP statements and it does not modify the backup database.
Run this provisioning step once; CREATE USER will reject a repeated existing name.
If it reports an error, inspect it instead of deleting/resetting an existing user.

The administrative account is only for setup and each member's schema scripts.
The Java application uses its restricted app account for runtime CRUD.
Database existence/permissions do not imply module tables or CRUD are implemented.

## Other teammates' database setup

Local credential files are deliberately absent from GitHub. Each member:

1. Installs/starts MySQL and connects as their own administrator in Workbench.
2. Runs database/00_create_database.sql.
3. Creates their own app account in Workbench with a unique local password and
   SELECT/INSERT/UPDATE/DELETE permissions on real_estate_portal_new.*.
4. Copies backend/.env.example to backend/.env and fills in their own app details.
5. Runs module table scripts as administrator once those scripts are implemented
   and reviewed. There are no completed module table scripts in this setup.

The app reads backend/.env automatically when launched from either backend or
the repository root. It uses Java properties syntax, not Bash export syntax.
Environment variables already present in a run configuration override this file;
remove stale DB_URL/DB_USER/DB_PASSWORD values inherited from an old project.

## Backend startup and tests

Select Java 21, reload backend/pom.xml in IntelliJ, and run PortalApplication.
Stop the old backend first because it also uses port 8081. The root may return
404 because no root controller is included. A startup message alone does not
verify MySQL connectivity; use the app-account verification above.

Run PortalApplicationTests in IntelliJ or run Maven test from backend.
On the leader's machine the verified Maven executable is:

```bash
export JAVA_HOME="C:/Users/lapto/.jdks/ms-21.0.12.1"
"/c/Program Files/JetBrains/IntelliJ IDEA 2026.1/plugins/maven/lib/maven3/bin/mvn.cmd" test
```

Tests use backend/src/test/resources/application.properties and H2 memory storage,
not local MySQL. This prevents ordinary tests using a member's real data. Future
MySQL integration tests must use a separate explicit test database.

The Maven Wrapper first-download problem is documented in setup-guide.md.
It remains separate from this setup; use bundled Maven if needed.

## Never commit local secrets or generated dependencies

Ignored: backend/.env, backend/.mysql.local.cnf, database/.local-setup.sql,
node_modules/, dist/, target/, .idea/. Commit the placeholder .env.example and
frontend/package-lock.json. Review git status and staged changes before committing.

## Next work for members

Review the pending database/API contract PR. Use module-owned feature branches.
Write your own Java models, SQL tables, DAOs, services, controllers, tests and
screens. Coordinate changes to shared App.jsx, security integration and build files.
