# Release Preparation Report: Kern Launcher
Date: 2026-10-13   Branch: release/prep-20261013   Commit: 8eacefc
Environment: JDK 17 (Temurin 17.0.20.1) / Gradle 8.11.1, AGP 8.7.2, device: none

## Summary
| Phase | Status | Fixed | Findings | Blocked |
|---|---|---|---|---|
| Phase 0: Preflight | PASS | 0 | 0 | 0 |
| Phase 1: Baseline Health | FAIL | 1 | 2 | 0 |
| Phase 2: Manifest & Launcher Role Audit | PASS | 0 | 4 | 0 |
| Phase 3: Build Configuration | PENDING | 0 | 0 | 0 |
| Phase 4: Room & DataStore Safety | PENDING | 0 | 0 | 0 |
| Phase 5: Static Security & Privacy Scan | PENDING | 0 | 0 | 0 |
| Phase 6: Generate & Run Unit Tests | PENDING | 0 | 0 | 0 |
| Phase 7: Lint & Style | PENDING | 0 | 0 | 0 |
| Phase 8: Release Build & R8 Verification | PENDING | 0 | 0 | 0 |
| Phase 9: Device Smoke Test | SKIPPED (no device) | 0 | 0 | 0 |
| Phase 10: Continuous Integration | PENDING | 0 | 0 | 0 |
| Phase 11: Documentation & Metadata | PENDING | 0 | 0 | 0 |
| Phase 12: Items Requiring a Human | HUMAN | 0 | 0 | 0 |

## Project Inventory
- **Application ID:** `com.kern.launcher`
- **Namespace:** `com.kern.launcher`
- **minSdk:** 26 (Android 8.0 Oreo)
- **targetSdk:** 35 (Android 15)
- **compileSdk:** 35
- **versionCode:** 2
- **versionName:** 1.0.1
- **AGP Version:** 8.7.2
- **Kotlin Version:** 2.0.21
- **Compose BOM:** 2024.11.00
- **Room:** 2.6.1
- **DataStore:** 1.1.1
- **Coroutines:** 1.9.0
- **KSP:** 2.0.21-1.0.27
- **ANDROID_HOME:** `/home/kimmi/Android/Sdk`
- **adb status:** Available, no device or emulator attached (Phase 9 marked `SKIPPED (no device)`)

## Phase 1 Execution Details
- **Debug Build:** PASS (`./gradlew assembleDebug` succeeded).
- **Unit Tests:** `0 tests` executed (`testDebugUnitTest` had `NO-SOURCE`). Flagged as finding `F-001`.
- **Lint Debug Task:** FAIL (`:app:lintDebug` aborted build with 1 error, 43 warnings).
- **Lint Report Path:** `app/build/reports/lint-results-debug.html` (text report: `app/build/intermediates/lint_intermediate_text_report/debug/lintReportDebug/lint-results-debug.txt`).
- **Lint Breakdown:**
  - Errors (1):
    - `QueryAllPackagesPermission`: `app/src/main/AndroidManifest.xml:4`
  - Warnings (43):
    - `InlinedApi` (1): `SearchEngine.kt:35` (`Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS` requires API 33)
    - `OldTargetApi` (1): `app/build.gradle.kts:15`
    - `AndroidGradlePluginVersion` (3): `gradle/libs.versions.toml:2`
    - `GradleDependency` (29): Outdated dependencies in `gradle/libs.versions.toml`
    - `ModifierParameter` (6): Compose modifier parameter order
    - `SystemPermissionTypo` (1): `AndroidManifest.xml:5` (`SET_ALARM` namespace)
    - `ObsoleteSdkInt` (1): `res/mipmap-anydpi-v26` qualifier redundant for minSdk 26
    - `MissingClass` (1): External package referenced in code

## Phase 2 Manifest & Launcher Role Audit Details
- **Merged Release Manifest Path:** `app/build/intermediates/merged_manifests/release/processReleaseManifest/AndroidManifest.xml`
- **MainActivity Intent Filter:** PASS
  - Actions: `android.intent.action.MAIN`
  - Categories: `android.intent.category.HOME`, `android.intent.category.DEFAULT`, `android.intent.category.LAUNCHER`
- **Launch Mode:** PASS (`singleTask`, configured on `MainActivity`).
- **Export Declarations:** PASS. Every component declares explicit `android:exported`:
  - `MainActivity`: `exported="true"` (Launcher activity)
  - `androidx.startup.InitializationProvider`: `exported="false"`
  - `androidx.room.MultiInstanceInvalidationService`: `exported="false"`
  - `androidx.profileinstaller.ProfileInstallReceiver`: `exported="true"`, protected by `android.permission.DUMP`.
- **Declared Permissions Audit:**
  - `android.permission.QUERY_ALL_PACKAGES`: Used in `AppRepository.kt:81` for querying installed applications. Flagged as `F-004`.
  - `android.permission.SET_ALARM`: Used in `CommandExecutor.kt` and `IntentFactory.kt`. Flagged as `F-003` (typo: should be `com.android.alarm.permission.SET_ALARM`).
  - `com.kern.launcher.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`: Signature-level protection for dynamic broadcast receivers.
- **Backup Configuration:** `allowBackup="true"` without extraction rules. Flagged as `F-005`. Propose excluding Room database and DataStore.
- **Cleartext Traffic & Security Config:** PASS. `usesCleartextTraffic` is absent (defaults to false on API 28+). `DownloadDialog.kt` performs no direct HTTP network requests.
- **Debuggable in Release:** PASS (`android:debuggable` is not present in release manifest).
- **Adaptive Icon Check:** FINDING `F-006`. Current launcher icon is a vector drawable in `res/drawable/ic_launcher.xml`; missing `res/mipmap-anydpi-v26/` adaptive icon with `<monochrome>` layer.

