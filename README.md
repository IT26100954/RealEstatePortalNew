# Real Estate Portal

A six-member project built with Java 21, Spring Boot, Maven, MySQL and React.
All six modules are integrated. Local evaluation mode works without cloud hosting.

Previously deployed demonstration (subject to free hosting limits): [https://real-estate-portal-production-3d7d.up.railway.app](https://real-estate-portal-production-3d7d.up.railway.app)

[Hosted access instructions and free-credit limits](docs/final/HOSTED-DEPLOYMENT.md).

## Final documentation
- [Final documentation index](docs/final/README.md)
- [Setup and running in IntelliJ](docs/final/SETUP-AND-RUN.md)
- [Final project guide PDF](docs/final/Final_Project_Guide.pdf)
- [Complete class diagram PDF](docs/final/Complete_Class_Diagrams.pdf)
- [Class inventory and full signatures](docs/final/CLASS-INVENTORY.md)
- [API and Postman reference](docs/final/API-REFERENCE.md)
- [Database reference](docs/final/DATABASE-REFERENCE.md)
- [Verification report](docs/final/TEST-REPORT.md)
- [Viva and handoff guide](docs/final/VIVA-AND-HANDOFF.md)

## Six modules
1. User and Access Management, including ADMIN roles.
2. Property Listing Management.
3. Moving Service Directory Management.
4. Viewing Appointment Management.
5. Offer Management.
6. Review and Feedback Management.

## Run locally
For evaluation, double-click **START-LOCAL.cmd** and open **http://127.0.0.1:8081**.
It serves React and the backend together, using existing local MySQL. Stop it with **STOP-LOCAL.cmd**.
See [local evaluation instructions](docs/final/LOCAL-EVALUATION.md).
For a second device on the same Wi-Fi, use **START-WIFI.cmd** and its printed network URL.
See [same-Wi-Fi instructions](docs/final/SAME-WIFI.md).

For the existing IntelliJ/Vite developer workflow:
Ensure MySQL is running and private backend/.env settings are configured.
Open this entire repository in IntelliJ, load backend/pom.xml, select Java 21,
and run com.realestate.PortalApplication normally without bootstrap-admin.
From frontend run npm ci once, then npm run dev. Open http://127.0.0.1:5173.
Keep backend 8081, frontend 5173 and MySQL running. Do not recreate existing tables.
Read the full setup guide before a fresh installation.

## Project workflow
Members work on feature branches and submit PRs targeting development.
Main release promotion is a separate reviewed action. Local credentials and build output stay ignored.
Preserve actual authorship and describe generated assistance accurately.
The final documentation is based on source commit 4aef4b56657c13fee5f19befe995195a378abe3a.
For the evaluation, use the tested local launcher; hosted availability depends on the external provider.

## Release and hosting
See [release status](docs/RELEASE-STATUS.md), [demo guide](docs/DEMO-GUIDE.md) and [free deployment](docs/DEPLOYMENT.md).
