# flaky-lab

Tier-1 planted test repo for a flaky-test-detection platform: Java 21 / Spring Boot 3 / JUnit 5 / Maven order-service
with 15 planted flaky tests (F01-F15), 5 decoys (D01-D05) and ~44 stable tests. Answer key: `ground-truth.yaml`.

Run once: `mvn test -Dtest.seed=42 -Dtest.tz=Asia/Kolkata -Dtest.parallel=true -Dtest.excludedGroups=slow`
(knobs in `pom.xml`: `test.seed`, `test.runOrder`, `test.methodOrderer`, `test.classOrderer`, `test.parallel`, `test.tz`, `test.excludedGroups`, `flaky.state.dir`; F15 needs env `FLAKY_LAB_EXPRESS_SHIPPING=enabled`).
Local history: `scripts/run_suite_n_times.sh 24 out/` then `python3 scripts/summarize_junit.py out/`.
CI: `.github/workflows/ci.yml`; helpers: `scripts/bot_commit.sh`, `scripts/rerun_failed.sh`, `scripts/ci_setup.sh` (D05).
`mvn test` is expected to be red (D01 always fails).
