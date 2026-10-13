# Kern Launcher: Automated Release Runbook (for AI CLI Agents)

**Target:** Android launcher, package `com.kern.launcher`, Kotlin, Jetpack Compose, Room, DataStore, Gradle Kotlin DSL.
**Purpose:** An ordered, machine-executable procedure that a terminal-based AI agent can run against the repository to bring the project to a release candidate and to produce an evidence report.

Place this file in the repository root (for example as `RELEASE_RUNBOOK.md`) and start the agent with:

```
Read RELEASE_RUNBOOK.md and execute it from Phase 0 in order. Follow the Agent Rules exactly.
Write all results to release-report.md. Stop and ask me only at items marked [HUMAN] or when blocked.
```

---

## Agent Rules (mandatory)

1. Work on a new branch `release/prep-<YYYYMMDD>`. Never commit to `main`/`master` directly.
2. Make one commit per phase, with the message `release-prep: phase N <title>`.
3. Never read, create, print, or commit a keystore, password, token, or `local.properties` value. Signing material is supplied by the human through environment variables only.
4. Never run destructive commands: `git push --force`, `git reset --hard`, `rm -rf` outside `build/` and `.gradle/`, or any change to `applicationId`, `namespace`, or package structure without explicit approval.
5. Never use `fallbackToDestructiveMigration()` as a fix for any failure.
6. Before editing a file, read it. After editing, build to verify the change compiles.
7. If a command fails, record the full error in `release-report.md`, attempt at most **two** targeted fixes, then mark the step `BLOCKED` and continue with independent steps.
8. Do not weaken a test or a lint rule to make it pass. Fix the cause, or record the item as `FINDING` for human review.
9. Item tags: **[AUTO]** execute fully; **[AUTO+REVIEW]** execute, then flag the diff for human review; **[HUMAN]** do not execute; list it in the final report.
10. Use this status vocabulary in the report: `PASS`, `FAIL`, `FIXED`, `FINDING`, `BLOCKED`, `SKIPPED (reason)`.

---

## Phase 0: Preflight [AUTO]

```bash
git status --porcelain            # must be empty, else stop and report
git checkout -b release/prep-$(date +%Y%m%d)
java -version
./gradlew --version
echo "ANDROID_HOME=$ANDROID_HOME"
adb devices                       # record whether a device or emulator is attached
```

- [ ] Working tree clean; branch created.
- [ ] JDK and Gradle versions recorded in the report.
- [ ] Record whether `adb` is available. If no device is attached, mark Phase 9 `SKIPPED (no device)`.
- [ ] Inventory the project and record in the report: `applicationId`, `namespace`, `minSdk`, `targetSdk`, `compileSdk`, `versionCode`, `versionName`, AGP version, Kotlin version, Compose BOM version.

```bash
grep -nE "applicationId|namespace|minSdk|targetSdk|compileSdk|versionCode|versionName" app/build.gradle.kts
```

## Phase 1: Baseline Health [AUTO]

```bash
./gradlew clean assembleDebug testDebugUnitTest lintDebug --stacktrace
```

**Pass if:** the command exits with code 0.
**On failure:** record the first failing task and error. Fix only genuine breakage (compile errors, broken tests). Do not suppress lint.

- [ ] Debug build succeeds.
- [ ] Existing unit tests pass (record the count; record `0 tests` as a `FINDING`).
- [ ] Lint report saved: copy `app/build/reports/lint-results-debug.html` path into the report; summarise error and warning counts.

## Phase 2: Manifest and Launcher Role Audit [AUTO+REVIEW]

```bash
./gradlew :app:processReleaseManifest
MANIFEST=$(find app/build -path "*merged_manifests/release*" -name AndroidManifest.xml | head -1)
echo "$MANIFEST"
grep -nE "exported|permission|category|allowBackup|dataExtractionRules|fullBackupContent|usesCleartextTraffic|debuggable|queries|launchMode" "$MANIFEST"
```

Verify each item and record the result:

