# Postman usage
Import the collection and blank environment. Fill private values locally; do not commit them.
Keep the cookie jar enabled. GET /api/auth/me stores csrfToken; then POST login stores the new token.
Before switching users use logout or a fresh cookie jar, then GET /auth/me again.
Select individual requests; do not run the whole collection against real data.
Create disposable accounts, property and child records; copy returned IDs to the environment.
Set futureDate to a real future YYYY-MM-DD date in Sri Lanka. Read permissions before each write.
Remove child records before deleting their property or user. Terminal Offer/Appointment records cannot be deleted by ordinary endpoints.
The collection contains no saved credentials and does not modify your environment until you run requests.
