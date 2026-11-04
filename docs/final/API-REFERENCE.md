# API reference

Backend base: http://127.0.0.1:8081. Frontend proxy base: http://127.0.0.1:5173/api.
Use the browser frontend hostname consistently. IDs below are UUIDs. Money is a JSON string in LKR.

## Session and CSRF sequence
1. GET /api/auth/me; keep the JSESSIONID cookie and csrfToken response.
2. Add X-CSRF-TOKEN to every POST/PUT/PATCH/DELETE, including public registration and login.
3. POST /api/auth/login with username/password. Preserve the cookie jar and use the NEW returned csrfToken.
4. Use separate cookie jars/browser contexts for separate identities. Do not reuse the real ADMIN for destructive tests.
5. Password change/logout invalidates a session; refresh /auth/me before future writes.

## Implemented routes
| Method | Path | Success | Controller |
|---|---|---|---|
| GET | `/api/appointments` | 200 | AppointmentController |
| GET | `/api/appointments/{id}` | 200 | AppointmentController |
| POST | `/api/appointments` | 201 | AppointmentController |
| PUT | `/api/appointments/{id}` | 200 | AppointmentController |
| DELETE | `/api/appointments/{id}` | 204 | AppointmentController |
| PATCH | `/api/appointments/{id}/status` | 200 | AppointmentController |
| GET | `/api/moving-companies` | 200 | MovingCompanyController |
| GET | `/api/moving-companies/{id}` | 200 | MovingCompanyController |
| POST | `/api/moving-companies` | 201 | MovingCompanyController |
| PUT | `/api/moving-companies/{id}` | 200 | MovingCompanyController |
| DELETE | `/api/moving-companies/{id}` | 204 | MovingCompanyController |
| GET | `/api/offers` | 200 | OfferController |
| GET | `/api/offers/{id}` | 200 | OfferController |
| POST | `/api/offers` | 201 | OfferController |
| PUT | `/api/offers/{id}` | 200 | OfferController |
| DELETE | `/api/offers/{id}` | 204 | OfferController |
| PATCH | `/api/offers/{id}/status` | 200 | OfferController |
| GET | `/api/properties` | 200 | PropertyController |
| GET | `/api/properties/{id}` | 200 | PropertyController |
| POST | `/api/properties` | 201 | PropertyController |
| PUT | `/api/properties/{id}` | 200 | PropertyController |
| DELETE | `/api/properties/{id}` | 204 | PropertyController |
| GET | `/api/reviews` | 200 | ReviewController |
| GET | `/api/reviews/{id}` | 200 | ReviewController |
| POST | `/api/reviews` | 201 | ReviewController |
| PUT | `/api/reviews/{id}` | 200 | ReviewController |
| DELETE | `/api/reviews/{id}` | 204 | ReviewController |
| GET | `/api/auth/me` | 200 | AuthController |
| POST | `/api/auth/register` | 201 | AuthController |
| POST | `/api/auth/login` | 200 | AuthController |
| POST | `/api/auth/logout` | 204 | AuthController |
| GET | `/api/users` | 200 | UserController |
| GET | `/api/users/{id}` | 200 | UserController |
| POST | `/api/users` | 201 | UserController |
| PUT | `/api/users/{id}` | 200 | UserController |
| PUT | `/api/users/{id}/role` | 200 | UserController |
| PUT | `/api/users/{id}/password` | 204 | UserController |
| DELETE | `/api/users/{id}` | 204 | UserController |

## Request bodies
These are examples only. Replace placeholders locally. DELETE requests have no JSON body.

### Register or ADMIN create user
```json
{
  "username": "demo_buyer",
  "password": "<choose-private-password>",
  "email": "buyer@example.com",
  "phone": "0700000000",
  "role": "BUYER"
}
```

### Login
```json
{
  "username": "demo_buyer",
  "password": "<your-private-password>"
}
```

### Update user profile
```json
{
  "username": "demo_buyer",
  "email": "new@example.com",
  "phone": "0711111111"
}
```

### Change user role
```json
{
  "role": "SELLER"
}
```

### Change own password
```json
{
  "currentPassword": "<your-current-password>",
  "newPassword": "<choose-new-private-password>"
}
```

### Create or update property
```json
{
  "title": "Kandy family home",
  "description": "Three-bedroom home",
  "city": "Kandy",
  "address": "Example Road",
  "price": "25000000.00",
  "propertyType": "HOUSE",
  "status": "AVAILABLE"
}
```

### Create or update moving company
```json
{
  "companyName": "Example Movers",
  "email": "movers@example.com",
  "phone": "0700000000",
  "serviceAreas": "Colombo, Kandy",
  "description": "Residential moving",
  "availability": "AVAILABLE"
}
```

### Create appointment
```json
{
  "propertyId": "<property-uuid>",
  "viewingDate": "<future-YYYY-MM-DD>",
  "viewingTime": "10:00:00",
  "notes": "Please call on arrival"
}
```

### Update appointment
```json
{
  "viewingDate": "<future-YYYY-MM-DD>",
  "viewingTime": "11:00:00",
  "notes": "Updated time"
}
```

### Appointment status
```json
{
  "status": "CONFIRMED"
}
```

### Create offer
```json
{
  "propertyId": "<property-uuid>",
  "amount": "24000000.00",
  "message": "Offer subject to viewing"
}
```

### Update offer
```json
{
  "amount": "24500000.00",
  "message": null
}
```

### Offer status
```json
{
  "status": "ACCEPTED"
}
```

### Create review
```json
{
  "propertyId": "<property-uuid>",
  "rating": 4,
  "comment": "Helpful viewing and clear listing"
}
```

### Update review
```json
{
  "rating": 5,
  "comment": "Updated feedback"
}
```

## Response conventions
201 for create; 200 for reads/updates/state actions; 204 for user password change, logout and deletes.
Safe response DTOs omit password hashes. Moving returns its immutable record representation.
400 invalid input; 401 login required/invalid login; 403 permission or CSRF failure; 404 record missing; 409 state/reference/concurrent conflict.
Expected business failures use JSON code/message. Error details and stack traces are not intentionally exposed.
Request DTOs reject unsupported fields. Do not send client-selected ownerId, buyerId, reviewerId, id or initial status where DTO omits them.
Property types: HOUSE/APARTMENT/LAND. Availability: AVAILABLE/UNAVAILABLE. Appointment and Offer states are in MODULES-AND-PERMISSIONS.md.
Review rating is an integer; fractional or overflowing values are invalid.
Property supports q/city filtering. Other controller parameters are not a promise of search functionality unless implemented by that service.
