# Kern Launcher

A raw, keyboard-driven, ultra-minimalist Android launcher designed for power users. Built with Jetpack Compose & Kotlin.

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-GPL%20v3.0-blue.svg)](LICENSE)

---

## Overview

Kern Launcher replaces cluttered app grids and infinite icon pages with a clean, command-bar interface. Type to launch applications, evaluate calculations, trigger instant deep-search shortcuts into your favorite apps, integrate custom URI deep links, or query AI assistants—all from your home screen without distraction.

---

## Setting Kern as Default Launcher

To make Kern your primary home screen launcher:
1. Open Android **Settings**.
2. Navigate to **Apps** (or **Apps & Notifications**) → **Default Apps** → **Home app**.
3. Select **Kern**.
4. Alternatively, press your device's **Home button** or swipe up to go home; when prompted by Android, select **Kern** and choose **Always**.

---

## Features

- **Blazing Fast App Launching**: Instant fuzzy search across installed apps with deterministic ranking and auto-launch capability.
- **Deep Search Shortcuts**: Search directly inside YouTube, Spotify, GitHub, Wikipedia, Reddit, X (Twitter), DuckDuckGo, Google, and the Play Store.
- **System Settings Index**: Query 25+ Android system settings pages directly using `set <query>` (e.g. `set wifi`, `set battery`, `set display`, `set dns`).
- **Configurable AI Command**: Run `ai <prompt>` to query your preferred AI service (ChatGPT, Google Gemini, Perplexity, or Claude).
- **yt-dlp Companion**: Run `down <link>` or `dl` to open an intent-based companion dialog routing media URLs to user-installed downloaders (Seal, YTDLnis) or web tools (Cobalt).
- **Inline Calculator**: Type mathematical expressions directly into the command bar (e.g. `24 * 7`, `(12 + 8) / 4`, `2^8`) for instant answers.
- **Native Deep Link Support**: Execute custom URI schemes (e.g. `lazylogs://add?text=lunch%2025k`) or use the built-in `log <text>` financial tracking shortcut.
- **Built-in App & System Utilities**:
  - Quick countdown timer (`timer 20m`, `timer 30s`, `timer 1h`)
  - App settings shortcut (`info <app>`)
  - Hide & unhide apps (`hide <app>`, `unhide <app>`, `hidden`)
  - User-defined aliases with cycle detection (`settings` → Custom Aliases)
- **Deep Aesthetic Customization**:
  - **Wallpaper Passthrough**: Transparent background option with blur / glassmorphism.
  - **Custom Theme Builder**: Full Hex & HSV visual color picker for Background, Primary Accent, and Text colors.
  - **Preset Color Palettes**: 38+ themes including VS Code Dark+, OLED Pitch Black, Dracula, Monokai Pro, One Dark, Tokyo Night, Gruvbox, Nord, Cyberpunk, and iconic mechanical keyboard palettes.
  - **Typography & Font Sizes**: JetBrains Mono (Raw Monospace), Sans-Serif, Serif, Cursive, and System Default fonts.
  - **Independent Alignments**: Separate Left, Center, or Right alignments for Clock and App List.
  - **Borderless Text Mode**: Hide container cards for pure text-only presentation.
- **Experimental Retro TUI Mode**: Pure ASCII terminal user interface with bordered window boxes (`tui`).
- **Gesture Shortcuts**: Assign custom apps to Swipe Left and Swipe Right home gestures.
- **Interactive Clock & Calendar**: Tap time to open System Clock and tap date to open System Calendar.

---

## Command Reference

The table below lists all built-in commands supported by `CommandParser`:

