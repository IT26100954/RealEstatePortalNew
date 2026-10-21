\# Six Member Team Agreement



\## Shared technology



\- Java 21

\- Spring Boot

\- Maven

\- MySQL

\- React and Vite

\- Git and GitHub

\- Git Bash for terminal commands



\## Module ownership



| Member | Module | Java package | Feature branch |

|---|---|---|---|

| 1 | User and Access | com.realestate.user | feature/user-access |

| 2 | Property Listings | com.realestate.property | feature/property-listing |

| 3 | Moving Services | com.realestate.moving | feature/moving-services |

| 4 | Viewing Appointments | com.realestate.appointment | feature/viewing-appointments |

| 5 | Offers | com.realestate.offer | feature/offers |

| 6 | Reviews and Feedback | com.realestate.review | feature/reviews |



\## Responsibilities



Each member writes their module's models, database access,

business rules, API endpoints, tests and frontend screens.



Each module must demonstrate permitted Create, Read,

Update and Delete operations and three identifiable screens.



Cancellation, withdrawal and deactivation are updates,

not substitutes for a required Delete operation.



User Management owns shared account authentication.

Each member enforces permissions for their own module.



There is no separate Agent or Admin Management module.

Administrative access is handled through account roles.



Moving Services manages company details independently

of property, appointment, offer and review records.



\## Shared project rules



We use one backend and one frontend application.



Members coordinate before changing shared configuration,

authentication, frontend navigation or database conventions.



Database fields, IDs and API contracts must be agreed

before implementing connected tables and endpoints.



Real passwords must not be committed.



\## Git workflow



Members start feature branches from updated development.

Each person commits using their own Git identity.

Commit messages describe the work actually completed.

Pull requests target development.

Tested releases are merged into main.



\## Current progress



Completed:

\- Backend Maven configuration.

\- Spring Boot startup class.

\- Backend port 8081.

\- Application context startup test.



Not implemented yet:

\- Database connection and tables.

\- Account authentication.

\- Business modules.

\- React frontend foundation.



Previous prepared projects remain separate references.

