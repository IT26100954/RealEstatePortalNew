# Release handoff - 4 November 2026

All six modules are complete. Final documentation PR #21 is merged into development.
The release adds a single packaged React/Spring Boot application, Docker configuration,
verified-TLS database support, production Secure cookies and a Render Free blueprint.
Local demonstration records and three separate demo accounts are prepared; real ADMIN and original records unchanged.

Validation completed for this release:
- 28 backend tests passed during clean release packaging.
- React production build passed and index/assets were verified inside the executable JAR.
- 43 browser checks passed against the packaged JAR and real local MySQL (23 + 20).
- The packaged app was exercised with a 256 MB Java heap.
- Disposable test records removed; intentional demo records retained separately afterward.

Hosting budget: $0. Live demonstration: https://real-estate-portal-production-3d7d.up.railway.app
Railway Free and Aiven MySQL Free are configured without adding payment details.
The actual Docker image was built and deployed by Railway. 43 additional public-website browser checks passed;
production cookie, CSRF and hosted ADMIN login/logout checks also passed. Temporary accounts were cleaned up.
Separate hosted demonstration records are retained. See final/HOSTED-DEPLOYMENT.md for private access-file locations,
free-credit limits and the evaluation walkthrough. Render was not deployed because its account required card verification.

Source class-diagram snapshot remains 4aef4b5 because deployment preparation added no production Java types.
Private demo access details are saved only in ignored .local; they are not in GitHub or release assets.
