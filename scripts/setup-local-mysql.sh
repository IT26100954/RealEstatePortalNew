#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MYSQL="${MYSQL_EXECUTABLE:-/c/Program Files/MySQL/MySQL Server 8.4/bin/mysql.exe}"
if [[ ! -f "$MYSQL" ]]; then
  echo 'Set MYSQL_EXECUTABLE to the path of your MySQL client executable.' >&2
  exit 1
fi
if [[ ! -f "$ROOT/database/.local-setup.sql" || ! -f "$ROOT/backend/.mysql.local.cnf" ]]; then
  echo 'Local credentials are not generated on this computer. Follow docs/shared-setup.md.' >&2
  exit 1
fi
echo 'Enter your LOCAL MySQL administrator password at the next prompt.'
echo 'It is not saved by this script. No existing database is dropped.'
"$MYSQL" --user="${MYSQL_ADMIN_USER:-root}" --password < "$ROOT/database/.local-setup.sql"
"$MYSQL" --defaults-extra-file="$(cygpath -m "$ROOT/backend/.mysql.local.cnf")" \
  --database=real_estate_portal_new \
  --execute='SELECT DATABASE() AS database_name, CURRENT_USER() AS app_account; SHOW TABLES;'
echo 'MySQL app connection verified. Business tables remain for module owners to create.'
