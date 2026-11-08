# Local evaluation - no cloud hosting required

The completed React frontend can be built into the Spring Boot JAR. A single local Java process then serves the whole website and its API at **http://127.0.0.1:8081**. The existing local MySQL Server must remain running. This mode does not use Railway or Aiven and is separate from the disposable practice pack on 8082.

For sharing with another device on the same Wi-Fi, use **START-WIFI.cmd** and the detected Wi-Fi URL instead. See [same-Wi-Fi setup](SAME-WIFI.md). The START launchers automatically restart their own application when switching modes; existing login sessions sign out.

## Existing owner computer

1. Open the project folder in File Explorer: `C:\OOP PROJECT\RealEstatePortalNew`.
2. Ensure MySQL Server is running. Your existing `backend/.env` supplies the private local database username/password.
3. Stop any backend already running from IntelliJ on port 8081.
4. Double-click **START-LOCAL.cmd**. The first build can take a few minutes; it builds React and Java and runs backend tests.
5. The launcher waits until the frontend and session endpoint respond, then opens the browser. Log in with your **local** account, not your hosted account.
6. Keep the Java process and MySQL running during the evaluation. You can close IntelliJ and the launcher window after startup.
7. Double-click **STOP-LOCAL.cmd** when finished. It stops only the Java process tagged and recorded by this launcher; it does not stop MySQL or unrelated Java applications.

The launcher also works from Git Bash at the project root:

```bash
./START-LOCAL.cmd
./STOP-LOCAL.cmd
```

To force a fresh frontend/backend build, stop the launcher first and run:

```bash
./START-LOCAL.cmd -Build
```

The launcher normally rebuilds automatically when source files are newer than the packaged JAR. It needs Java 21. Node/npm and Maven (system, IntelliJ bundled, or Wrapper) are required when building. After a successful build, normal startup can reuse the JAR without internet downloads. Test the full local demo before relying on offline use; GitHub access still requires internet.

## Data and configuration

- Uses `jdbc:mysql://127.0.0.1:3306/real_estate_portal_new` explicitly, with credentials read from the existing ignored `backend/.env`.
- Does not create/drop tables, load sample data, reset passwords or run `bootstrap-admin`.
- Clears stale hosting/ADMIN-setup overrides only for the launched child process. It does not edit Windows environment settings.
- Uses a local-evaluation profile (without the production profile), localhost binding and HTTP cookies appropriate for the local URL. The hosted production configuration is unchanged.
- Writes process metadata and logs under ignored `.local/`. Do not commit or publish that directory.
- Keep the real ADMIN intact and use disposable local accounts for CRUD demonstrations. Referenced records may correctly reject deletion.

## Existing developer workflow still works

You may instead run PortalApplication in IntelliJ on 8081, then `npm run dev` from frontend and open `http://127.0.0.1:5173`. Do not run a second backend while the single-process launcher occupies 8081. Vite is not needed in launcher mode because the JAR already contains the built frontend.

## Troubleshooting

- Port occupied: stop the earlier backend yourself; the launcher refuses to kill an unknown process.
- Missing `.env`: configure your own local database connection; never copy hosted credentials into public files.
- Access denied/database connection refused: inspect local credentials and MySQL service status. Do not rerun initial SQL scripts on existing tables.
- Changed frontend not visible: stop, force `-Build`, then refresh the browser.
- Login fails: local and hosted passwords may differ. This launcher never changes an account password.
- Startup error: inspect `.local/local-evaluation-out.log` and `local-evaluation-error.log` privately.

Bring your full class diagram and keep the Git repository/history accessible. Running locally fulfils the demonstration setup described in your notice; individual backend understanding remains the main viva preparation.
