#!/bin/bash
set -euo pipefail
CREDS="/opt/mantis/CREDENTIALS.local.txt"
TOKEN=$(grep '^API_token_pas-api=' "$CREDS" | cut -d= -f2-)
ISSUE_ID="${1:-latest}"
FILE_ID="${2:-}"

if [ "${3:-}" = "probe" ]; then
  for u in "issues/files/${FILE_ID}" "issues/files/${FILE_ID}/download" "issues/files/${FILE_ID}/content" "issues/${ISSUE_ID}/files/${FILE_ID}"; do
    echo "=== $u ==="
    curl -sS -o /dev/null -w "%{http_code} %{size_download}\n" -H "Authorization: ${TOKEN}" \
      "https://t.easy-order-taxi.site/mantis/api/rest/${u}"
  done
  exit 0
fi

if [ -n "$FILE_ID" ]; then
  curl -sS -H "Authorization: ${TOKEN}" \
    "https://t.easy-order-taxi.site/mantis/api/rest/issues/${ISSUE_ID}/files/${FILE_ID}"
  exit 0
fi

if [ "$ISSUE_ID" = "latest" ]; then
  curl -sS -H "Authorization: ${TOKEN}" \
    'https://t.easy-order-taxi.site/mantis/api/rest/issues?page_size=5&page=1'
else
  curl -sS -H "Authorization: ${TOKEN}" \
    "https://t.easy-order-taxi.site/mantis/api/rest/issues/${ISSUE_ID}"
fi
