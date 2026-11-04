# Module responsibilities and permissions

ADMIN is a role in User Management. It is not a seventh module or a separate Admin entity.

## Member 1: User and Access Management
Package: com.realestate.user. Table: users.
- Create: ADMIN creates accounts; public register only BUYER/SELLER.
- Read: ADMIN lists all; other users access own account.
- Update: Owner/ADMIN edit profile. Password is owner-only. Role changes are ADMIN-only.
- Delete: ADMIN only; self-delete and last ADMIN blocked; referenced users cannot be deleted.
- Screens: Register/Login; My profile; Account list; Create account; Account detail/edit.
- Production Java types in this snapshot: 23.

## Member 2: Property Listing Management
Package: com.realestate.property. Table: properties.
- Create: SELLER creates with ownerId derived from login.
- Read: AVAILABLE public; otherwise only owner/ADMIN. Text and city search supported.
- Update: Owning SELLER or ADMIN.
- Delete: Owning SELLER or ADMIN; foreign-key references block deletion.
- Screens: Listing/search; Add/Edit property; Property detail.
- Production Java types in this snapshot: 9.

## Member 3: Moving Service Directory Management
Package: com.realestate.moving. Table: moving_companies.
- Create: ADMIN only.
- Read: Public, including unavailable companies.
- Update: ADMIN only.
- Delete: ADMIN only.
- Screens: Moving directory; Add/Edit company; Company detail.
- Production Java types in this snapshot: 5.

## Member 4: Viewing Appointment Management
Package: com.realestate.appointment. Table: appointments.
- Create: BUYER, available property, future Sri Lanka viewing time; server sets PENDING.
- Read: Buyer, relevant property owner SELLER, or ADMIN.
- Update: Buyer edits own PENDING record; state actions follow the transition rules below.
- Delete: Buyer owner or ADMIN, only while PENDING.
- Screens: Appointment list; Request/Edit viewing; Appointment detail/status controls.
- Production Java types in this snapshot: 10.

## Member 5: Offer Management
Package: com.realestate.offer. Table: offers.
- Create: BUYER on an AVAILABLE property; server sets PENDING.
- Read: Buyer, relevant property owner SELLER, or ADMIN.
- Update: Buyer edits own PENDING offer; seller accepts/rejects; buyer withdraws.
- Delete: Buyer owner or ADMIN, only PENDING.
- Screens: Offer list; Submit/Edit offer; Offer detail/status controls.
- Production Java types in this snapshot: 10.

## Member 6: Review and Feedback Management
Package: com.realestate.review. Table: reviews.
- Create: BUYER/SELLER may review another owner property; ADMIN cannot create.
- Read: Public list and detail.
- Update: Author only; ADMIN cannot rewrite another user comment.
- Delete: Author or ADMIN.
- Screens: Review list; Write/Edit review; Review detail.
- Production Java types in this snapshot: 9.

## Appointment transitions
PENDING -> CONFIRMED or REJECTED: owning SELLER. PENDING -> CANCELLED: buyer or ADMIN.
CONFIRMED -> CANCELLED: buyer, owning SELLER or ADMIN. CONFIRMED -> COMPLETED: owning SELLER after viewing time.
No reopening of terminal records; same-status requests are idempotent for an authorized relevant actor.
Confirmation requires a still-available property and a future time. One confirmed record per exact property/date/time slot.
Pending duplicates can exist. Service locks property then appointment and performs a locking slot read under MySQL.
Only pending records can be deleted. Confirmed and terminal records preserve history.

## Offer transitions
PENDING -> ACCEPTED or REJECTED: owning SELLER. PENDING -> WITHDRAWN: buyer.
ADMIN may read and delete pending records but cannot decide on the seller behalf.
Accepted/rejected/withdrawn records cannot be edited, deleted or reopened.
Accepting an offer does not automatically change property status; that is a separate listing decision.

## Reviews and ratings
Rating is stored as Integer and must be 1-5. Request DTO setters accept a decimal JSON number only when conversion is exact.
The setter uses BigDecimal.intValueExact so 1.5 is rejected instead of being rounded. Service checks null and range.
Comment is required and trimmed, max 10000. Request DTOs reject unsupported fields such as reviewerId.
Multiple reviews by one user on one property are currently allowed; there is no unique reviewer/property constraint.
