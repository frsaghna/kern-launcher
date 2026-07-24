package com.kern.launcher.search.engine

import android.provider.Settings
import com.kern.launcher.command.parser.CommandParser
import com.kern.launcher.command.parser.InputTokenizer
import com.kern.launcher.model.Alias
import com.kern.launcher.model.AppInfo
import com.kern.launcher.model.Command
import com.kern.launcher.model.SearchResult
import com.kern.launcher.model.SearchResultType
import com.kern.launcher.search.ranking.SearchRanker

private data class SystemSettingOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val action: String,
    val keywords: List<String>
)

private val SYSTEM_SETTINGS_OPTIONS = listOf(
    SystemSettingOption("set_wellbeing", "Digital Wellbeing & Screen Time", "App timers, screen time dashboard & parental controls", "com.google.android.apps.wellbeing.action.WELLBEING_DASHBOARD", listOf("wellbeing", "digitalwellbeing", "screentime", "screen time", "app timer", "parental", "usage")),
    SystemSettingOption("set_dns", "Private DNS Settings", "Configure Private DNS provider & mode", "android.settings.DNS_SETTINGS", listOf("dns", "privatedns", "private dns", "adguard", "cloudflare")),
    SystemSettingOption("set_vpn", "VPN Settings", "Configure Virtual Private Network profiles", Settings.ACTION_VPN_SETTINGS, listOf("vpn", "proxy")),
    SystemSettingOption("set_wifi", "Wi-Fi Settings", "Manage Wi-Fi networks & connections", Settings.ACTION_WIFI_SETTINGS, listOf("wifi", "wlan", "internet")),
    SystemSettingOption("set_bluetooth", "Bluetooth Settings", "Pair & manage Bluetooth devices", Settings.ACTION_BLUETOOTH_SETTINGS, listOf("bluetooth", "bt")),
    SystemSettingOption("set_display", "Display & Brightness", "Adjust screen brightness, font, & dark mode", Settings.ACTION_DISPLAY_SETTINGS, listOf("display", "screen", "brightness", "font")),
    SystemSettingOption("set_sound", "Sound & Vibration", "Volume, ringtones, & silent mode", Settings.ACTION_SOUND_SETTINGS, listOf("sound", "volume", "audio", "vibration", "ringtone")),
    SystemSettingOption("set_dnd", "Do Not Disturb (DND)", "Configure DND schedules & priority notifications", Settings.ACTION_ZEN_MODE_PRIORITY_SETTINGS, listOf("dnd", "do not disturb", "silent")),
    SystemSettingOption("set_battery", "Battery & Power", "Battery usage, battery saver & charging", Settings.ACTION_BATTERY_SAVER_SETTINGS, listOf("battery", "power", "saver", "charge")),
    SystemSettingOption("set_storage", "Storage Settings", "Manage internal storage & SD card", Settings.ACTION_INTERNAL_STORAGE_SETTINGS, listOf("storage", "memory", "sdcard")),
    SystemSettingOption("set_location", "Location & GPS", "Location permissions & GPS mode", Settings.ACTION_LOCATION_SOURCE_SETTINGS, listOf("location", "gps")),
    SystemSettingOption("set_apps", "Applications Manager", "Installed apps & app permissions", Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS, listOf("apps", "applications", "manager")),
    SystemSettingOption("set_default_apps", "Default Applications", "Choose default browser, launcher & assistant", Settings.ACTION_HOME_SETTINGS, listOf("default", "defaultapps", "browser", "home")),
    SystemSettingOption("set_notifications", "Notifications Settings", "Notification history, banners & lockscreen notifs", Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS, listOf("notif", "notification", "banner")),
    SystemSettingOption("set_nfc", "NFC & Contactless", "NFC & contactless payments settings", Settings.ACTION_NFC_SETTINGS, listOf("nfc", "pay", "contactless")),
    SystemSettingOption("set_hotspot", "Hotspot & Tethering", "Mobile hotspot, USB tethering & network", Settings.ACTION_WIRELESS_SETTINGS, listOf("hotspot", "tethering", "wireless", "network", "cellular")),
    SystemSettingOption("set_developer", "Developer Options", "USB debugging, OEM unlock & advanced tools", Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS, listOf("developer", "dev", "debugging", "usb")),
    SystemSettingOption("set_security", "Security & Privacy", "Screen lock, fingerprint, & privacy permissions", Settings.ACTION_SECURITY_SETTINGS, listOf("security", "privacy", "lockscreen", "fingerprint")),
    SystemSettingOption("set_accounts", "Accounts & Sync", "Manage Google accounts & data auto-sync", Settings.ACTION_SYNC_SETTINGS, listOf("accounts", "sync", "google")),
    SystemSettingOption("set_date", "Date & Time", "Timezone & automatic clock sync", Settings.ACTION_DATE_SETTINGS, listOf("date", "time", "clock")),
    SystemSettingOption("set_language", "Language & Input", "System languages & physical/virtual keyboards", Settings.ACTION_LOCALE_SETTINGS, listOf("language", "locale", "keyboard", "input")),
    SystemSettingOption("set_accessibility", "Accessibility", "Screen readers, magnification & accessibility", Settings.ACTION_ACCESSIBILITY_SETTINGS, listOf("accessibility", "vision", "talkback")),
    SystemSettingOption("set_airplane", "Airplane Mode", "Network flight mode toggle", Settings.ACTION_AIRPLANE_MODE_SETTINGS, listOf("airplane", "flight", "mode")),
    SystemSettingOption("set_about", "About Phone", "Device info, Android version & build number", Settings.ACTION_DEVICE_INFO_SETTINGS, listOf("about", "phone", "device", "version", "system info")),
    SystemSettingOption("set_cast", "Cast & Screen Mirroring", "Wireless display, Smart View & screen cast", Settings.ACTION_CAST_SETTINGS, listOf("cast", "mirror", "screen mirror", "smartview")),
    SystemSettingOption("set_main", "Main System Settings", "Open full phone Settings app", Settings.ACTION_SETTINGS, listOf("main", "all", "system", "settings"))
)

