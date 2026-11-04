# Shared React frontend

All six modules have shared frontend screens: accounts, property, moving services,
viewing appointments, offers and reviews. Role/ownership permissions are enforced by the backend.

Use Node meeting package.json (>=22.12.0). From this folder:
```bash
npm ci
npm run dev
```
Open http://127.0.0.1:5173. The development /api proxy targets Spring Boot on 8081.
MySQL and the backend must also be running. Use the same hostname consistently for sessions.
Production build: npm run build. Vite preview does not configure the deployment API proxy.

See [final setup](../docs/final/SETUP-AND-RUN.md),
[API reference](../docs/final/API-REFERENCE.md) and
[verification report](../docs/final/TEST-REPORT.md).
