# Free deployment: Render + Aiven MySQL

Spending limit approved by owner: $0. Select Free plans only, do not add payment details or enable paid upgrades.
Use Aiven MySQL Free, not a paid trial. Render Free sleeps after 15 idle minutes; open the site before the viva.
Free-service limits can change; verify the provider dashboard at creation. No paid disk/database/add-on is authorized.

## What this release adds
Docker builds React, embeds dist into the Spring Boot JAR, and serves UI plus /api from one HTTPS origin.
No separate frontend host, CORS changes, localhost proxy or IntelliJ is needed online.
The production profile honors PORT, forwarded HTTPS headers and Secure session cookies.
Normal local development on 8081/5173 remains available without this profile.

## Aiven account (user sign-in required)
Create one MySQL service and explicitly select Free ($0). Create an empty database named real_estate_portal_new.
Use a separate hosted account/database; do not upload the local database or reuse the real local ADMIN password.
Run the numbered initial schema scripts once, in order 00-06, against that empty hosted database.
Create a runtime user with SELECT/INSERT/UPDATE/DELETE privileges only. Keep schema admin separate.
Save the public CA certificate as aiven-ca.pem. Existing databases must be inspected, never dropped.

## Render account (user sign-in/GitHub authorization required)
Create a Docker web service from the repository's main branch, select the Free instance and disable automatic deploy.
The render.yaml blueprint also declares plan: free and auto-deploy off. Do not add a payment method.
Set environment variables privately:
- SPRING_PROFILES_ACTIVE=production
- DB_URL=jdbc:mysql://HOST:PORT/real_estate_portal_new?sslMode=VERIFY_IDENTITY
- DB_USER and DB_PASSWORD: the separate hosted runtime credentials.
Upload the public CA certificate as the secret file /etc/secrets/aiven-ca.pem.
container-start.sh imports it into an ephemeral trust store for verified TLS. The trust-store password is not a database password.
Do not place DB credentials in the URL, Git, Docker image, screenshots or this documentation.

## First hosted ADMIN
The hosted empty installation needs its own controlled first ADMIN. Temporarily add bootstrap-admin to the production
profile and supply BOOTSTRAP_ADMIN_USERNAME/PASSWORD/EMAIL/PHONE privately; deploy once. After creation, remove all four
bootstrap values and return to production profile only, then redeploy. This must never target the existing local ADMIN.
Log in over the Render HTTPS URL and change the initial hosted password. Do not publish credentials in documentation.

## Verify the live service before declaring deployment complete
Confirm root page/assets load and GET /api/auth/me works over HTTPS.
Use a temporary hosted BUYER/SELLER and dedicated demo ADMIN to test six-module CRUD, cookies, login/logout and CSRF.
Delete temporary records in child-to-parent order; keep separate intentional demonstration records.
Restart the service once: database data should persist; in-memory sessions are expected to log out.
Do not claim a public URL is working until these checks pass. Render may suspend the free service when quotas are exhausted.

## Build verification limits
The local release JAR can be built with frontend assets and exercised against MySQL without Docker.
The Docker image requires Docker or the hosting builder; absence of local Docker is not evidence the image built.
The production cookie must stay Secure online. Any HTTP-only local production-profile smoke check must explicitly override
cookie.secure=false for that isolated local check only.

## Provider references checked 4 November 2026
- https://render.com/docs/free
- https://render.com/docs/docker
- https://render.com/docs/blueprint-spec
- https://aiven.io/docs/products/mysql/concepts/mysql-free-tier

Current deployment status: account access and hosted provisioning pending. No hosted URL has yet been verified.

## Card-free fallback: Railway Free
The owner declined Render card verification. No card was added and no paid service was created.
Railway Free advertises $5 of trial credit for up to 30 days, then $1 of free credit monthly.
This is a limited evaluation deployment, not a guarantee of continuous free availability.
Do not choose Hobby/Pro or add a payment method. GitHub account verification must allow outbound MySQL access.
Use the same Dockerfile and production variables, plus AIVEN_CA_PEM containing the full public Aiven CA certificate.
The startup script writes that CA to a temporary file and imports it into the JVM trust store.
Use /api/auth/me as the health check, leave PORT to Railway, and use one service/one replica.
Keep the database runtime account restricted to data access; never send the database administrator credentials to hosting.
The live URL remains pending until an actual Railway deployment and browser/API verification succeed.
References checked 4 November 2026:
- https://docs.railway.com/pricing/free-trial
- https://docs.railway.com/pricing/plans
- https://docs.railway.com/builds/dockerfiles
- https://docs.railway.com/deployments/healthchecks