- [ ] `MainActivity` has `android.intent.action.MAIN` with categories `android.intent.category.HOME` and `android.intent.category.DEFAULT`, and also `LAUNCHER` if intended.
- [ ] `launchMode` is `singleTask` or `singleInstance` (flag `FINDING` if `standard`).
- [ ] Every `<activity>`, `<service>`, `<receiver>`, `<provider>` declares `android:exported` explicitly.
- [ ] No component other than `MainActivity` is exported without justification (list each exported component in the report).
- [ ] Permission list recorded. For each permission, grep for its use in `app/src/main/java`; flag any unused permission as `FINDING`.
- [ ] If `QUERY_ALL_PACKAGES` is present: flag `FINDING` "Play permission declaration required" (this is a **[HUMAN]** form). Also assess whether a `<queries>` block for `MAIN`/`LAUNCHER` intents could replace it, and describe the change without applying it.
- [ ] `android:allowBackup` is explicitly set. If absent or `true`, flag as `FINDING` and propose `dataExtractionRules` that exclude the command-history database and DataStore files.
- [ ] `usesCleartextTraffic` is not `true`; if `DownloadDialog` makes network calls, a network security config exists.
- [ ] Release manifest has no `android:debuggable="true"`.
- [ ] Adaptive icon (`ic_launcher` with `<monochrome>`) exists in `res/mipmap-anydpi-v26/`; flag if absent.

## Phase 3: Build Configuration [AUTO+REVIEW]

Edit `app/build.gradle.kts` so the following hold:

- [ ] `release` build type: `isMinifyEnabled = true`, `isShrinkResources = true`, `proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")`.
- [ ] `release` build type: `isDebuggable = false`.
- [ ] Signing configuration reads from environment variables and **must not fail the build when they are absent** (so CI and the agent can still build unsigned artifacts):

```kotlin
signingConfigs {
    create("release") {
        val path = System.getenv("KERN_KEYSTORE_PATH")
        if (path != null) {
            storeFile = file(path)
            storePassword = System.getenv("KERN_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KERN_KEY_ALIAS")
            keyPassword = System.getenv("KERN_KEY_PASSWORD")
        }
    }
}
buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release").takeIf { it.storeFile != null }
    }
}
```

- [ ] Create `app/proguard-rules.pro` if missing. Add keep rules only as required by evidence from Phase 8 (Room and Kotlin serialization rules are normally supplied by their libraries).
- [ ] Ensure `.gitignore` contains: `*.jks`, `*.keystore`, `local.properties`, `keystore.properties`, `/app/release/`.
- [ ] Scan the repository for committed secrets:

```bash
git ls-files | grep -E "\.(jks|keystore|p12|pem)$"
git grep -nEi "(password|secret|api[_-]?key|token)\s*[=:]\s*['\"][^'\"]+['\"]" -- . ':!*.md'
```

  Any match is a `FINDING` (do not print the secret value in the report; print only file and line).

- [ ] Record `targetSdk` and `versionCode`. Do **not** change `targetSdk` automatically; propose a change as a `FINDING` with a comparison against the current Google Play requirement (the agent must look this up in the official Android documentation and cite the URL).

Verification:

```bash
./gradlew assembleRelease --stacktrace
```

## Phase 4: Room and DataStore Safety [AUTO+REVIEW]

Relevant files: `data/room/AppDatabase.kt`, `data/room/dao/*`, `data/room/entity/*`, `data/datastore/SettingsDataStore.kt`.

```bash
grep -nE "version\s*=|exportSchema|fallbackToDestructiveMigration|addMigrations|autoMigrations" app/src/main/java/com/kern/launcher/data/room/AppDatabase.kt
grep -rn "fallbackToDestructiveMigration" app/src
```

- [ ] `@Database(exportSchema = true)`.
- [ ] Schema location configured (use whichever matches the project; detect KSP, KAPT, or the Room Gradle plugin):
  - KSP: `ksp { arg("room.schemaLocation", "$projectDir/schemas") }`
  - Room Gradle plugin: `room { schemaDirectory("$projectDir/schemas") }`
- [ ] Run a build so that `app/schemas/com.kern.launcher.data.room.AppDatabase/<version>.json` is generated, and commit it.
- [ ] `fallbackToDestructiveMigration` is absent from release code. If present, remove it and record it as `FIXED`; if the database version is greater than 1, record the need for explicit migrations as `FINDING`.
- [ ] Add `androidTestImplementation("androidx.room:room-testing:<same version as room-runtime>")` and `sourceSets["androidTest"].assets.srcDir("$projectDir/schemas")`.
- [ ] If the database version is greater than 1, generate a `MigrationTest` using `MigrationTestHelper` for each version pair. If the version is 1, generate a test that opens the database and verifies each DAO round trip.
- [ ] `SettingsDataStore`: confirm a `corruptionHandler` is configured, and that every preference key has a default. If not, propose a patch.
- [ ] Verify each DAO method used for writes is `suspend` (or returns `Flow`) and is never called from the main thread (`grep -rn "runBlocking" app/src/main`; every match is a `FINDING`).

