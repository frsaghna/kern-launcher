package com.kern.launcher.command.parser

import com.kern.launcher.model.Command

object CommandParser {

    fun parse(input: String): Command {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return Command.Unknown("")

        // Check if raw input is a direct Deep Link URI (e.g., lazylogs://..., myapp://...)
        if (trimmed.contains("://")) {
            return Command.DeepLinkUri(trimmed, "Open Deep Link")
        }

        val tokenized = InputTokenizer.tokenize(trimmed)

        return when (tokenized.keyword) {
            "help", "?", "manual" -> Command.Help
            "hidden", "hiddenapps", "secret" -> Command.ShowHiddenApps
            "tui", "tuiview", "terminal" -> Command.ToggleTuiView
            "ai" -> Command.AiSearch(prompt = tokenized.args)
            "log", "lazylogs" -> Command.LazyLogsAdd(rawText = tokenized.args)
            "maps", "map" -> Command.GoogleMaps(tokenized.args)
            "yt" -> Command.YoutubeSearch(tokenized.args)
            "spot", "spotify", "music" -> Command.SpotifySearch(tokenized.args)
            "play", "store", "ps" -> Command.PlayStoreSearch(tokenized.args)
            "gh", "github" -> Command.GithubSearch(tokenized.args)
            "wiki", "wikipedia" -> Command.WikipediaSearch(tokenized.args)
            "reddit", "r" -> Command.RedditSearch(tokenized.args)
            "x", "tw", "twitter" -> Command.TwitterSearch(tokenized.args)
            "ddg", "duck" -> Command.DuckDuckGoSearch(tokenized.args)
            "g", "google" -> Command.GoogleSearch(tokenized.args)
            "timer" -> {
                val seconds = parseDurationSeconds(tokenized.args)
                Command.Timer(durationSeconds = seconds, rawInput = tokenized.args)
            }
            "settings", "config", "pref" -> Command.OpenSettings
            else -> Command.Unknown(tokenized.raw)
        }
    }

    private fun parseDurationSeconds(arg: String): Int {
        val clean = arg.lowercase().trim()
        if (clean.isEmpty()) return 60

        val numberPart = clean.filter { it.isDigit() }.toIntOrNull() ?: 1
        return when {
            clean.endsWith("h") || clean.endsWith("hr") || clean.endsWith("hours") -> numberPart * 3600
            clean.endsWith("m") || clean.endsWith("min") || clean.endsWith("minutes") -> numberPart * 60
            clean.endsWith("s") || clean.endsWith("sec") || clean.endsWith("seconds") -> numberPart
            else -> numberPart * 60
        }
    }
}
