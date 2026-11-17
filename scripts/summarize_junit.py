#!/usr/bin/env python3
"""Summarise per-test failure rates from a directory of run dirs (each with surefire-reports/TEST-*.xml).

Usage: summarize_junit.py OUT_DIR [--parallel=true|false] [--tz=UTC|Asia/Kolkata|America/New_York]
Runs whose meta.json says setup_failed are excluded from rates (reported separately).
"""
import glob, json, os, sys, collections
import xml.etree.ElementTree as ET


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    flags = {a for a in sys.argv[1:] if a.startswith("--")}
    out = args[0] if args else "out"
    stats = collections.defaultdict(lambda: [0, 0, 0])  # name -> [runs, fails, skips]
    runs = 0
    outage = 0
    for d in sorted(glob.glob(os.path.join(out, "run-*"))):
        meta = {}
        try:
            meta = json.load(open(os.path.join(d, "meta.json")))
        except Exception:
            pass
        want_p = next((f.split("=")[1] for f in flags if f.startswith("--parallel=")), None)
        want_tz = next((f.split("=")[1] for f in flags if f.startswith("--tz=")), None)
        if want_p and str(meta.get("parallel")).lower() != want_p:
            continue
        if want_tz and meta.get("tz") != want_tz:
            continue
        if meta.get("setup_failed"):
            outage += 1
            continue
        files = glob.glob(os.path.join(d, "surefire-reports", "TEST-*.xml"))
        if not files:
            continue
        runs += 1
        for f in files:
            for tc in ET.parse(f).getroot().iter("testcase"):
                key = tc.get("classname") + "." + tc.get("name").replace("()", "")
                s = stats[key]
                if tc.find("skipped") is not None:
                    s[2] += 1
                    continue
                s[0] += 1
                if tc.find("failure") is not None or tc.find("error") is not None:
                    s[1] += 1
    print(f"runs with results: {runs}   setup-failed (infra outage) runs excluded: {outage}")
    print(f"{'test':95s} {'runs':>5s} {'fails':>5s} {'rate':>7s}")
    for k in sorted(stats):
        r, fl, sk = stats[k]
        note = f" skipped={sk}" if sk else ""
        rate = (fl / r * 100) if r else 0
        print(f"{k:95s} {r:5d} {fl:5d} {rate:6.1f}%{note}")


if __name__ == "__main__":
    main()
