# Hosted demonstration handoff - 4 November 2026

Website: https://real-estate-portal-production-3d7d.up.railway.app

All six modules run together on Railway, with a separate Aiven MySQL Free database.
The frontend and Java backend are built into the same Docker service. IntelliJ and local terminals are not needed to open this hosted website.
The GitHub repository remains private. No local database or real local ADMIN credential was uploaded.

## Verified
- Railway built and deployed the actual Dockerfile from main, including verified MySQL TLS.
- Root page and /api/auth/me return HTTP 200 over HTTPS.
- 43 browser checks passed on the public website: User/Admin, Property, Moving, Appointment, Offer and Review CRUD and permission checks.
- Temporary test data was removed and existing hosted records were verified unchanged.
- Separate hosted ADMIN login/logout, CSRF enforcement and Secure/HttpOnly/SameSite=Lax cookies passed.
- Separate intentional demo BUYER, SELLER and ADMIN accounts plus sample records are retained for the presentation.
- Original local ADMIN and local data are unchanged.

## Private login details - owner computer only
Open .local/hosted-admin.txt for the initial hosted administrator account.
Open .local/hosted-demo-access.txt for separate demonstration accounts.
These ignored files are not in GitHub, Docker or release assets. Do not publish or include them in screenshots.
Change the initial hosted administrator password after first login using My profile.
Local and hosted accounts are separate: use the hosted access file with the website above.

## Cost and availability
Approved spending limit: $0. No payment card or paid plan was added by the deployment workflow.
Render deployment was stopped at card verification. Aiven MySQL uses Free, not the unrelated PostgreSQL trial.
Railway provides $5 trial credit for up to 30 days, then $1 free credit per month. This does not guarantee unlimited or always-on availability.
The website may stop when credits are exhausted. Do not upgrade to Hobby/Pro or attach a payment method without a new budget decision.
Serverless idle sleeping is enabled, with minimum idle database connections set to 0 and idle timeout 30 seconds to conserve resources.
A sleeping/restarting service logs users out because sessions are in memory. Open the website before the evaluation and log in again.

## Demo order
1. Log in as the demo SELLER to show the two example property listings.
2. Log in as the demo BUYER to show a viewing request, offer and review.
3. Log in as the demo ADMIN to show user management and the moving directory.
4. Use throwaway demo records for delete demonstrations; referenced records may correctly reject deletion.
5. Keep the complete class diagram PDF and GitHub commit history open for the viva.

## References and source
The Java class-diagram snapshot is unchanged; hosting fixes added no Java business classes.
The source release is on main. v1.0.0 assets remain at https://github.com/IT26100954/RealEstatePortalNew/releases/tag/v1.0.0 .
The later hosting certificate patch supports Railway variables without changing the packaged Java business code.
Pricing: https://docs.railway.com/pricing/free-trial and https://aiven.io/docs/products/mysql/concepts/mysql-free-tier .
Prepared with Codex assistance; each member should understand and explain their individual Java contribution.
