# Release Preparation Report: Kern Launcher
Date: 2026-10-13   Branch: release/prep-20261013   Commit: 00fb7b5
Environment: JDK 17 (Temurin 17.0.20.1) / Gradle 8.11.1, AGP 8.7.2, device: none

## Summary
| Phase | Status | Fixed | Findings | Blocked |
|---|---|---|---|---|
| Phase 0: Preflight | PASS | 0 | 0 | 0 |
| Phase 1: Baseline Health | FAIL | 1 | 2 | 0 |
| Phase 2: Manifest & Launcher Role Audit | PASS | 0 | 4 | 0 |
| Phase 3: Build Configuration | PASS | 1 | 2 | 0 |
| Phase 4: Room & DataStore Safety | PASS | 2 | 1 | 0 |
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

## Phase 3 Build Configuration Details
- **Minification & Shrinking:** Enabled (`isMinifyEnabled = true`, `isShrinkResources = true`).
- **Debuggable Flag:** Configured (`isDebuggable = false`).
- **ProGuard Configuration:** `proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")`. Created `app/proguard-rules.pro`.
- **Signing Configuration:** Configured release `signingConfigs` consuming `KERN_KEYSTORE_PATH`, `KERN_KEYSTORE_PASSWORD`, `KERN_KEY_ALIAS`, `KERN_KEY_PASSWORD` via environment variables. Does not fail when variables are absent (falls back cleanly to unsigned build).
- **Git Ignore Hygiene:** Added `keystore.properties` and `/app/release/` to `.gitignore`. Verified existing entries for `*.jks`, `*.keystore`, `local.properties`.
- **Secret Scanning:**
  - Binary/Key files (`*.jks`, `*.keystore`, `*.p12`, `*.pem`): None tracked in git index.
  - Text pattern scan: Flagged `F-007` for hard-coded debug credentials in `app/build.gradle.kts`.
- **Target SDK Review:** `targetSdk = 35` (Android 15), `versionCode = 2`. Flagged `F-008`.
- **Verification Build:** PASS (`./gradlew assembleRelease --stacktrace` succeeded, output size: 1.38 MB).

## Phase 4 Room & DataStore Safety Details
- **Room Schema Export:** PASS (`exportSchema = true` enabled in `AppDatabase.kt`; KSP `room.schemaLocation` configured).
- **Generated Schema:** Generated and committed `app/schemas/com.kern.launcher.data.room.AppDatabase/2.json`.
- **Destructive Migration Removed:** PASS. Removed `.fallbackToDestructiveMigration()` from `AppDatabase.getInstance()`. Flagged `F-009` regarding database version 2 without prior version 1 schema.
- **Test Infrastructure:** Added `androidx.room:room-testing:2.6.1` and linked schema directory in `androidTest` sourceSets assets.
- **DAO Verification Test:** Created `app/src/androidTest/java/com/kern/launcher/data/room/AppDatabaseDaoTest.kt` verifying in-memory round trips across all DAOs (`AppUsageDao`, `CommandHistoryDao`, `AliasDao`, `HiddenAppDao`).
- **SettingsDataStore Hardening:** PASS. Added `corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }`. Confirmed all preference keys have non-null default fallbacks in `userSettings` Flow mapper.
- **Threading and DAOs:** Verified all write operations in DAOs are `suspend` functions and query methods return `Flow` or `suspend`. Scanned codebase for `runBlocking` in `app/src/main` (0 matches).

## Artifacts
- APK: pending (interim unsigned APK built at `app/build/outputs/apk/release/app-release-unsigned.apk`)
- AAB: pending
- R8 mapping: `app/build/outputs/mapping/release/mapping.txt`

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

### F-007 Hardcoded Debug Keystore Credentials
- Phase: 3
- Severity: Low
- Location: `app/build.gradle.kts:28,30`
- Evidence: `storePassword` and `keyPassword` values hard-coded in `signingConfigs.getByName("fixedDebug")`.
- Proposed action: Standard practice for local debug keystores, but for clean release hygiene, can be migrated to environment variables or Gradle project properties.
- Status: open

### F-008 Target SDK Assessment Against Google Play Requirements
- Phase: 3
- Severity: Low
- Location: `app/build.gradle.kts:15`
- Evidence: `targetSdk = 35`, `versionCode = 2`.
- Proposed action: Per official Google Play policy ([Target SDK Requirements](https://developer.android.com/google/play/requirements/target-sdk)), apps submitted after August 31, 2024 must target Android 14 (API 34)+, and by August 31, 2025 must target Android 15 (API 35)+. Kern Launcher currently targets API 35 (Android 15), fully complying with current and upcoming requirements. No change needed.
- Status: open

### F-009 Database Version 2 Without Historical Version 1 Schema
- Phase: 4
- Severity: Medium
- Location: `app/src/main/java/com/kern/launcher/data/room/AppDatabase.kt:23`
- Evidence: Database `version = 2` is defined in initial repository commits with `fallbackToDestructiveMigration()` and without explicit `Migration(1, 2)` or `1.json` schema.
- Proposed action: With `fallbackToDestructiveMigration()` now removed, any device running a hypothetical version 1 database would fail on upgrade. If version 2 was the first public release, database versioning should be stabilized at version 2 or explicit migrations added if schema changes occur in future versions.
- Status: open

## Changes Made
- `release-prep: phase 0 preflight`: Initial preflight checks, environment capture, and initial release-report.md.
- `release-prep: phase 1 baseline health`: Installed Temurin JDK 17 to `~/.jdks/temurin-17` and updated `gradlew` to ensure AGP/Gradle/JVM stability; recorded baseline health results.
- `release-prep: phase 2 manifest audit`: Completed manifest, exported component, permission, and launcher role audit.
- `release-prep: phase 3 build configuration`: Configured R8 minification, resource shrinking, non-fatal environment-based signing config, created `proguard-rules.pro`, updated `.gitignore`.
- `release-prep: phase 4 room and datastore safety`: Enabled `exportSchema = true`, generated `2.json`, removed `fallbackToDestructiveMigration()`, added DataStore corruption handler, added Room testing & DAO round-trip test.

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