## Phase 5: Static Security and Privacy Scan [AUTO]

Run each command and record the matches in the report (file, line, and a one-line assessment).

```bash
# Logging that could expose user input in release
grep -rnE "Log\.[dviwe]\(|println\(|Timber\." app/src/main/java

# Intent and PendingIntent safety
grep -rnE "PendingIntent\.get" app/src/main/java
grep -rnE "Intent\.parseUri|parseUri\(" app/src/main/java
grep -rnE "FLAG_IMMUTABLE|FLAG_MUTABLE" app/src/main/java

# Cleartext or unvalidated URLs
grep -rnE "http://" app/src/main
grep -rnE "Uri\.parse|ACTION_VIEW" app/src/main/java

# Blocking calls and unsafe patterns
grep -rnE "runBlocking|GlobalScope|\.printStackTrace\(\)" app/src/main/java
grep -rnE "WebView|addJavascriptInterface|setJavaScriptEnabled" app/src/main/java
```

Assessment criteria:

- [ ] Every `PendingIntent` sets `FLAG_IMMUTABLE` or has a documented reason for `FLAG_MUTABLE`.
- [ ] `Intent.parseUri` is never applied to user-entered text without scheme validation (`intent:` URIs allow arbitrary component targeting). If found, **propose a patch** that allows only `http`, `https`, `tel`, `mailto`, and `geo` schemes.
- [ ] `ACTION_VIEW` calls in `CommandExecutor` / `IntentFactory` are wrapped in `try/catch (ActivityNotFoundException)`.
- [ ] `Log` calls do not include the typed command, query, or the installed-app list. Wrap in `if (BuildConfig.DEBUG)` or strip with R8 `-assumenosideeffects` rules (record as `FIXED`).
- [ ] `GlobalScope` and `runBlocking` usages are listed as `FINDING`.
- [ ] **`ui/components/DownloadDialog.kt`:** read it fully and write a paragraph in the report covering: what is downloaded, from which URL, which protocol, whether integrity is verified, whether anything is installed or executed, where the file is stored, and which permissions it requires. Flag every unverified download as `FINDING`. Do not modify it.

## Phase 6: Generate and Run Unit Tests [AUTO+REVIEW]

Create tests under `app/src/test/java/com/kern/launcher/`. Use JUnit 4 or 5 (match the existing dependency), `kotlinx-coroutines-test`, and fakes rather than mocking frameworks when possible. Where a class is not unit-testable because it depends on Android types, record that as a `FINDING` and test the extractable logic only.

| Subject | Required cases |
|---|---|
| `InputTokenizer` | empty string; whitespace only; single token; multiple spaces; quoted strings; unmatched quote; very long input (10 000 characters); unicode and emoji; control characters |
| `CommandParser` | every built-in command with valid arguments; each with missing arguments; unknown command; command with trailing whitespace; case variations |
| `CalculatorEvaluator` | basic operators and precedence; parentheses; nested parentheses (depth 100); division by zero; overflow; `0.1 + 0.2` formatting; negative numbers; unary minus; empty expression; invalid characters; extremely long expression; decimal separator handling |
| `SearchRanker` | determinism for identical input; exact match outranks prefix match outranks substring; usage count influences order; empty query; tie-breaking is stable |
| `AliasRepository` (or its resolution logic) | create; duplicate name; delete; alias cycle (A to B to A) is rejected or bounded; alias that shadows a built-in command |
| `UserSettings` defaults | default values are as documented in `HelpView` / README |

```bash
./gradlew testDebugUnitTest --stacktrace
```

**Pass if:** all tests pass and a coverage-style summary (test counts per class) is recorded. Any test that exposes a genuine defect must **not** be deleted: record the defect as `FINDING` with the failing test name, mark the test `@Ignore("known defect: <ref>")`, and continue.

## Phase 7: Lint and Style [AUTO+REVIEW]

```bash
./gradlew lintRelease --stacktrace
```