## Artifacts
- APK: pending
- AAB: pending
- R8 mapping: pending

## Findings (ordered by severity: Blocker, High, Medium, Low)
### F-001 Zero Unit Tests in Repository
- Phase: 1
- Severity: High
- Location: `app/src/test/`
- Evidence: `> Task :app:testDebugUnitTest NO-SOURCE`
- Proposed action: Implement test suite covering InputTokenizer, CommandParser, CalculatorEvaluator, SearchRanker, AliasRepository, and UserSettings in Phase 6.
- Status: open

### F-002 Lint Error: QueryAllPackagesPermission
- Phase: 1
- Severity: High
- Location: `app/src/main/AndroidManifest.xml:4`
- Evidence: `app/src/main/AndroidManifest.xml:4: Error: A <queries> declaration should generally be used instead of QUERY_ALL_PACKAGES; see https://g.co/dev/packagevisibility for details [QueryAllPackagesPermission]`
- Proposed action: Address in lint configuration / Phase 7; see F-004.
- Status: open

### F-003 Manifest Permission Typo for Alarm
- Phase: 2
- Severity: Medium
- Location: `app/src/main/AndroidManifest.xml:5`
- Evidence: `<uses-permission android:name="android.permission.SET_ALARM" />` (Android framework defines `com.android.alarm.permission.SET_ALARM`).
- Proposed action: Propose updating permission string to `com.android.alarm.permission.SET_ALARM`.
- Status: open

### F-004 Play Permission Declaration Required: QUERY_ALL_PACKAGES
- Phase: 2
- Severity: High
- Location: `app/src/main/AndroidManifest.xml:4`
- Evidence: `<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />`
- Proposed action: [HUMAN] Google Play policy requires submitting a sensitive permission declaration form for `QUERY_ALL_PACKAGES`. As Kern Launcher is an Android Home/Launcher app, it falls within an permitted use case.
  - *Alternative non-sensitive replacement:* `AndroidManifest.xml` already defines a `<queries>` block with `<intent><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent></queries>`. On Android 11+ (API 30+), this allows resolving all launcher activities without `QUERY_ALL_PACKAGES`. Removing `QUERY_ALL_PACKAGES` would eliminate the need for the Play declaration.
- Status: open

### F-005 Data Backup Enabled Without Exclusion Rules
- Phase: 2
- Severity: Medium
- Location: `app/src/main/AndroidManifest.xml:47`
- Evidence: `android:allowBackup="true"` without `dataExtractionRules` or `fullBackupContent`.
- Proposed action: Configure `android:dataExtractionRules="@xml/data_extraction_rules"` and `android:fullBackupContent="@xml/backup_rules"` to exclude `databases/` (command history and sensitive user aliases) and `shared_prefs/` / `datastore/` from cloud backups.
- Status: open

### F-006 Missing Adaptive Icon with Monochrome Layer
- Phase: 2
- Severity: Low
- Location: `app/src/main/res/`
- Evidence: Launcher icon is provided as `res/drawable/ic_launcher.xml`. No `res/mipmap-anydpi-v26/` or `res/mipmap-anydpi/` exists with `<adaptive-icon>` and `<monochrome>` elements for Android 13+ themed icons.
- Proposed action: Create adaptive launcher icon XML referencing foreground, background, and monochrome layers.
- Status: open

## Changes Made
- `release-prep: phase 0 preflight`: Initial preflight checks, environment capture, and initial release-report.md.
- `release-prep: phase 1 baseline health`: Installed Temurin JDK 17 to `~/.jdks/temurin-17` and updated `gradlew` to ensure AGP/Gradle/JVM stability; recorded baseline health results.
- `release-prep: phase 2 manifest audit`: Completed manifest, exported component, permission, and launcher role audit.

## Human Actions Remaining
- [ ] Generate or supply the release keystore; back it up in two locations.
- [ ] Provide signing environment variables for the final signed build.
- [ ] Enrol in Play App Signing; create the Play Console app entry.
- [ ] Complete the Play permission declaration (if `QUERY_ALL_PACKAGES` is used) (see F-004).
- [ ] Complete the Data Safety form, content rating, target audience, and ads declaration.
- [ ] Review and publish `PRIVACY.md` at a public URL.
- [ ] Prepare the store listing: descriptions, 512 px icon, feature graphic, screenshots.
- [ ] Confirm the current Google Play `targetSdk` deadline and any testing requirements for new developer accounts.
- [ ] Manual verification on physical devices: gestures and back navigation, TalkBack, font scale at 200 percent, light and dark themes, rotation, keyboard behaviour, work profile (if supported).
- [ ] Choose the open-source licence (if public).
- [ ] Decide on the rollout plan (internal, closed, staged production).
- [ ] Review every `FINDING` in `release-report.md` and accept or reject each fix proposed by the agent.

## Commands That Failed
- Command: `./gradlew clean assembleDebug testDebugUnitTest lintDebug --stacktrace`
- Failing Task: `:app:lintDebug`
- Error Excerpt:
  ```
  FAILURE: Build failed with an exception.
  * What went wrong:
  Execution failed for task ':app:lintDebug'.
  > Lint found errors in the project; aborting build.
  app/src/main/AndroidManifest.xml:4: Error: A <queries> declaration should generally be used instead of QUERY_ALL_PACKAGES [QueryAllPackagesPermission]
  ```
- Fix attempts: Evaluated under Agent Rule 8 & Phase 1 rule ("Do not suppress lint. Fix only genuine breakage"). Lint suppression not applied; flagged as Finding F-002 for human/Phase 2 review.
