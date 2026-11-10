# Changelog

All notable changes to Kern Launcher will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.3] - 2026-11-10

### Added
- **OLED Black Theme (Default)**: Introduced 0-saturation pure black (`oledblack` / `#000000`) theme palette with high-contrast monochrome text and accents, optimized for OLED displays and battery savings. Set as the default theme.
- **Short Canonical Shortcuts**: Canonical command keywords streamlined to preferred short forms (`dl`, `map`, `spot`, `ps`, `gh`, `wiki`, `r`, `x`, `g`, `log`, `tui`, `hidden`, `pref`, `conf`, `?`, `timer`, `ai`).
- **Launcher Preferences Command (`pref` / `conf`)**: Dedicated shortcuts `pref` and `conf` to open launcher settings, freeing `settings` to open the phone's system Settings app without collision.
- **Direct Google Search App Integration**: Updated `g <query>` to open native Google Search app (`com.google.android.googlequicksearchbox` via `ACTION_WEB_SEARCH`) instead of generic browser tab.
- **Icon Caching & Background App Scanning**: Added in-memory icon cache with `ConcurrentHashMap` and offloaded app list scanning to `Dispatchers.IO` for instantaneous search response.
- **Fixed Fallback Release Signing**: Added fallback signing configuration to `app/build.gradle.kts` for direct local testing and sideloading of minified release builds.

### Removed
- **Custom Aliases Feature**: Removed custom alias manager, settings card, and resolution logic to reduce complexity and avoid query shadowing.
- **Redundant Synonyms**: Removed redundant command synonyms (`maps`, `spotify`, `music`, `play`, `store`, `github`, `wikipedia`, `reddit`, `tw`, `twitter`, `duck`, `google`, `down`, `ytdl`, `lazylogs`, `tuiview`, `terminal`, `hiddenapps`, `secret`, `config`, `manual`).
- **DuckDuckGo Shortcut**: Removed `ddg` command per user preference.

### Fixed
- **Android 15 / Modern OS Back Gesture Conflict**: Fixed root touch gesture interception on `HomeScreen.kt` and made `BackHandler` conditional on idle state so modern Android predictive back gestures and system navigation work seamlessly without lag or "not optimized" system warnings.
- **Clock Recomposition Lag**: Optimized `ClockView` and `DateView` recompositions from 1-second ticks to minute-boundary alignment, eliminating idle UI re-renders.

---

## [1.0.2] - 2026-10-21

### Added
- **yt-dlp Companion**: Added `down <link>` and `dl` companion dialog for delegating media links to YTDLnis, NewPipe, or Cobalt web service.
- **Unit Test Suite**: 38 comprehensive unit tests covering `InputTokenizer`, `CommandParser`, `CalculatorEvaluator`, `SearchRanker`, `AliasRepository`, and `UserSettings`.
- **Database Schema Export**: Configured Room schema export with version 2 JSON schema asset.
- **DataStore Corruption Handler**: Added `ReplaceFileCorruptionHandler` for resilience against file corruption.
- **R8 Minification & Shrinking**: Configured ProGuard rules, resource shrinking, and automated strip of framework logging calls.
- **Continuous Integration**: Added GitHub Actions CI pipeline (`.github/workflows/android.yml`) with wrapper validation, Temurin 17, and unsigned release build checks.
- **38+ Color Themes**: Added Monkeytype theme presets with real-time theme search filtering.
- **Clock Toggle**: Added home screen clock display toggle option in Settings.

### Removed
- Removed Seal downloader integration from yt-dlp companion dialog and manifest queries (app discontinued).

### Changed
- Promoted YTDLnis as the primary native yt-dlp companion client.
- Removed Room `.fallbackToDestructiveMigration()` in favor of non-destructive schema preservation.
- Corrected alarm permission to `com.android.alarm.permission.SET_ALARM`.
- Replaced TUI prompt symbol with `>` and restructured help manual.
- Updated documentation with default launcher instructions, command reference, and privacy policy.

### Fixed
- Fixed soft keyboard re-focusing on screen unlock and `ON_RESUME` events.
- Fixed back button handling on home screen to prevent activity restart.
- Fixed IME padding in settings screen to prevent keyboard overlap on input fields.
- Fixed duplicate key event and focus race crashes in TUI mode.

---

## [1.0.1] - 2026-10-14

### Added
- **yt-dlp Companion**: Added `down <link>` and `dl` companion dialog for delegating media links to Seal, YTDLnis, or Cobalt web service.
- **Unit Test Suite**: 38 comprehensive unit tests covering `InputTokenizer`, `CommandParser`, `CalculatorEvaluator`, `SearchRanker`, `AliasRepository`, and `UserSettings`.
- **Database Schema Export**: Configured Room schema export with version 2 JSON schema asset.
- **DataStore Corruption Handler**: Added `ReplaceFileCorruptionHandler` for resilience against file corruption.
- **R8 Minification & Shrinking**: Configured ProGuard rules, resource shrinking, and automated strip of framework logging calls.
- **Continuous Integration**: Added GitHub Actions CI pipeline (`.github/workflows/android.yml`) with wrapper validation, Temurin 17, and unsigned release build checks.
- **38+ Color Themes**: Added Monkeytype theme presets with real-time theme search filtering.
- **Clock Toggle**: Added home screen clock display toggle option in Settings.

### Changed
- Removed Room `.fallbackToDestructiveMigration()` in favor of non-destructive schema preservation.
- Corrected alarm permission to `com.android.alarm.permission.SET_ALARM`.
- Replaced TUI prompt symbol with `>` and restructured help manual.
- Updated documentation with default launcher instructions, command reference, and privacy policy.

### Fixed
- Fixed soft keyboard re-focusing on screen unlock and `ON_RESUME` events.
- Fixed back button handling on home screen to prevent activity restart.
- Fixed IME padding in settings screen to prevent keyboard overlap on input fields.
- Fixed duplicate key event and focus race crashes in TUI mode.

---

## [1.0.0] - 2026-07-23

### Added
- Initial release of Kern Launcher.
- Keyboard-first command bar interface built with Jetpack Compose.
- Instant fuzzy app search and launching.
- Deep search shortcuts: YouTube, Spotify, GitHub, Wikipedia, Reddit, X (Twitter), Google, Play Store, and Google Maps.
- System settings index (`set <query>`).
- Configurable AI search provider integration (`ai <prompt>`).
- Inline mathematical calculator.
- Command history tracking and up/down arrow navigation.
- Custom alias manager.
- Hidden applications manager (`hide`, `unhide`, `hidden`).
- Experimental retro ASCII terminal TUI mode (`tui`).
- Custom theme builder with HSV color picker and preset palettes.
- Swipe left and right gesture shortcut execution.
- Monospace font integration (JetBrains Mono) and borderless text presentation.