class SearchEngine {

    fun search(
        query: String,
        apps: List<AppInfo>,
        aliases: List<Alias>,
        hiddenPackageNames: Set<String> = emptySet(),
        aiProvider: String = "CHATGPT"
    ): List<SearchResult> {
        val trimmed = query.trim()
        val results = mutableListOf<SearchResult>()
        val aliasMap = aliases.associateBy { it.alias.lowercase() }

        // Filter out hidden apps for regular app launcher list
        val visibleApps = apps.filter { it.packageName !in hiddenPackageNames }
        val hiddenApps = apps.filter { it.packageName in hiddenPackageNames }

        if (trimmed.isEmpty()) {
            val topApps = visibleApps.map { app ->
                val score = SearchRanker.calculateScore(app, "")
                SearchResult(
                    id = "app_${app.packageName}",
                    type = SearchResultType.APP,
                    title = app.label,
                    subtitle = "",
                    iconBitmap = app.iconBitmap,
                    actionCommand = Command.AppLaunch(app),
                    score = score
                )
            }.sortedByDescending { it.score }
            return topApps
        }

        // Direct Scheme URI check (e.g. lazylogs://add?text=makan%20siang%2025k)
        if (trimmed.contains("://")) {
            results.add(
                SearchResult(
                    id = "deeplink_uri",
                    type = SearchResultType.BUILTIN_COMMAND,
                    title = "Execute Deep Link: $trimmed",
                    subtitle = "Open custom app URI scheme",
                    actionCommand = Command.DeepLinkUri(trimmed, "Deep Link"),
                    score = 3000.0
                )
            )
            return results
        }

        val tokenized = InputTokenizer.tokenize(trimmed)
        val keyword = tokenized.keyword.lowercase()

        // 1. Built-in Kern Settings Command Check (Exact keyword match)
        val settingsKeywords = listOf("settings", "setting", "config", "pref", "kern")
        if (keyword in settingsKeywords) {
            results.add(
                SearchResult(
                    id = "builtin_settings",
                    type = SearchResultType.BUILTIN_COMMAND,
                    title = "Kern Launcher Settings",
                    subtitle = "Open theme, alias, font & preferences config",
                    actionCommand = Command.OpenSettings,
                    score = 4000.0
                )
            )
        }

        // 2. System Settings Index Command 'set' (exact keyword 'set')
        if (keyword == "set") {
            val subQuery = tokenized.args.trim().lowercase()
            val filteredOptions = if (subQuery.isBlank()) {
                SYSTEM_SETTINGS_OPTIONS
            } else {
                SYSTEM_SETTINGS_OPTIONS.filter { option ->
                    option.title.lowercase().contains(subQuery) ||
                            option.subtitle.lowercase().contains(subQuery) ||
                            option.keywords.any { it.contains(subQuery) }
                }
            }

            var initialScore = if (subQuery.isNotBlank()) 4500.0 else 3800.0
            filteredOptions.forEach { opt ->
                results.add(
                    SearchResult(
                        id = opt.id,
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "System Setting: ${opt.title}",
                        subtitle = opt.subtitle,
                        actionCommand = Command.SystemSettingsPage(opt.action, opt.title),
                        score = initialScore
                    )
                )
                initialScore -= 1.0
            }
        }

        // 3. Help Command Check
        val helpKeywords = listOf("help", "?", "manual")
        if (keyword in helpKeywords) {
            results.add(
                SearchResult(
                    id = "builtin_help",
                    type = SearchResultType.BUILTIN_COMMAND,
                    title = "Kern Command Manual (Help)",
                    subtitle = "Usage manual, command syntax & aliases",
                    actionCommand = Command.Help,
                    score = 2500.0
                )
            )
        }

        // 4. Hidden Apps Command Check
        val hiddenKeywords = listOf("hidden", "hiddenapps", "secret")
        if (keyword in hiddenKeywords) {
            results.add(
                SearchResult(
                    id = "builtin_hidden",
                    type = SearchResultType.BUILTIN_COMMAND,
                    title = "Hidden Applications (${hiddenApps.size} Hidden)",
                    subtitle = "View and unhide hidden applications",
                    actionCommand = Command.ShowHiddenApps,
                    score = 2500.0
                )
            )
        }

        // 5. TUI View Mode Toggle Command Check
        val tuiKeywords = listOf("tui", "tuiview", "terminal")
        if (keyword in tuiKeywords) {
            results.add(
                SearchResult(
                    id = "builtin_tui",
                    type = SearchResultType.BUILTIN_COMMAND,
                    title = "Toggle TUI View Mode",
                    subtitle = "Switch between Standard UI & Retro Terminal UI",
                    actionCommand = Command.ToggleTuiView,
                    score = 2500.0
                )
            )
        }

        // 6. Info Command (App Info Settings)
        if (keyword == "info") {
            val targetArg = tokenized.args
            val matchingApps = if (targetArg.isBlank()) visibleApps else visibleApps.filter { app ->
                SearchRanker.calculateScore(app, targetArg) > 0
            }
            matchingApps.take(5).forEach { app ->
                results.add(
                    SearchResult(
                        id = "info_${app.packageName}",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "App Info: ${app.label}",
                        subtitle = "Open system settings for ${app.label}",
                        iconBitmap = app.iconBitmap,
                        actionCommand = Command.AppInfoSettings(app.packageName, app.label),
                        score = 2300.0
                    )
                )
            }
        }

        // 7. Hide App Command
        if (keyword == "hide") {
            val targetArg = tokenized.args
            val appsToHide = if (targetArg.isBlank()) visibleApps else visibleApps.filter { app ->
                SearchRanker.calculateScore(app, targetArg) > 0
            }
            appsToHide.take(5).forEach { app ->
                results.add(
                    SearchResult(
                        id = "hide_${app.packageName}",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "Hide App: ${app.label}",
                        subtitle = "Hide application from search",
                        iconBitmap = app.iconBitmap,
                        actionCommand = Command.HideApp(app),
                        score = 2200.0
                    )
                )
            }
        }

        // 8. Unhide App Command
        if (keyword == "unhide") {
            val targetArg = tokenized.args
            val appsToUnhide = if (targetArg.isBlank()) hiddenApps else hiddenApps.filter { app ->
                app.label.contains(targetArg, ignoreCase = true)
            }
            appsToUnhide.forEach { app ->
                results.add(
                    SearchResult(
                        id = "unhide_${app.packageName}",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "Unhide App: ${app.label}",
                        subtitle = "Make application visible in search again",
                        iconBitmap = app.iconBitmap,
                        actionCommand = Command.UnhideApp(app.packageName, app.label),
                        score = 2200.0
                    )
                )
            }
        }

        // 9. Alias Check
        aliasMap[keyword]?.let { userAlias ->
            val resolvedCommand = CommandParser.parse("${userAlias.targetCommandOrPackage} ${tokenized.args}".trim())
            results.add(
                SearchResult(
                    id = "alias_${userAlias.alias}",
                    type = SearchResultType.ALIAS,
                    title = "Alias: ${userAlias.alias} ➔ ${userAlias.targetCommandOrPackage}",
                    subtitle = "Execute user custom alias",
                    actionCommand = Command.CustomAlias(userAlias.alias, resolvedCommand),
                    score = 2000.0
                )
            )
        }

        // 10. Built-in Deep Search & Custom App Commands (STRICT EXACT MATCHES ONLY)
        when (keyword) {
            "ai" -> {
                val providerName = when (aiProvider.uppercase()) {
                    "GEMINI" -> "Google Gemini"
                    "PERPLEXITY" -> "Perplexity AI"
                    "CLAUDE" -> "Claude AI"
                    else -> "ChatGPT"
                }
                results.add(
                    SearchResult(
                        id = "builtin_ai",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "AI Prompt ($providerName)",
                        subtitle = if (tokenized.args.isBlank()) "Launch $providerName app (Add query to search)" else "Ask $providerName '${tokenized.args}'",
                        actionCommand = Command.AiSearch(prompt = tokenized.args, provider = aiProvider),
                        score = 1600.0
                    )
                )
            }
            "log", "lazylogs" -> {
                val parsedLazyLogs = CommandParser.parse(trimmed) as? Command.LazyLogsAdd
                if (parsedLazyLogs != null) {
                    results.add(
                        SearchResult(
                            id = "builtin_lazylogs",
                            type = SearchResultType.BUILTIN_COMMAND,
                            title = if (parsedLazyLogs.rawText.isBlank()) "LazyLogs" else "LazyLogs Add: '${parsedLazyLogs.rawText}'",
                            subtitle = if (parsedLazyLogs.rawText.isBlank()) "Launch LazyLogs app" else "lazylogs://add?text=${parsedLazyLogs.rawText}",
                            actionCommand = parsedLazyLogs,
                            score = 1600.0
                        )
                    )
                }
            }
            "maps", "map" -> {
                results.add(
                    SearchResult(
                        id = "builtin_maps",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "Google Maps",
                        subtitle = if (tokenized.args.isBlank()) "Launch Google Maps app" else "Search '${tokenized.args}' on Google Maps",
                        actionCommand = Command.GoogleMaps(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "yt" -> {
                results.add(
                    SearchResult(
                        id = "builtin_yt",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "YouTube",
                        subtitle = if (tokenized.args.isBlank()) "Launch YouTube app" else "Search '${tokenized.args}' on YouTube",
                        actionCommand = Command.YoutubeSearch(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "spot", "spotify", "music" -> {
                results.add(
                    SearchResult(
                        id = "builtin_spotify",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "Spotify",
                        subtitle = if (tokenized.args.isBlank()) "Launch Spotify app" else "Search '${tokenized.args}' on Spotify",
                        actionCommand = Command.SpotifySearch(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "play", "store", "ps" -> {
                results.add(
                    SearchResult(
                        id = "builtin_playstore",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "Play Store",
                        subtitle = if (tokenized.args.isBlank()) "Launch Play Store app" else "Search '${tokenized.args}' on Play Store",
                        actionCommand = Command.PlayStoreSearch(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "gh", "github" -> {
                results.add(
                    SearchResult(
                        id = "builtin_github",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "GitHub",
                        subtitle = if (tokenized.args.isBlank()) "Launch GitHub app" else "Search '${tokenized.args}' on GitHub",
                        actionCommand = Command.GithubSearch(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "wiki", "wikipedia" -> {
                results.add(
                    SearchResult(
                        id = "builtin_wiki",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "Wikipedia",
                        subtitle = if (tokenized.args.isBlank()) "Launch Wikipedia app" else "Search '${tokenized.args}' on Wikipedia",
                        actionCommand = Command.WikipediaSearch(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "reddit", "r" -> {
                results.add(
                    SearchResult(
                        id = "builtin_reddit",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "Reddit",
                        subtitle = if (tokenized.args.isBlank()) "Launch Reddit app" else "Search '${tokenized.args}' on Reddit",
                        actionCommand = Command.RedditSearch(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "x", "tw", "twitter" -> {
                results.add(
                    SearchResult(
                        id = "builtin_twitter",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "X / Twitter",
                        subtitle = if (tokenized.args.isBlank()) "Launch X / Twitter app" else "Search '${tokenized.args}' on X / Twitter",
                        actionCommand = Command.TwitterSearch(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "ddg", "duck" -> {
                results.add(
                    SearchResult(
                        id = "builtin_ddg",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "DuckDuckGo",
                        subtitle = if (tokenized.args.isBlank()) "Launch DuckDuckGo app" else "Search '${tokenized.args}' on DuckDuckGo",
                        actionCommand = Command.DuckDuckGoSearch(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "g", "google" -> {
                results.add(
                    SearchResult(
                        id = "builtin_google",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "Google Search",
                        subtitle = if (tokenized.args.isBlank()) "Launch Google app" else "Search '${tokenized.args}' on Google",
                        actionCommand = Command.GoogleSearch(tokenized.args),
                        score = 1500.0
                    )
                )
            }
            "timer" -> {
                results.add(
                    SearchResult(
                        id = "builtin_timer",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "Timer",
                        subtitle = if (tokenized.args.isBlank()) "Start 1m timer" else "Set timer '${tokenized.args}'",
                        actionCommand = CommandParser.parse(trimmed),
                        score = 1500.0
                    )
                )
            }
        }

        // 11. Visible App Search Matches
        val matchingApps = visibleApps.mapNotNull { app ->
            val score = SearchRanker.calculateScore(app, trimmed)
            if (score > 0) {
                SearchResult(
                    id = "app_${app.packageName}",
                    type = SearchResultType.APP,
                    title = app.label,
                    subtitle = "",
                    iconBitmap = app.iconBitmap,
                    actionCommand = Command.AppLaunch(app),
                    score = score
                )
            } else null
        }
        results.addAll(matchingApps)

        // 12. Fallback Search Option
        if (results.none { it.type == SearchResultType.BUILTIN_COMMAND && (it.id == "builtin_google" || it.id == "builtin_settings" || it.id.startsWith("set_") || it.id == "builtin_help" || it.id == "builtin_tui" || it.id.startsWith("hide_") || it.id.startsWith("unhide_") || it.id.startsWith("info_") || it.id == "builtin_lazylogs" || it.id == "builtin_ai") }) {
            results.add(
                SearchResult(
                    id = "fallback_search",
                    type = SearchResultType.BUILTIN_COMMAND,
                    title = "Search Web: '$trimmed'",
                    subtitle = "Search with Google",
                    actionCommand = Command.GoogleSearch(trimmed),
                    score = 10.0
                )
            )
        }

        return results.sortedByDescending { it.score }
    }
}
