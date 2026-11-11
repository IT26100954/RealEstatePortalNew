# User/Admin input validation fix

Branch: fix/user-admin-validation. No merge performed.

## Rules applied in both layers

- Phone: trim surrounding whitespace; accept 7-15 ASCII digits with an optional leading +. Preserve leading zeros. Letters, internal spaces/hyphens and other characters are rejected with a clear message. This is format validation, not phone ownership verification.
- Email: trim surrounding whitespace; enforce a practical ASCII email format, total maximum 254 and local part maximum 64. Reject leading/trailing/consecutive local dots, empty domain labels, invalid domain hyphens/characters and invalid final labels. No DNS or mailbox ownership check is performed.
- Password: keep the existing 8-128 length rule and reject whitespace-only values, including common Unicode whitespace. Do not trim passwords or rewrite stored hashes. Significant spaces in nonblank passwords are preserved.

The Java services enforce these rules for public registration, ADMIN creation, profile updates, password changes and first-admin setup. Login rejects blank-only passwords with the existing generic credential error. The React forms show matching errors before submission. Existing usernames, emails, roles and account records are not migrated or rewritten.

## Verification

- Node frontend validation tests: 6 passed, using shared valid/invalid test vectors.
- Full Maven package with frontend: 30 tests passed, 0 failures/errors. Automated destructive/reset tests use the explicitly configured disposable H2 database, never the real MySQL database.
- Frontend production build passed.
- Real MySQL API/browser verification: 40 checks passed with generated temporary accounts, including a separate disposable ADMIN.
- Rejected profile/password edits left saved values and password hashes unchanged.
- Valid local/international phone numbers, normal email addresses, and passwords with significant spaces worked.
- Temporary accounts were removed. Fingerprints of all pre-existing user records (including ADMINs) and table counts matched the pre-test snapshot.

Live evidence: docs/final/test-evidence/user-validation-mysql-results.json.

## Main locations

- backend/src/main/java/com/realestate/common/InputRules.java: shared email, phone and password checks.
- backend/src/main/java/com/realestate/user/UserServiceImpl.java: account creation/update/password validation calls.
- backend/src/main/java/com/realestate/user/BootstrapAdminService.java: first-admin input validation.
- backend/src/main/java/com/realestate/user/AuthServiceImpl.java: generic blank-password login rejection.
- frontend/src/shared/validation.js and Forms.jsx: matching client-side checks.
- frontend/src/modules/user/UserPages.jsx: phone/password field guidance.
- backend/src/test/java/com/realestate/InputRulesValidationTest.java and UserAdminIntegrationTest.java: regression coverage.

Earlier viva PDF source line numbers may have shifted after this code edit. Use method names to locate the same functions. Earlier notes saying that phone letters or whitespace-only passwords are accepted describe the old version.
