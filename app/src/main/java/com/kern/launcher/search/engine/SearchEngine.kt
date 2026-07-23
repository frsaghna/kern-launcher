package com.kern.launcher.search.engine

import com.kern.launcher.command.builtin.CalculatorEvaluator
import com.kern.launcher.command.parser.CommandParser
import com.kern.launcher.command.parser.InputTokenizer
import com.kern.launcher.model.Alias
import com.kern.launcher.model.AppInfo
import com.kern.launcher.model.Command
import com.kern.launcher.model.SearchResult
import com.kern.launcher.model.SearchResultType
import com.kern.launcher.search.ranking.SearchRanker

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
                    icon = app.icon,
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

        // 1. Built-in Kern Settings Command Check (Prefix matching so typing s, set, setting, settings, config always shows Kern Settings #1)
        val settingsKeywords = listOf("settings", "setting", "config", "pref", "kern")
        if (settingsKeywords.any { it.startsWith(keyword) || keyword.startsWith(it) }) {
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

        // 2. Help Command Check
        val helpKeywords = listOf("help", "?", "manual")
        if (helpKeywords.any { it.startsWith(keyword) || keyword.startsWith(it) }) {
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

        // 3. Hidden Apps Command Check
        val hiddenKeywords = listOf("hidden", "hiddenapps", "secret")
        if (hiddenKeywords.any { it.startsWith(keyword) || keyword.startsWith(it) }) {
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

        // 4. TUI View Mode Toggle Command Check
        val tuiKeywords = listOf("tui", "tuiview", "terminal")
        if (tuiKeywords.any { it.startsWith(keyword) || keyword.startsWith(it) }) {
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

        // 5. Info Command (App Info Settings)
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
                        icon = app.icon,
                        actionCommand = Command.AppInfoSettings(app.packageName, app.label),
                        score = 2300.0
                    )
                )
            }
        }

        // 6. Hide App Command
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
                        icon = app.icon,
                        actionCommand = Command.HideApp(app),
                        score = 2200.0
                    )
                )
            }
        }

        // 7. Unhide App Command
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
                        icon = app.icon,
                        actionCommand = Command.UnhideApp(app.packageName, app.label),
                        score = 2200.0
                    )
                )
            }
        }

        // 8. Alias Check
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

        // 9. Calculator check
        val mathResult = CalculatorEvaluator.evaluate(trimmed)
            ?: (if (keyword == "calc") CalculatorEvaluator.evaluate(tokenized.args) else null)

        if (mathResult != null) {
            results.add(
                SearchResult(
                    id = "calc_$trimmed",
                    type = SearchResultType.CALCULATOR_RESULT,
                    title = "= $mathResult",
                    subtitle = "Calculator ($trimmed) — press Enter to copy",
                    actionCommand = Command.Calculator(trimmed, mathResult),
                    score = 1800.0
                )
            )
        }

        // 10. Built-in Deep Search & Custom App Commands
        when {
            listOf("ai", "gpt", "chatgpt", "gemini").any { it == keyword || it.startsWith(keyword) } -> {
                val providerName = when (keyword) {
                    "gpt", "chatgpt" -> "ChatGPT"
                    "gemini" -> "Google Gemini"
                    else -> when (aiProvider.uppercase()) {
                        "GEMINI" -> "Google Gemini"
                        "PERPLEXITY" -> "Perplexity AI"
                        "CLAUDE" -> "Claude AI"
                        else -> "ChatGPT"
                    }
                }
                val selectedProviderCode = when (keyword) {
                    "gpt", "chatgpt" -> "CHATGPT"
                    "gemini" -> "GEMINI"
                    else -> aiProvider
                }
                results.add(
                    SearchResult(
                        id = "builtin_ai",
                        type = SearchResultType.BUILTIN_COMMAND,
                        title = "AI Prompt ($providerName)",
                        subtitle = if (tokenized.args.isBlank()) "Launch $providerName app (Add query to search)" else "Ask $providerName '${tokenized.args}'",
                        actionCommand = Command.AiSearch(prompt = tokenized.args, provider = selectedProviderCode),
                        score = 1600.0
                    )
                )
            }
            listOf("log", "lazylogs").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("maps", "map").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("yt", "youtube").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("spot", "spotify", "music").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("play", "store", "ps").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("gh", "github").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("wiki", "wikipedia").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("reddit", "r").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("x", "tw", "twitter").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("ddg", "duck").any { it == keyword || it.startsWith(keyword) } -> {
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
            listOf("g", "google").any { it == keyword || it.startsWith(keyword) } -> {
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
            keyword == "timer" -> {
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
                    icon = app.icon,
                    actionCommand = Command.AppLaunch(app),
                    score = score
                )
            } else null
        }
        results.addAll(matchingApps)

        // 12. Fallback Search Option
        if (results.none { it.type == SearchResultType.BUILTIN_COMMAND && (it.id == "builtin_google" || it.id == "builtin_settings" || it.id == "builtin_help" || it.id == "builtin_tui" || it.id.startsWith("hide_") || it.id.startsWith("unhide_") || it.id.startsWith("info_") || it.id == "builtin_lazylogs" || it.id == "builtin_ai") }) {
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
