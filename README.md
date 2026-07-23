# Kern Launcher

A raw, keyboard-driven, ultra-minimalist Android launcher designed for power users. Built with Jetpack Compose & Kotlin.

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-GPL%20v3.0-blue.svg)](LICENSE)

---

## Overview

Kern Launcher replaces cluttered app grids with a clean, command-bar interface. Type to launch applications, run instant deep search shortcuts across your favorite apps, integrate custom URI deep links, or query AI assistants—all without leaving your home screen.

---

## Features

- **Fast App Launcher**: Instant fuzzy search with auto-launch capability.
- **Deep Search Shortcuts**: Instant search into YouTube, Spotify, GitHub, Wikipedia, Reddit, X/Twitter, Play Store, and Google.
- **Configurable AI Command**: Type `ai <prompt>` to query your preferred AI provider (ChatGPT, Google Gemini, Perplexity, or Claude).
- **Native Deep Link Support**: Execute custom URI schemes (e.g. `lazylogs://add?text=lunch%2025k`) or use the built-in `log <text>` financial tracking shortcut.
- **Built-in Utilities**: Quick calculator (`calc 15*12`), timer alarm (`timer 20m`), app settings shortcut (`info <app>`), and app management (`hide <app>` / `unhide <app>`).
- **Deep Aesthetic Customization**:
  - **Wallpaper Passthrough**: Transparent background option with glassmorphism elements.
  - **Custom Theme Builder**: Full Hex color picker for Background, Primary Accent, and Text colors.
  - **Preset Color Palettes**: VS Code Dark+, OLED Pitch Black, Dracula, Monokai Pro, One Dark, Tokyo Night, Gruvbox, Nord, and Cyberpunk.
  - **Typography & Font Sizes**: JetBrains Mono (Raw Monospace), Sans-Serif, Serif, Cursive, and System Default fonts.
  - **Independent Alignments**: Separate Left, Center, or Right alignments for Clock and App List.
  - **Borderless Text Mode**: Toggle square outlines to hide container cards for a pure text-only UI.
- **Experimental Retro TUI Mode**: 100% pure ASCII terminal UI with framed window boxes (`tui`).
- **Gesture Shortcuts**: Assign custom apps to Swipe Left and Swipe Right home gestures.
- **Interactive Clock & Calendar**: Tap time to open System Clock (optimized for Samsung Galaxy One UI & Stock Android) and date to open Calendar.
- **Real-time Package Monitor**: Broadcast receiver updates search query results immediately upon app install/uninstall.

---

## Command Cheatsheet

| Command / Input | Description | Example |
| :--- | :--- | :--- |
| **`<app_name>`** | Launch an installed application | `whatsapp` |
| **`ai <prompt>`** | Query configured AI provider (ChatGPT / Gemini) | `ai explain relativity in 2 sentences` |
| **`log <text>`** | Quick log entry to LazyLogs app | `log coffee 18000` |
| **`<scheme>://...`** | Direct execute any custom App Deep Link URI | `lazylogs://add?text=lunch%2025k` |
| **`spot <query>`** | Search music on Spotify app (or launch app) | `spot daft punk` |
| **`yt <query>`** | Search videos on YouTube app (or launch app) | `yt lofi beats` |
| **`play <query>`** | Search apps on Play Store (or launch app) | `play minecraft` |
| **`gh <query>`** | Search repositories on GitHub (or launch app) | `gh compose` |
| **`wiki <query>`** | Search articles on Wikipedia (or launch app) | `wiki quantum physics` |
| **`r <query>`** | Search posts on Reddit (or launch app) | `r androiddev` |
| **`x <query>`** | Search tweets on X / Twitter (or launch app) | `x android 15` |
| **`g <query>`** | Search web on Google Search | `g kotlin flow` |
| **`maps <loc>`** | Search location on Google Maps | `maps jakarta` |
| **`calc <expr>`** | Evaluate math expression | `calc 15*12` |
| **`timer <time>`** | Set timer alarm | `timer 20m` |
| **`info <app>`** | Open system Settings page for application | `info instagram` |
| **`hide <app>`** | Hide application from search results | `hide games` |
| **`unhide <app>`** | Restore hidden application to search | `unhide games` |
| **`hidden`** | View and manage all hidden applications | `hidden` |
| **`tui`** | Toggle retro TUI terminal view mode | `tui` |
| **`help` / `?`** | Display interactive command manual | `help` |
| **`settings`** | Open Kern Launcher settings screen | `settings` |

---

## Building & Installation

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or JDK 17+
- Android SDK 34+

### Build Debug APK
```bash
git clone https://github.com/USERNAME/kern-launcher.git
cd kern-launcher
./gradlew assembleDebug
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## Privacy & Permissions

Kern Launcher is designed with a privacy-first approach:
- **No Internet/Network Permissions Required**: The launcher itself operates entirely offline.
- **No Accessibility Services**: Kept minimal to avoid Play Protect warnings and preserve battery.
- **Minimal Permissions**: Uses `QUERY_ALL_PACKAGES` for app discovery and `SET_ALARM` for timers.

---

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