- [ ] Zero lint **errors** in `lintRelease`; record the warning count by category.
- [ ] Fix errors involving: `MissingPermission`, `ExportedReceiver`, `UnsafeIntentLaunch`, `HardcodedText`, `ContentDescription`, `RtlHardcoded`, `Overdraw`, `ObsoleteSdkInt`. Record each fix.
- [ ] Add `ktlint` or `detekt` as a Gradle plugin if neither is present. Run it in report-only mode and record the counts; do not mass-reformat the codebase in this phase.
- [ ] Verify there are no `TODO`, `FIXME`, or `HACK` markers in release paths:

```bash
grep -rnE "TODO|FIXME|HACK|XXX" app/src/main
```

  Record every match as a `FINDING`.

## Phase 8: Release Build and R8 Verification [AUTO]

```bash
./gradlew clean assembleRelease bundleRelease --stacktrace
APK=$(find app/build/outputs/apk/release -name "*.apk" | head -1)
AAB=$(find app/build/outputs/bundle/release -name "*.aab" | head -1)
MAPPING=app/build/outputs/mapping/release/mapping.txt
ls -lh "$APK" "$AAB" "$MAPPING"
```

- [ ] Both artifacts built; record file sizes.
- [ ] `mapping.txt` exists (proves R8 ran). Copy it to `release-artifacts/mapping-<versionName>-<versionCode>.txt`.
- [ ] Inspect the badging (locate `aapt2` under `$ANDROID_HOME/build-tools/*/`):

```bash
AAPT2=$(ls -d $ANDROID_HOME/build-tools/*/aapt2 | sort -V | tail -1)
"$AAPT2" dump badging "$APK" | grep -E "package:|sdkVersion|targetSdkVersion|application-debuggable|launchable-activity|uses-permission"
```

  **Pass if:** `application-debuggable` is absent, package name and versions are as expected, and the launchable activity is `MainActivity`.

- [ ] Signature check (only if signing variables were supplied by the human; otherwise `SKIPPED (unsigned)`):

```bash
APKSIGNER=$(ls -d $ANDROID_HOME/build-tools/*/apksigner | sort -V | tail -1)
"$APKSIGNER" verify --print-certs "$APK"
```

- [ ] Compute SHA-256 checksums into `release-artifacts/SHA256SUMS`.
- [ ] Search the mapping and the decompiled class list for removed classes that the code reaches by reflection (Room-generated `*_Impl`, DataStore serializers). If the Phase 9 smoke test crashes with `ClassNotFoundException` or `NoSuchMethodException`, add the minimal keep rule, rebuild, and retest.

## Phase 9: Device Smoke Test [AUTO] (skip if no device attached)

Use the **release** APK. Replace the package name if it differs.

```bash
adb install -r "$APK"
adb logcat -c

# Cold start timing (record TotalTime and WaitTime)
adb shell am force-stop com.kern.launcher
adb shell am start -W -n com.kern.launcher/.MainActivity

# Optional (API 29+): set as default home
adb shell cmd role add-role-holder android.app.role.HOME com.kern.launcher

# Stress test
adb shell monkey -p com.kern.launcher --throttle 100 --pct-syskeys 0 -v 2000

# Crash and ANR detection
adb logcat -b crash -d
adb logcat -d | grep -E "FATAL EXCEPTION|ANR in|StrictMode|ClassNotFoundException|NoSuchMethodException"
```

- [ ] App installs and launches with no crash.
- [ ] Cold start `TotalTime` recorded (flag as `FINDING` if above 1000 ms on the test device; state the device model).
- [ ] Monkey test completes with no `FATAL EXCEPTION` or `ANR`.
- [ ] Simulate process death and confirm that the app restarts into a valid state:

```bash
adb shell am start -n com.kern.launcher/.MainActivity
adb shell am kill com.kern.launcher
adb shell am start -n com.kern.launcher/.MainActivity
adb logcat -b crash -d
```

- [ ] Clean uninstall succeeds: `adb uninstall com.kern.launcher`.
- [ ] Any visual or interaction check that cannot be verified from the command line is listed under **[HUMAN]** (Phase 12).

## Phase 10: Continuous Integration [AUTO+REVIEW]

Create `.github/workflows/android.yml` (adapt if another CI system is in use). Pin each action to its current stable major version at the time of writing.

