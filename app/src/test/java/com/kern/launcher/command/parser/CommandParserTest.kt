package com.kern.launcher.command.parser

import com.kern.launcher.model.Command
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandParserTest {

    @Test
    fun testBuiltInCommandsWithValidArguments() {
        assertTrue(CommandParser.parse("help") is Command.Help)
        assertTrue(CommandParser.parse("?") is Command.Help)
        assertTrue(CommandParser.parse("manual") is Command.Help)

        assertTrue(CommandParser.parse("hidden") is Command.ShowHiddenApps)
        assertTrue(CommandParser.parse("hiddenapps") is Command.ShowHiddenApps)
        assertTrue(CommandParser.parse("secret") is Command.ShowHiddenApps)

        assertTrue(CommandParser.parse("tui") is Command.ToggleTuiView)
        assertTrue(CommandParser.parse("tuiview") is Command.ToggleTuiView)
        assertTrue(CommandParser.parse("terminal") is Command.ToggleTuiView)

        val ai = CommandParser.parse("ai summarize this text")
        assertTrue(ai is Command.AiSearch)
        assertEquals("summarize this text", (ai as Command.AiSearch).prompt)

        val down = CommandParser.parse("down https://youtu.be/dQw4w9WgXcQ")
        assertTrue(down is Command.MediaDownload)
        assertEquals("https://youtu.be/dQw4w9WgXcQ", (down as Command.MediaDownload).url)

        val log = CommandParser.parse("log buy groceries")
        assertTrue(log is Command.LazyLogsAdd)
        assertEquals("buy groceries", (log as Command.LazyLogsAdd).rawText)

        val maps = CommandParser.parse("maps central park")
        assertTrue(maps is Command.GoogleMaps)
        assertEquals("central park", (maps as Command.GoogleMaps).query)

        val yt = CommandParser.parse("yt lofi hip hop")
        assertTrue(yt is Command.YoutubeSearch)
        assertEquals("lofi hip hop", (yt as Command.YoutubeSearch).query)

        val spot = CommandParser.parse("spot daft punk")
        assertTrue(spot is Command.SpotifySearch)
        assertEquals("daft punk", (spot as Command.SpotifySearch).query)

        val play = CommandParser.parse("play termux")
        assertTrue(play is Command.PlayStoreSearch)
        assertEquals("termux", (play as Command.PlayStoreSearch).query)

        val gh = CommandParser.parse("gh torvalds")
        assertTrue(gh is Command.GithubSearch)
        assertEquals("torvalds", (gh as Command.GithubSearch).query)

        val wiki = CommandParser.parse("wiki rust language")
        assertTrue(wiki is Command.WikipediaSearch)
        assertEquals("rust language", (wiki as Command.WikipediaSearch).query)

        val reddit = CommandParser.parse("reddit androiddev")
        assertTrue(reddit is Command.RedditSearch)
        assertEquals("androiddev", (reddit as Command.RedditSearch).query)

        val x = CommandParser.parse("x kotlin")
        assertTrue(x is Command.TwitterSearch)
        assertEquals("kotlin", (x as Command.TwitterSearch).query)

        val ddg = CommandParser.parse("ddg duckduckgo search")
        assertTrue(ddg is Command.DuckDuckGoSearch)
        assertEquals("duckduckgo search", (ddg as Command.DuckDuckGoSearch).query)

        val g = CommandParser.parse("g google query")
        assertTrue(g is Command.GoogleSearch)
        assertEquals("google query", (g as Command.GoogleSearch).query)

        val timer1 = CommandParser.parse("timer 5m")
        assertTrue(timer1 is Command.Timer)
        assertEquals(300, (timer1 as Command.Timer).durationSeconds)

        val timer2 = CommandParser.parse("timer 1h")
        assertTrue(timer2 is Command.Timer)
        assertEquals(3600, (timer2 as Command.Timer).durationSeconds)

        val timer3 = CommandParser.parse("timer 45s")
        assertTrue(timer3 is Command.Timer)
        assertEquals(45, (timer3 as Command.Timer).durationSeconds)

        assertTrue(CommandParser.parse("settings") is Command.OpenSettings)
        assertTrue(CommandParser.parse("config") is Command.OpenSettings)
        assertTrue(CommandParser.parse("pref") is Command.OpenSettings)

        val deepLink = CommandParser.parse("https://github.com/frsaghna/kern-launcher")
        assertTrue(deepLink is Command.DeepLinkUri)
        assertEquals("https://github.com/frsaghna/kern-launcher", (deepLink as Command.DeepLinkUri).uriString)
    }

    @Test
    fun testCommandsWithMissingArguments() {
        val ai = CommandParser.parse("ai")
        assertTrue(ai is Command.AiSearch)
        assertEquals("", (ai as Command.AiSearch).prompt)

        val down = CommandParser.parse("down")
        assertTrue(down is Command.MediaDownload)
        assertEquals("", (down as Command.MediaDownload).url)

        val log = CommandParser.parse("log")
        assertTrue(log is Command.LazyLogsAdd)
        assertEquals("", (log as Command.LazyLogsAdd).rawText)

        val g = CommandParser.parse("g")
        assertTrue(g is Command.GoogleSearch)
        assertEquals("", (g as Command.GoogleSearch).query)

        val timer = CommandParser.parse("timer")
        assertTrue(timer is Command.Timer)
        assertEquals(60, (timer as Command.Timer).durationSeconds)
    }

    @Test
    fun testUnknownCommand() {
        val cmd = CommandParser.parse("nonexistentcommand with some args")
        assertTrue(cmd is Command.Unknown)
        assertEquals("nonexistentcommand with some args", (cmd as Command.Unknown).rawQuery)
    }

    @Test
    fun testCommandWithTrailingWhitespace() {
        assertTrue(CommandParser.parse("help   \t  ") is Command.Help)
        val g = CommandParser.parse("g    test query   ")
        assertTrue(g is Command.GoogleSearch)
        assertEquals("test query", (g as Command.GoogleSearch).query)
    }

    @Test
    fun testCaseVariations() {
        assertTrue(CommandParser.parse("HELP") is Command.Help)
        assertTrue(CommandParser.parse("Settings") is Command.OpenSettings)
        assertTrue(CommandParser.parse("TuI") is Command.ToggleTuiView)
        val timer = CommandParser.parse("TIMER 10M")
        assertTrue(timer is Command.Timer)
        assertEquals(600, (timer as Command.Timer).durationSeconds)
    }
}
