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

Hosting budget: $0. Recommended Render Free + Aiven MySQL Free, no payment method or paid trial.
The free cloud service is not yet live: provider email verification, database provisioning,
GitHub access authorization and online validation are pending.
Local Docker is unavailable, so the Docker build must still be verified by the hosting builder.
See DEPLOYMENT.md. Do not claim a deployed URL until the actual online checks pass.

Source class-diagram snapshot remains 4aef4b5 because deployment preparation added no production Java types.
Private demo access details are saved only in ignored .local; they are not in GitHub or release assets.
