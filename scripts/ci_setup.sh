#!/usr/bin/env bash
# D05: "infra outage" simulation. Runs as the first CI step, before any test executes.
# The whole job fails here (no JUnit XML is produced) when either:
#   * SIMULATE_INFRA_OUTAGE=true  (set from the workflow_dispatch input `simulate_infra_outage`), or
#   * today's UTC date is listed in ci/outage-dates.txt  (scheduled-day switch).
set -eu
cd "$(dirname "$0")/.."
today="$(date -u +%F)"

if [ "${SIMULATE_INFRA_OUTAGE:-false}" = "true" ]; then
  echo "::error::Simulated infra outage (workflow input): artifact registry unreachable (HTTP 503)" >&2
  echo "Simulated infra outage: artifact registry unreachable (HTTP 503)" >&2
  exit 1
fi

if [ -f ci/outage-dates.txt ] && grep -qx "$today" <(grep -v '^[[:space:]]*#' ci/outage-dates.txt | tr -d ' \r'); then
  echo "::error::Simulated infra outage (scheduled day $today): runner pool exhausted" >&2
  echo "Simulated infra outage (scheduled day $today): runner pool exhausted" >&2
  exit 1
fi
echo "ci_setup: environment OK ($today)"
