# Shared frontend and MySQL completion

Generated supporting work; Java implementation remains owned by members.
No Java files or existing SQL scripts were modified. The frontend replaces
the previous placeholder shell. It contains no fake successful CRUD data.

## Run
From the repository root in Git Bash:

```bash
cd frontend
npm ci
npm run dev
```

Open http://127.0.0.1:5173. Use this same hostname for the session cookie.
Run the backend on 8081. Vite proxies /api to it. npm run build verifies
the frontend bundle; npm run preview is a static preview, not API integration.

## Database (Workbench administrator connection)
1. Run database/check_tables.sql (read-only).
2. users already exists on the leader's PC. Do not rerun 01_users.sql there.
3. Run only missing table scripts in order: 02, 03, 04, 05, 06.
4. If a table already exists, compare SHOW CREATE TABLE against its script.
   Do not drop a table or assume CREATE IF NOT EXISTS validates its schema.
5. Scripts deliberately stop on existing tables. They contain no DROP,
   TRUNCATE, test accounts or changes to existing user data.
6. These files do not execute automatically. Back up local data before
   applying future schema changes; run only on real_estate_portal_new.

Constraints protect references, valid statuses, positive prices and ratings.
Role ownership, future dates, status transitions and atomic slot confirmation
remain Java service responsibilities. Do not enable Hibernate create/create-drop.

## Screens
User: register, login, profile/edit/password, ADMIN account list, detail,
create/edit and separate role update.
Property: searchable list, details, create/edit, delete, viewing/offer/review links.
Moving: public directory, company details, ADMIN create/edit/delete.
Appointment: list, request, detail, pending edit/delete and permitted status actions.
Offer: list, submit, detail, pending edit/delete and permitted status actions.
Review: list, create, detail, author edit and author/ADMIN delete.

No endpoints are fabricated. Backend errors appear on screen with retry/session
refresh options. Missing backend code means its related actions cannot yet work.
Client-side hidden buttons improve usability; backend authorization is mandatory.
No initial ADMIN is created by SQL or frontend. Member 1 supplies the controlled
first-admin process agreed in the contract.

## Exact backend interface to implement
All list GETs return arrays; detail/POST/PUT return objects with id and camelCase
fields; DELETE returns 204. IDs are strings, money decimal strings.
Routes: /api/users, /api/properties, /api/moving-companies, /api/appointments,
/api/offers, /api/reviews. Standard /{id} GET, PUT, DELETE; base GET, POST.
Property search: ?q=...&city=... . Lists enforce visibility on the server.
Appointment/offer content PUT excludes propertyId, buyerId and status.
PATCH /api/appointments/{id}/status and /api/offers/{id}/status accept {status}.
PUT /api/users/{id}/role accepts {role}; /password accepts currentPassword,
newPassword, returns 204 and invalidates sessions.
GET /api/auth/me creates an anonymous session and returns {user:null,csrfToken}
or {user:{id,username,email,phone,role},csrfToken}; no-store.
POST /api/auth/login accepts {username,password}, rotates session/token and
returns the same user+csrfToken envelope. POST /register creates an ordinary
account without login. POST /logout invalidates session and returns 204.
Every write, including login/register, sends X-CSRF-TOKEN and includes cookies.
Failures use {code,message}, with agreed 400/401/403/404/409 status codes.

## Integration checklist before claiming complete
- Each backend list/detail/create/update/delete tested against actual MySQL.
- BUYER, SELLER and ADMIN sessions; refresh, logout, expiry, wrong permissions.
- Username conflict; linked-user/property delete 409; last-admin protection.
- Password change invalidates all sessions; responses never expose hashes.
- Appointment double-confirm race, invalid transitions and terminal records.
- Seller offer decisions, buyer withdrawal; rating and money validation.
- Browser forms and console/network checks; mobile and keyboard use.
- Full class diagram must come from the actual member-written Java code.

This supporting code does not implement any Java endpoint. Record assistance
accurately in contribution documentation. No commit or push is performed for you.
