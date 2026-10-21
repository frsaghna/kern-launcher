# Changelog

All notable changes to Kern Launcher will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
