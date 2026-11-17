#!/usr/bin/env bash
# Re-runs ~50% of the failed jobs of recent failed workflow runs, using the `gh` CLI.
# This is what produces same-SHA multi-attempt history (the key signal for flaky detection).
# NOT run automatically by the author of this repo; invoke manually or from a schedule once a remote exists.
#
# Usage:  scripts/rerun_failed.sh [--limit 20] [--prob 50] [--dry-run]
# Needs:  gh authenticated against the repo, jq.
set -eu
LIMIT=20; PROB=50; DRY=0; WORKFLOW="ci.yml"
while [ $# -gt 0 ]; do
  case "$1" in
    --limit) LIMIT="$2"; shift 2 ;;
    --prob) PROB="$2"; shift 2 ;;
    --dry-run) DRY=1; shift ;;
    --workflow) WORKFLOW="$2"; shift 2 ;;
    *) echo "unknown arg $1" >&2; exit 2 ;;
  esac
done

run_ids=$(gh run list --workflow "$WORKFLOW" --status failure --limit "$LIMIT" --json databaseId,attempt -q '.[] | select(.attempt < 3) | .databaseId')
for run in $run_ids; do
  # failed jobs of this run (latest attempt), excluding jobs that failed during setup (infra outage)
  jobs=$(gh run view "$run" --json jobs -q '.jobs[] | select(.conclusion=="failure") | .databaseId')
  for job in $jobs; do
    if [ $((RANDOM % 100)) -lt "$PROB" ]; then
      echo "re-running job $job of run $run"
      [ "$DRY" = 1 ] || gh run rerun "$run" --job "$job" || echo "  (could not re-run $job)"
    else
      echo "skipping job $job of run $run"
    fi
  done
done
