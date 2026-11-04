# Viva and handoff guide

## Explain the application in one minute
Our React interface sends HTTP requests to Spring controllers. Services validate data and permissions,
then repositories/DAOs use MySQL. User accounts have BUYER, SELLER or ADMIN roles. The six modules share
session identity and CSRF helpers. We tested the combined development code with isolated H2 tests and
temporary-account browser checks using real MySQL.

## OOP in the actual code
- Encapsulation: private User/Property/Offer/Review fields and accessors control object state; MovingCompany is an immutable record.
- Abstraction: service and repository interfaces state operations without exposing implementation details.
- Polymorphism: a PropertyService reference uses PropertyServiceImpl supplied by Spring; RowMapper implementations map SQL rows.
- Inheritance: UserRepository extends JpaRepository; ModuleException extends RuntimeException.
- Composition/dependency: a controller holds a service, a service holds a repository; constructor injection supplies these objects.
- Enum: Role restricts account roles to a fixed set; this is not an independent roles CRUD table.
- Overriding: @Override marks implementation methods that satisfy an interface contract.
- DTOs: input objects allow only expected fields; response objects keep password hashes private.
- Transactions: related SQL operations and checks succeed together or roll back together on failure.
- No Java lambda functions are required by this implementation; named mapper and ordinary loops are used.

## Simple terms
JPA is the persistence specification used to map User objects to a table. Hibernate implements it.
Spring Data generates the UserRepository implementation. JDBC is the lower-level SQL approach used by other modules.
DI means a class receives objects it needs, rather than constructing them itself. Spring manages those objects as beans.
@RestController handles HTTP JSON requests. @Service identifies business logic. @Repository identifies data access.
@Entity maps User for JPA. @Id identifies its key. @Column configures columns. @Enumerated stores role text.
application.properties configures ports, database settings, schema policy and sessions; .env contains private local values.
Hashing is one-way password verification, not reversible encryption. A password is checked with encoder.matches.
CSRF tokens prevent a different site from silently submitting session-authenticated writes.

## Demonstration order using disposable demo data
1. Run MySQL, PortalApplication on 8081 and frontend on 5173. Open the complete development project.
2. Register a demo BUYER and SELLER. Use a dedicated demo ADMIN for moderation; do not change the real ADMIN.
3. SELLER creates a property, reads it and changes its price.
4. BUYER requests a future appointment, edits a pending request; SELLER confirms it. Show blocked terminal deletion.
5. BUYER submits/updates an offer; SELLER accepts/rejects; explain that terminal records preserve history.
6. BUYER reviews another owner's property, edits rating/comment, then deletes an unreferenced review.
7. Demo ADMIN creates/updates/deletes a moving company and a disposable account.
8. Show that BUYER cannot promote role, another author cannot edit a review, and references block deletion.
9. Show commit history and UML for each person's actual contribution. Remove demo records in child-to-parent order.

## Questions each member should practise
Point to your controller, service, data access, model and DTOs. Explain one create request all the way to SQL.
Where is identity taken from? Which checks run before saving? Why can the frontend alone not enforce permissions?
Why use prepared parameters? What does a foreign key stop? What is a transaction? What returns 400/401/403/404/409?
Which classes/interfaces really exist in your module? Do not claim a UserRepositoryImpl or MovingService interface that is absent.
What did you write, adapt, test and fix? Describe assistance accurately and be able to explain the resulting code.

## User/Admin member focus
Explain Role, User mapping, request/response DTOs, JpaRepository, PBKDF2 PasswordEncoder and constructor DI.
Walk through registration, login, safe profile response, profile edit, password change, role change and account delete.
Explain last-ADMIN/self-delete safeguards, reference checks and why session invalidation is delayed until commit.
Explain bootstrap-admin is controlled first-install setup, not a public promotion URL.

## Handoff and release checklist
All six modules are integrated on development in this snapshot. Keep feature history and PR attribution.
Do not delete business tables, local credentials or real ADMIN accounts as a cleanup task.
Review the outstanding revert proposal separately before changing main. Main has not been promoted by this pack.
For hosting choose provider, database and HTTPS arrangement; keep secrets out of Git, use a separate hosted ADMIN,
configure backend port and same-origin API routing, and test login/CSRF/cookies after deployment.
Sessions are in-memory; restart logs users out. Multiple backend instances need a deliberate shared-session design.
Back up persistent data and document how to restore it. Production schema migrations are not implemented by the initial scripts.
No hosted URL is claimed until a deployment has actually been completed and verified.
