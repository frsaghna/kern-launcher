package com.kern.launcher.service.intent

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import com.kern.launcher.model.AppInfo

object IntentFactory {

    private fun launchAppOrWeb(context: Context, packageNames: List<String>, webUrl: String): Intent {
        for (pkg in packageNames) {
            val intent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return intent
            }
        }
        return Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createAppLaunchIntent(context: Context, app: AppInfo): Intent? {
        return context.packageManager.getLaunchIntentForPackage(app.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createSystemSettingsPageIntent(context: Context, actionKey: String): Intent {
        val candidates: List<Intent> = when (actionKey) {
            "set_dns" -> listOf(
                Intent().apply { setClassName("com.android.settings", "com.android.settings.Settings\$MoreUrlSettingsActivity") },
                Intent().apply { setClassName("com.android.settings", "com.samsung.android.settings.connections.MoreConnectionsSettings") },
                Intent().apply { setClassName("com.android.settings", "com.samsung.android.settings.moreconnection.MoreConnectionSettingsActivity") },
                Intent("android.settings.SETTINGS").apply { putExtra(":settings:show_fragment", "com.samsung.android.settings.connections.PrivateDnsSettings") },
                Intent("android.settings.DNS_SETTINGS"),
                Intent(Settings.ACTION_WIRELESS_SETTINGS)
            )
            "set_battery" -> listOf(
                Intent(Intent.ACTION_POWER_USAGE_SUMMARY),
                Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            )
            "set_hotspot" -> listOf(
                Intent("android.settings.TETHER_SETTINGS"),
                Intent(Settings.ACTION_WIRELESS_SETTINGS)
            )
            "set_default_apps" -> listOf(
                Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"),
                Intent(Settings.ACTION_HOME_SETTINGS)
            )
            "set_notifications" -> listOf(
                Intent("android.settings.ALL_APPS_NOTIFICATION_SETTINGS"),
                Intent("android.settings.APP_NOTIFICATION_SETTINGS"),
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            )
            "set_vpn" -> listOf(
                Intent(Settings.ACTION_VPN_SETTINGS),
                Intent("android.settings.VPN_SETTINGS"),
                Intent(Settings.ACTION_WIRELESS_SETTINGS)
            )
            "set_cast" -> listOf(
                Intent("android.settings.CAST_SETTINGS"),
                Intent(Settings.ACTION_CAST_SETTINGS),
                Intent(Settings.ACTION_DISPLAY_SETTINGS)
            )
            "set_wellbeing" -> {
                val list = mutableListOf<Intent>()
                // Samsung One UI explicit components
                list.add(Intent().apply { setClassName("com.samsung.android.forest", "com.samsung.android.forest.main.HomeActivity") })
                list.add(Intent().apply { setClassName("com.android.settings", "com.samsung.android.settings.wellbeing.WellbeingSettingActivity") })
                list.add(Intent("com.samsung.android.forest.MAIN"))
                list.add(Intent("com.samsung.android.forest.action.MAIN"))
                // Google / Stock Android standard dashboard action
                list.add(Intent("com.google.android.apps.wellbeing.action.WELLBEING_DASHBOARD"))
                // Package launch intents
                listOf("com.samsung.android.forest", "com.samsung.android.wellbeing", "com.google.android.apps.wellbeing").forEach { pkg ->
                    context.packageManager.getLaunchIntentForPackage(pkg)?.let { list.add(it) }
                }
                // Usage access fallback
                list.add(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                list
            }
            "set_accounts" -> listOf(
                Intent(Settings.ACTION_SYNC_SETTINGS),
                Intent("android.settings.ACCOUNT_SYNC_SETTINGS")
            )
            "set_wifi" -> listOf(Intent(Settings.ACTION_WIFI_SETTINGS))
            "set_bluetooth" -> listOf(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            "set_display" -> listOf(Intent(Settings.ACTION_DISPLAY_SETTINGS))
            "set_sound" -> listOf(Intent(Settings.ACTION_SOUND_SETTINGS))
            "set_dnd" -> listOf(Intent(Settings.ACTION_ZEN_MODE_PRIORITY_SETTINGS), Intent(Settings.ACTION_SOUND_SETTINGS))
            "set_storage" -> listOf(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
            "set_location" -> listOf(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            "set_apps" -> listOf(Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS))
            "set_nfc" -> listOf(Intent(Settings.ACTION_NFC_SETTINGS))
            "set_developer" -> listOf(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
            "set_security" -> listOf(Intent(Settings.ACTION_SECURITY_SETTINGS))
            "set_date" -> listOf(Intent(Settings.ACTION_DATE_SETTINGS))
            "set_language" -> listOf(Intent(Settings.ACTION_LOCALE_SETTINGS), Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            "set_accessibility" -> listOf(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            "set_airplane" -> listOf(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
            "set_about" -> listOf(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS))
            else -> listOf(Intent(actionKey))
        }

        // Try candidate intents with resolveActivity check
        for (candidate in candidates) {
            candidate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (candidate.resolveActivity(context.packageManager) != null) {
                return candidate
            }
        }

        // Ultimate fallback to main System Settings
        return Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createGoogleSearchIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.google.android.googlequicksearchbox"), "https://www.google.com")
        }
    }

    fun createAiSearchIntent(context: Context, prompt: String, provider: String = "CHATGPT"): Intent {
        val (packages, searchUrl, homeUrl) = when (provider.uppercase()) {
            "GEMINI" -> Triple(listOf("com.google.android.apps.bard"), "https://gemini.google.com/prompt?q=${Uri.encode(prompt)}", "https://gemini.google.com")
            "PERPLEXITY" -> Triple(listOf("ai.perplexity.app"), "https://www.perplexity.ai/search?q=${Uri.encode(prompt)}", "https://www.perplexity.ai")
            "CLAUDE" -> Triple(listOf("com.anthropic.claude"), "https://claude.ai/new?q=${Uri.encode(prompt)}", "https://claude.ai")
            else -> Triple(listOf("com.openai.chatgpt"), "https://chatgpt.com/?q=${Uri.encode(prompt)}", "https://chatgpt.com")
        }

        return if (prompt.isNotBlank()) {
            Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, packages, homeUrl)
        }
    }

    fun createDeepLinkUriIntent(uriString: String): Intent {
        val uri = Uri.parse(uriString)
        return Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createLazyLogsIntent(context: Context, rawText: String): Intent {
        return if (rawText.isNotBlank()) {
            val uriString = "lazylogs://add?text=${Uri.encode(rawText)}"
            Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.lazylogs.app", "com.example.lazylogs"), "lazylogs://home")
        }
    }

    fun createGoogleMapsIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.google.android.apps.maps"), "https://maps.google.com")
        }
    }

    fun createYoutubeSearchIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.google.android.youtube"), "https://www.youtube.com")
        }
    }

    fun createSpotifySearchIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("https://open.spotify.com/search/${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.spotify.music", "com.spotify.tv.android"), "https://open.spotify.com")
        }
    }

