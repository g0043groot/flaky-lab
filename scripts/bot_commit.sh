#!/usr/bin/env bash
# Makes small, benign "daily" commits (touches docs/bot-log.md only; never changes code or tests).
# Each commit triggers CI (on: push), which builds up run history on unchanged logic.
#
# Usage:
#   scripts/bot_commit.sh                 # one commit dated now
#   scripts/bot_commit.sh --backfill N    # N commits dated N-1 .. 0 days ago, one per day (local history only)
#   scripts/bot_commit.sh --push          # also `git push` (explicitly opt-in; NOT used by default)
# Suitable for a daily cron / scheduled workflow with --push once a remote exists.
set -eu
cd "$(dirname "$0")/.."
mkdir -p docs
log=docs/bot-log.md
[ -f "$log" ] || printf '# Bot log\n\nBenign daily commits that generate CI history. No functional change.\n\n' > "$log"

commit_one() {
  local when="$1"
  printf -- '- %s: routine upkeep touch (%s)\n' "${when:0:10}" "$RANDOM" >> "$log"
  git add "$log"
  GIT_AUTHOR_DATE="$when" GIT_COMMITTER_DATE="$when" \
    git -c user.name="flaky-lab-bot" -c user.email="bot@flaky-lab.invalid" \
    commit -q -m "chore(bot): daily upkeep $(printf '%s' "$when" | cut -c1-10)"
}

if [ "${1:-}" = "--backfill" ]; then
  n="${2:?N required}"
  for ((k = n - 1; k >= 0; k--)); do
    when="$(date -u -d "-$k day" +%FT12:00:00 2>/dev/null || date -u -v-"${k}"d +%FT12:00:00)"
    commit_one "$when"
  done
else
  commit_one "$(date -u +%FT%H:%M:%S)"
  [ "${1:-}" = "--push" ] && git push
fi
echo "bot_commit: done"
