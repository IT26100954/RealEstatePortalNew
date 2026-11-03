-- Read-only inspection: run this first in Workbench.
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'real_estate_portal_new'
ORDER BY table_name;

SELECT table_name, column_name, column_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'real_estate_portal_new'
ORDER BY table_name, ordinal_position;