    fun createPlayStoreSearchIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("market://search?q=${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.android.vending"), "https://play.google.com")
        }
    }

    fun createGithubSearchIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("https://github.com/search?q=${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.github.android"), "https://github.com")
        }
    }

    fun createWikipediaSearchIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("https://en.wikipedia.org/wiki/Special:Search?search=${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("org.wikipedia"), "https://en.wikipedia.org")
        }
    }

    fun createRedditSearchIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("https://www.reddit.com/search/?q=${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.reddit.frontpage"), "https://www.reddit.com")
        }
    }

    fun createTwitterSearchIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("https://twitter.com/search?q=${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.twitter.android", "com.twitter.android.lite"), "https://x.com")
        }
    }

    fun createDuckDuckGoSearchIntent(context: Context, query: String): Intent {
        return if (query.isNotBlank()) {
            val uri = Uri.parse("https://duckduckgo.com/?q=${Uri.encode(query)}")
            Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchAppOrWeb(context, listOf("com.duckduckgo.mobile.android"), "https://duckduckgo.com")
        }
    }

    fun createTimerIntent(durationSeconds: Int, message: String): Intent {
        return Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, durationSeconds)
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createAppInfoIntent(packageName: String): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun openClock(context: Context) {
        val clockPackages = listOf(
            "com.sec.android.app.clockpackage",
            "com.google.android.deskclock",
            "com.android.deskclock",
            "com.coloros.alarmclock",
            "com.oplus.alarmclock",
            "com.miui.clock",
            "com.asus.deskclock",
            "com.lenovo.deskclock"
        )

        for (pkg in clockPackages) {
            val intent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            }
        }

        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_TIMERS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openCalendar(context: Context) {
        try {
            val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
            ContentUris.appendId(builder, System.currentTimeMillis())
            val intent = Intent(Intent.ACTION_VIEW, builder.build()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_CALENDAR)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }
}
