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
