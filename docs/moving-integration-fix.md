# Moving services integration correction

This follow-up adapts the existing moving module to shared CurrentUser,
CurrentAccount and InputRules. It preserves the member's record/DAO/service design
and JSON endpoints. Constructor injection remains explicit; no lambdas or streams.

- Public GET includes all directory entries; ADMIN is required for writes.
- MovingCompanyRequest rejects server-controlled or unknown input fields.
- DAO uses safe parameterized SQL, named RowMapper, locking for mutations, safe
  404 errors and affected-row checks. Shared advice handles invalid JSON/conflicts.
- The SQL script is aligned with the leader's canonical schema and selects the
  correct database. It has NOT been executed against existing tables.
- The unused competing MovingCompanyPages component was removed. The leader's
  generated shared frontend will provide screens; no shared frontend is replaced.
- PortalApplication remains present. This correction was prepared with assistant
  help in a separate integration commit, not as the member's original authorship.

The moving member should pull the fix, read these changes and understand them
before evaluation. The PR remains unmerged until reviewed by the leader.
