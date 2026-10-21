# Frontend foundation

Requires Node.js 22.12 or newer (Node 24 is supported) and npm.

From Git Bash:

```bash
cd frontend
npm ci
npm run dev
```

Open http://127.0.0.1:5173. Keep the terminal running. Ctrl+C stops Vite.
If this exact port is occupied, stop the old frontend first.

`npm run build` creates dist/. `npm run preview` previews built files on port 4173;
preview is not a production server and does not use the development API proxy.

Development requests beginning /api are forwarded to http://127.0.0.1:8081.
Use relative API paths. The proxy forwards requests; it does not implement endpoints.
A 404 is expected for endpoints that members have not written.

App.jsx is only the shared placeholder shell. Members write their own screens in
src/modules/<module> and coordinate App.jsx navigation changes. There is no login,
CRUD, router, shared authentication client or completed module UI in this scaffold.
Commit package-lock.json; do not commit node_modules/ or dist/.
