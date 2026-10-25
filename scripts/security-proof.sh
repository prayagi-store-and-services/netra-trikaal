#!/usr/bin/env bash
set -euo pipefail
mkdir -p proof-evidence
cleanup() {
 local original=$?;trap - EXIT
 timeout 20s adb logcat -d > proof-evidence/logcat.txt || true
 timeout 20s adb shell dumpsys activity activities > proof-evidence/activities.txt || true
 timeout 20s adb shell dumpsys activity lastanr > proof-evidence/lastanr.txt || true
 timeout 20s adb shell screencap -p /data/local/tmp/centralized-timeout.png || true
 timeout 20s adb pull /data/local/tmp/centralized-timeout.png proof-evidence/ || true
 timeout 20s adb pull /data/local/tmp/centralized-no-updater.png proof-evidence/ || true
 cp -r app/build/outputs/androidTest-results proof-evidence/ 2>/dev/null || true
 cp -r app/build/reports/androidTests proof-evidence/ 2>/dev/null || true
 printf 'original_exit=%s\n' "$original" > proof-evidence/outcome.txt
 exit "$original"
}
trap cleanup EXIT
set -o pipefail
timeout --signal=TERM --kill-after=20s 8m gradle --no-daemon :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.timeout_msec=60000 -Pandroid.testInstrumentationRunnerArguments.class=com.prayagi.netratrikaal.update.SelfUpdateProductionFixtureTest,com.prayagi.netratrikaal.update.SelfUpdateChoiceDeviceTest --stacktrace 2>&1 | tee proof-evidence/instrumentation.log
