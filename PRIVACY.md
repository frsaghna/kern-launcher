# Privacy Policy for Kern Launcher

**DRAFT: requires human review**

*Last updated: 2026-10-14*

Kern Launcher ("Kern", "we", "the app") is developed as an open-source, offline-first, keyboard-driven Android launcher. This document describes the categories of data processed by Kern Launcher, how that data is stored, and your rights and controls as a user.

---

## 1. Core Principle: Zero Telemetry & On-Device Processing

Kern Launcher does not collect, transmit, sell, or monetize any personal data.
- **No Analytics**: We do not include third-party tracking, crash reporting services (such as Firebase Crashlytics), advertising SDKs, or user telemetry.
- **No Account Required**: The app functions without user accounts, sign-ins, or cloud synchronization.
- **No Network Requests by Kern**: Kern Launcher does not request `android.permission.INTERNET` in its manifest. The application binary itself makes zero direct HTTP, HTTPS, or socket connections to any server.

---

## 2. Data Stored Locally on Your Device

All user data is stored exclusively on your device's private application storage:

| Data Type | Storage Mechanism | Purpose |
| :--- | :--- | :--- |
| **Command History** | Room SQLite Database (`AppDatabase`) | Enables navigating previously entered commands via Up/Down arrow keys. |
| **Custom Aliases** | Room SQLite Database (`AppDatabase`) | Maps user-defined short commands to application package names or search strings. |
| **Hidden Applications** | Room SQLite Database (`AppDatabase`) | Preserves the list of apps you have chosen to hide from launcher search results. |
| **Application Usage Counts** | Room SQLite Database (`AppDatabase`) | Records launch frequency to rank frequently used apps higher in search results. |
| **User Settings & Themes** | Jetpack DataStore (`user_preferences.preferences_pb`) | Stores your visual preferences (theme colors, font choice, alignments, gesture assignments, clock visibility). |

---

## 3. External Intent & Companion Interactions

Kern Launcher interacts with external apps strictly through standard Android `Intent` routing:
- **Search & Deep Links**: When you execute commands like `g <query>`, `yt <query>`, `spot <query>`, or `ai <prompt>`, Kern passes your query to your device's installed web browser or relevant application via an Android `ACTION_VIEW` intent.
- **yt-dlp Downloader Companion (`down` / `dl`)**:
  - The companion dialog allows you to route media URLs to user-installed third-party downloader apps (YTDLnis, NewPipe) via `ACTION_SEND` intents, or to open public web services (such as Cobalt) in your browser.
  - Kern Launcher does not download, parse, store, or execute media files or remote binaries.
  - Clipboard access is triggered only when you tap the companion dialog or click paste/copy buttons.

---

## 4. Permissions Used

- **`android.permission.QUERY_ALL_PACKAGES`**: Required by Android launcher applications to query installed packages and present them in search results.
- **`com.android.alarm.permission.SET_ALARM`**: Required to set countdown timers and alarms when using the `timer <duration>` command.

Neither permission allows transmitting data off the device.

---

## 5. How to Delete or Reset Your Data

Because all data resides exclusively in your local device storage, you retain total control over its retention:

1. **Clear Command History**: In Kern Launcher, use the `settings` menu to clear history.
2. **Clear All Data**:
   - Open Android **Settings** → **Apps** → **Kern**.
   - Tap **Storage & cache**.
   - Select **Clear storage** (or **Clear data**).
   - This immediately deletes the Room database and DataStore preferences from your device.
3. **Uninstalling the App**: Uninstalling Kern Launcher completely and irreversibly removes all local databases, preferences, and cached files from your device.

---

## 6. Open Source Verification

Kern Launcher is licensed under the GNU General Public License v3.0. You can inspect the entire source code, build process, and dependencies directly in the project repository.

---

## 7. Contact & Review

This privacy policy is a draft prepared for release review. If you have questions or require modifications prior to store publication, please open an issue in the project repository.
