#!/usr/bin/env bash
# Fast path: run the suite N times locally, varying seed / TZ / parallel / F15 env flag, and emit
# JUnit XML in the same shape as the CI artifacts, so you don't have to wait for CI history.
#
# Usage:  scripts/run_suite_n_times.sh N [OUT_DIR]
#
# Output (per run):   OUT_DIR/run-0007_seed1007-tzAsia_Kolkata-parallelon/
#                        surefire-reports/TEST-*.xml     <- same as CI artifact "junit-<leg>"
#                        meta.json                       <- seed, tz, parallel, env flag, timestamps, ...
# The 12 CI matrix legs (seed-salt x TZ x parallel) are cycled in order, so N=24 covers every leg twice.
#
# Env knobs:
#   SEED_BASE=1000     seed for run i is SEED_BASE+i
#   EXCLUDE_SLOW=1     1 = skip the 20 s D03 test (-Dtest.excludedGroups=slow); 0 = include it
#   RERUN_PROB=20      percent of runs that re-use the previous run's workspace state (F14 then fails,
#                      like a CI "re-run" in the same workspace). 0 = never.
#   OUTAGE_EVERY=0     if K>0, every K-th run simulates an infra outage (D05): fails at setup, no XML
#   TEST_FILTER=       surefire -Dtest filter, e.g. 'AsyncRaceTest' (handy for rare-flake sampling)
#   MVN=mvn            maven command
set -u
N="${1:?usage: $0 N [OUT_DIR]}"
cd "$(dirname "$0")/.."
ROOT="$PWD"
OUT="${2:-$ROOT/out}"
SEED_BASE="${SEED_BASE:-1000}"
EXCLUDE_SLOW="${EXCLUDE_SLOW:-1}"
RERUN_PROB="${RERUN_PROB:-20}"
OUTAGE_EVERY="${OUTAGE_EVERY:-0}"
TEST_FILTER="${TEST_FILTER:-}"
MVN="${MVN:-mvn}"
TZS=("UTC" "Asia/Kolkata" "America/New_York")

mkdir -p "$OUT/.state"
echo "[run_suite] compiling once..."
$MVN -B -q test-compile || { echo "compile failed"; exit 2; }

prev_state=""
for ((i = 1; i <= N; i++)); do
  seed=$((SEED_BASE + i))
  tz="${TZS[$(((i - 1) % 3))]}"
  par_idx=$(((i - 1) / 3 % 2))
  flag_idx=$(((i - 1) / 6 % 2))
  if [ "$par_idx" = 1 ]; then par=true; par_name=on; else par=false; par_name=off; fi
  if [ "$flag_idx" = 0 ]; then flag="enabled"; else flag=""; fi
  name=$(printf "run-%04d_seed%s-tz%s-parallel%s" "$i" "$seed" "${tz//\//_}" "$par_name")
  dir="$OUT/$name"
  mkdir -p "$dir"

  # --- D05: simulated infra outage (fails during setup, before any test runs) ---
  if [ "$OUTAGE_EVERY" -gt 0 ] && [ $((i % OUTAGE_EVERY)) -eq 0 ]; then
    SIMULATE_INFRA_OUTAGE=true "$ROOT/scripts/ci_setup.sh" > "$dir/setup.log" 2>&1
    rc=$?
    printf '{"run":%d,"seed":%s,"tz":"%s","parallel":%s,"feature_flag":"%s","setup_failed":true,"setup_exit_code":%d,"started_at":"%s"}\n' \
      "$i" "$seed" "$tz" "$par" "$flag" "$rc" "$(date -u +%FT%TZ)" > "$dir/meta.json"
    echo "[run_suite] $name: SIMULATED INFRA OUTAGE (setup exit $rc), no XML"
    continue
  fi

  # --- F14: reuse previous workspace state for a fraction of runs ("re-run in same workspace") ---
  state="$OUT/.state/$name"
  rerun=false
  if [ -n "$prev_state" ] && [ "$RERUN_PROB" -gt 0 ] && [ $((RANDOM % 100)) -lt "$RERUN_PROB" ]; then
    state="$prev_state"; rerun=true
  fi
  mkdir -p "$state"

  excl="none-excluded"; [ "$EXCLUDE_SLOW" = 1 ] && excl="slow"
  args=(-B -q surefire:test -Dmaven.test.failure.ignore=true
        "-Dtest.seed=$seed" "-Dtest.tz=$tz" "-Dtest.parallel=$par"
        "-Dtest.excludedGroups=$excl" "-Dflaky.state.dir=$state")
  [ -n "$TEST_FILTER" ] && args+=("-Dtest=$TEST_FILTER" "-Dsurefire.failIfNoSpecifiedTests=false")

  rm -rf "$ROOT/target/surefire-reports"
  start=$(date -u +%FT%TZ)
  if [ -n "$flag" ]; then
    env TZ="$tz" FLAKY_LAB_EXPRESS_SHIPPING="$flag" $MVN "${args[@]}" > "$dir/mvn.log" 2>&1
  else
    env -u FLAKY_LAB_EXPRESS_SHIPPING TZ="$tz" $MVN "${args[@]}" > "$dir/mvn.log" 2>&1
  fi
  rc=$?
  mkdir -p "$dir/surefire-reports"
  cp "$ROOT"/target/surefire-reports/TEST-*.xml "$dir/surefire-reports/" 2>/dev/null
  printf '{"run":%d,"seed":%s,"tz":"%s","parallel":%s,"feature_flag":"%s","rerun_same_workspace":%s,"setup_failed":false,"mvn_exit_code":%d,"started_at":"%s","finished_at":"%s"}\n' \
    "$i" "$seed" "$tz" "$par" "$flag" "$rerun" "$rc" "$start" "$(date -u +%FT%TZ)" > "$dir/meta.json"
  prev_state="$state"
  echo "[run_suite] $name done (xml: $(ls "$dir/surefire-reports" | wc -l | tr -d ' ') files, rerun_ws=$rerun)"
done
echo "[run_suite] finished. Summarise with: python3 scripts/summarize_junit.py $OUT"