| Command / Keyword | Arguments | Description | Example |
| :--- | :--- | :--- | :--- |
| **`<app_name>`** | — | Launch an installed application | `whatsapp` |
| **`<math_expr>`** | — | Inline mathematical calculation | `(120 * 4) + 15` |
| **`settings`**, `config`, `pref` | — | Open Kern Launcher settings screen | `settings` |
| **`help`**, `?`, `manual` | — | Open interactive manual dialog | `help` |
| **`set <query>`** | `<query>` | Search 25+ Android System Settings | `set wifi`, `set battery` |
| **`tui`**, `tuiview`, `terminal` | — | Toggle retro ASCII TUI terminal mode | `tui` |
| **`info <app>`** | `<app>` | Open system App Info settings page | `info instagram` |
| **`hide <app>`** | `<app>` | Hide application from search results | `hide games` |
| **`unhide <app>`** | `<app>` | Restore hidden application to search results | `unhide games` |
| **`hidden`**, `hiddenapps`, `secret` | — | View and manage all hidden applications | `hidden` |
| **`ai <prompt>`** | `<prompt>` | Query configured AI provider | `ai explain p vs np` |
| **`down <link>`**, `dl`, `ytdl` | `[link]` | Open yt-dlp downloader companion | `down https://youtu.be/...` |
| **`maps <loc>`**, `map` | `<location>` | Search location or directions on Google Maps | `maps tokyo station` |
| **`timer <duration>`** | `<time>` | Set countdown timer (`h`, `m`, `s`) | `timer 25m`, `timer 45s` |
| **`log <text>`**, `lazylogs` | `<text>` | Append entry to LazyLogs app | `log coffee 20k` |
| **`<scheme>://...`** | — | Direct execute custom app deep link URI | `obsidian://open?vault=work` |
| **`g <query>`**, `google` | `<query>` | Search Google in browser | `g android compose` |
| **`ddg <query>`**, `duck` | `<query>` | Search DuckDuckGo in browser | `ddg privacy guide` |
| **`yt <query>`** | `<query>` | Search YouTube (app or browser) | `yt lofi beats` |
| **`spot <query>`**, `spotify`, `music` | `<query>` | Search Spotify (app or browser) | `spot daft punk` |
| **`play <query>`**, `store`, `ps` | `<query>` | Search Google Play Store | `play termux` |
| **`gh <query>`**, `github` | `<query>` | Search GitHub repositories | `gh compose` |
| **`wiki <query>`**, `wikipedia` | `<query>` | Search Wikipedia articles | `wiki quantum electrodynamics` |
| **`reddit <query>`**, `r` | `<query>` | Search Reddit posts or subreddits | `r androiddev` |
| **`x <query>`**, `tw`, `twitter` | `<query>` | Search posts on X / Twitter | `x jetpack compose` |

### Keyboard & Gesture Navigation
- **Enter / Return**: Execute query, launch top app, or run command.
- **Up / Down Arrow**: Cycle through historical commands.
- **Swipe Left / Right**: Trigger configured gesture application shortcuts.
- **Tap Clock**: Launch device system clock / alarm.
- **Tap Date**: Launch device system calendar.

---

## Building from Source

### Prerequisites
- JDK 17 (e.g. Eclipse Temurin 17)
- Android SDK with `compileSdk 35` / `targetSdk 35` (Android 15)
- Git

### Build Debug APK
```bash
git clone https://github.com/USERNAME/kern-launcher.git
cd kern-launcher
./gradlew assembleDebug testDebugUnitTest
```
Debug APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Build Minified Release APK & App Bundle
```bash
./gradlew assembleRelease bundleRelease
```
Outputs:
- Release APK: `app/build/outputs/apk/release/app-release-unsigned.apk`
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`
- ProGuard / R8 Mapping: `app/build/outputs/mapping/release/mapping.txt`

To sign the release build, provide the following environment variables:
```bash
export KERN_KEYSTORE_PATH="/path/to/keystore.jks"
export KERN_KEYSTORE_PASSWORD="keystore-password"
export KERN_KEY_ALIAS="key-alias"
export KERN_KEY_PASSWORD="key-password"
./gradlew assembleRelease
```

---

## Known Limitations

- **No Home Screen Widgets**: Kern Launcher is strictly keyboard- and text-driven by design; Android AppWidget hosts are intentionally not implemented.
- **Requires Keyboard Input**: Best suited for physical keyboards or software virtual keyboards; icon grid browsing is intentionally absent.
- **Package Visibility (`QUERY_ALL_PACKAGES`)**: On Android 11+ (API 30+), launcher apps require visibility to query all installed packages to present an app list. For Google Play releases, a Sensitive Permission Declaration is required.
- **No Background Network Sync**: Kern Launcher operates entirely offline and does not contain background network sync or telemetry services.

---

## Privacy

Kern Launcher is designed with privacy as a foundational principle:
- No telemetry, analytics, or remote tracking.
- No network permissions are requested by the app itself.
- All aliases, command history, and preferences remain strictly on your local device.

See [PRIVACY.md](PRIVACY.md) for full details.

---

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
