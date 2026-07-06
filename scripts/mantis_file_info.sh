#!/bin/bash
set -euo pipefail
CREDS="/opt/mantis/CREDENTIALS.local.txt"
PW=$(grep '^MariaDB_mantisbt=' "$CREDS" | cut -d= -f2-)
FILE_ID="${1:?file id}"
OUT="/tmp/mantis_file_${FILE_ID}.txt"
docker exec mantisbt-db mariadb -umantisbt -p"${PW}" bugtracker -N -B -e \
  "SELECT file_type, diskfile, filename, filesize FROM mantis_bug_file_table WHERE id=${FILE_ID};"
docker exec mantisbt-db mariadb -umantisbt -p"${PW}" bugtracker -N -B -e \
  "SELECT HEX(LEFT(content, 32)) FROM mantis_bug_file_table WHERE id=${FILE_ID};" 2>/dev/null || echo "no content column"
