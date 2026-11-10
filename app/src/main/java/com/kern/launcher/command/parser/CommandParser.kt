package com.kern.launcher.command.parser

import com.kern.launcher.model.Command

object CommandParser {

    fun parse(input: String): Command {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return Command.Unknown("")

        val tokenized = InputTokenizer.tokenize(trimmed)

        return when (tokenized.keyword) {
            "help", "?" -> Command.Help
            "hidden" -> Command.ShowHiddenApps
            "tui" -> Command.ToggleTuiView
            "ai" -> Command.AiSearch(prompt = tokenized.args)
            "dl" -> Command.MediaDownload(url = tokenized.args)
            "log" -> Command.LazyLogsAdd(rawText = tokenized.args)
            "map" -> Command.GoogleMaps(tokenized.args)
            "yt" -> Command.YoutubeSearch(tokenized.args)
            "spot" -> Command.SpotifySearch(tokenized.args)
            "ps" -> Command.PlayStoreSearch(tokenized.args)
            "gh" -> Command.GithubSearch(tokenized.args)
            "wiki" -> Command.WikipediaSearch(tokenized.args)
            "r" -> Command.RedditSearch(tokenized.args)
            "x" -> Command.TwitterSearch(tokenized.args)
            "g" -> Command.GoogleSearch(tokenized.args)
            "timer" -> {
                val seconds = parseDurationSeconds(tokenized.args)
                Command.Timer(durationSeconds = seconds, rawInput = tokenized.args)
            }
            "pref", "conf" -> Command.OpenSettings
            else -> {
                if (trimmed.contains("://")) {
                    Command.DeepLinkUri(trimmed, "Open Deep Link")
                } else {
                    Command.Unknown(tokenized.raw)
                }
            }
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
