# Evaluation demonstration

Three dedicated local demo accounts (BUYER, SELLER, ADMIN) and sample records have been created.
Your original account and previous records are unchanged. Demo accounts are separate from the real ADMIN.

Private local access details are in `.local/demo-access.txt` and `.local/demo-access.json`.
These files are ignored by Git and Docker. Do not upload them or share the ADMIN password publicly.
They are not included in the release artifact or documentation ZIP.

## Start
Run the normal backend on 8081 and frontend on 5173, or the bundled release JAR with the local MySQL settings.
Open http://127.0.0.1:5173 for development. Hosted deployment uses a separate HTTPS URL and separate credentials.
Use the private access file to log in to a demo account, not your real ADMIN.

## Records and order
- SELLER: two demonstration properties. Edit the City Apartment to show update/delete.
- BUYER: a future viewing request, a pending offer and a review on the Lakeside Family Home.
- ADMIN: one moving company plus account-management access.
- SELLER can confirm the viewing and accept/reject the offer; explain terminal-state history protections.
- BUYER can edit/delete pending records and edit/delete their own review.
- ADMIN can create/edit/delete a disposable moving company and account; never delete the real ADMIN.

The appointment date is seven days after the dataset was prepared. If the date passes, create a new future request
or edit the pending request before the demonstration. Do not rerun initial schema scripts to refresh demo data.
Demonstration changes persist in MySQL. Terminal appointment/offer history cannot be deleted through ordinary endpoints.
Use separate browser profiles or log out between roles. Stop the demo servers when finished.