```yaml
name: Android CI
on:
  push:
    branches: [main]
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: gradle/actions/wrapper-validation@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
      - uses: gradle/actions/setup-gradle@v4
      - name: Build, test, lint
        run: ./gradlew assembleDebug testDebugUnitTest lintDebug
      - name: Release build (unsigned)
        run: ./gradlew assembleRelease
      - uses: actions/upload-artifact@v4
        if: always()
        with:
          name: reports
          path: |
            app/build/reports/
            app/build/outputs/mapping/
```

- [ ] Workflow file created and syntactically valid (`python -c "import yaml,sys; yaml.safe_load(open('.github/workflows/android.yml'))"`).
- [ ] Confirm that the JDK version in the workflow matches the project's requirement (AGP version determines the minimum).
- [ ] Do not add signing secrets to the workflow; flag release signing in CI as **[HUMAN]**.

## Phase 11: Documentation and Metadata [AUTO+REVIEW]

- [ ] `README.md`: verify that it contains installation, how to set Kern as the default launcher, the full command reference (generate this from `CommandParser` / `HelpView` so that it is accurate), build instructions, and known limitations. Record gaps and fill them.
- [ ] Create `CHANGELOG.md` (Keep a Changelog format) with an entry for the release candidate version, derived from `git log`.
- [ ] Create `PRIVACY.md` as a **draft**, describing exactly what data is stored locally (aliases, command history, hidden apps, usage counts, settings), that no data leaves the device (only if Phase 5 confirms there is no network use other than `DownloadDialog`; otherwise describe it accurately), and how to clear it. Mark the file header "DRAFT: requires human review".
- [ ] Verify that a `LICENSE` file exists; if absent, record `FINDING` (do not choose a licence on the user's behalf).
- [ ] Verify that `app/src/main/res/values/strings.xml` contains the correct `app_name`, and that no user-visible string is hard-coded in Kotlin (`grep -rn "Text(\"" app/src/main/java`; list matches as `FINDING` if the app is meant to be localised).
- [ ] Remove or relocate `projectplanningnextphase.md` if it is not intended to be public (flag as `FINDING`; do not delete without approval).

## Phase 12: Items Requiring a Human [HUMAN]

Do not attempt these. List each in the report with its current status.

- [ ] Generate or supply the release keystore; back it up in two locations.
- [ ] Provide signing environment variables for the final signed build.
- [ ] Enrol in Play App Signing; create the Play Console app entry.
- [ ] Complete the Play permission declaration (if `QUERY_ALL_PACKAGES` is used).
- [ ] Complete the Data Safety form, content rating, target audience, and ads declaration.
- [ ] Review and publish `PRIVACY.md` at a public URL.
- [ ] Prepare the store listing: descriptions, 512 px icon, feature graphic, screenshots.
- [ ] Confirm the current Google Play `targetSdk` deadline and any testing requirements for new developer accounts.
- [ ] Manual verification on physical devices: gestures and back navigation, TalkBack, font scale at 200 percent, light and dark themes, rotation, keyboard behaviour, work profile (if supported).
- [ ] Choose the open-source licence (if public).
- [ ] Decide on the rollout plan (internal, closed, staged production).
- [ ] Review every `FINDING` in `release-report.md` and accept or reject each fix proposed by the agent.

---

## Final Deliverable: `release-report.md`

The agent must produce this file in the repository root, using the following structure:

```markdown
# Release Preparation Report: Kern Launcher
Date: <ISO date>   Branch: <name>   Commit: <short hash>
Environment: JDK <v>, Gradle <v>, AGP <v>, device: <model or none>

## Summary
| Phase | Status | Fixed | Findings | Blocked |
|---|---|---|---|---|

## Artifacts
- APK: <path>, <size>, SHA-256 <hash>   (signed: yes/no)
- AAB: <path>, <size>, SHA-256 <hash>
- R8 mapping: <path>

## Findings (ordered by severity: Blocker, High, Medium, Low)
### F-001 <title>
- Phase: N
- Location: <file:line>
- Evidence: <command output excerpt>
- Proposed action: <text>
- Status: open / fixed in <commit>

## Changes Made
<commit hash and one-line description per phase>

## Human Actions Remaining
<copy of Phase 12 with current status>

## Commands That Failed
<command, error excerpt, fix attempts>
```

**Completion criteria:** `assembleRelease` and `bundleRelease` succeed; `testDebugUnitTest` passes; `lintRelease` reports zero errors; no secrets in the repository; every `[HUMAN]` item is listed; `release-report.md` exists and is committed. Do not merge the branch; leave the merge decision to the human.
