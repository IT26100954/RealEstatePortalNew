> Historical setup snapshot. For the completed six-module project, use [the final setup guide](final/SETUP-AND-RUN.md) and [current progress](progress.md).

\# Team Setup Guide



\## Requirements



\- Git and Git Bash

\- IntelliJ IDEA

\- JDK 21

\- Internet access for downloading dependencies



MySQL, Node.js and npm will be required when the database

and frontend foundations are added.



\## Clone the repository



Run in Git Bash:



git clone https://github.com/IT26100954/RealEstatePortalNew.git



cd RealEstatePortalNew



git switch development



git pull --ff-only origin development



\## Open in IntelliJ



1\. Choose File > Open.

2\. Select the cloned RealEstatePortalNew folder.

3\. Set the Project SDK to JDK 21.

4\. Open backend/pom.xml.

5\. Add it as a Maven project if not already imported.

6\. Wait for dependency loading to finish.

7\. Use JDK 21 for the application and test run configurations.



\## Verify the backend



Open:



backend/src/main/java/com/realestate/PortalApplication.java



Run its main method.



Expected:

\- Java 21 is used.

\- The server starts on port 8081.

\- The console reports Started PortalApplication.



The root address may return 404 because no API endpoint

has been implemented there yet.



\## Run the startup test



Open:



backend/src/test/java/com/realestate/PortalApplicationTests.java



Run contextLoads.



This checks application context loading.

It does not test CRUD or MySQL.



\## Before module coding



Read docs/team-agreement.md.



Create your assigned feature branch from updated development.

Do not copy another member's implementation.

Coordinate database relationships and shared interfaces first.



\## Current limitations



The project currently contains the backend foundation only.

Database integration and the React frontend are not ready yet.





\## Local Maven workaround on the leader's computer



The Maven Wrapper was generated, but its initial download

failed because of a network/certificate-revocation issue.



IntelliJ's bundled Maven works. From the backend folder,

the following Git Bash commands were verified:



```bash

export JAVA\_HOME="C:/Users/lapto/.jdks/ms-21.0.12.1"



"/c/Program Files/JetBrains/IntelliJ IDEA 2026.1/plugins/maven/lib/maven3/bin/mvn.cmd" test

```



Result: BUILD SUCCESS.



These installation paths are specific to this computer.

Other members can use IntelliJ's bundled Maven through

the Maven tool window.

