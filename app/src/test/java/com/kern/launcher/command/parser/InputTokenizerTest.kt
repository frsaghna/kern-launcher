package com.kern.launcher.command.parser

import org.junit.Assert.assertEquals
import org.junit.Test

class InputTokenizerTest {

    @Test
    fun testEmptyString() {
        val result = InputTokenizer.tokenize("")
        assertEquals("", result.keyword)
        assertEquals("", result.args)
        assertEquals("", result.raw)
    }

    @Test
    fun testWhitespaceOnly() {
        val result = InputTokenizer.tokenize("    \t  \n  ")
        assertEquals("", result.keyword)
        assertEquals("", result.args)
        assertEquals("", result.raw)
    }

    @Test
    fun testSingleToken() {
        val result = InputTokenizer.tokenize("help")
        assertEquals("help", result.keyword)
        assertEquals("", result.args)
        assertEquals("help", result.raw)
    }

    @Test
    fun testMultipleSpaces() {
        val result = InputTokenizer.tokenize("  google   kotlin   compose  ")
        assertEquals("google", result.keyword)
        assertEquals("kotlin   compose", result.args)
        assertEquals("google   kotlin   compose", result.raw)
    }

    @Test
    fun testQuotedStrings() {
        val result = InputTokenizer.tokenize("alias \"my custom command\"")
        assertEquals("alias", result.keyword)
        assertEquals("\"my custom command\"", result.args)
    }

    @Test
    fun testUnmatchedQuote() {
        val result = InputTokenizer.tokenize("log \"unclosed quote message")
        assertEquals("log", result.keyword)
        assertEquals("\"unclosed quote message", result.args)
    }

    @Test
    fun testVeryLongInput() {
        val longArgs = "a".repeat(10000)
        val result = InputTokenizer.tokenize("search $longArgs")
        assertEquals("search", result.keyword)
        assertEquals(longArgs, result.args)
    }

    @Test
    fun testUnicodeAndEmoji() {
        val result = InputTokenizer.tokenize("ai 🚀 search 日本語 한국어")
        assertEquals("ai", result.keyword)
        assertEquals("🚀 search 日本語 한국어", result.args)
    }

    @Test
    fun testControlCharacters() {
        val result = InputTokenizer.tokenize("test\twith\nnewlines\rand\u0000null")
        assertEquals("test", result.keyword)
        assertEquals("with\nnewlines\rand\u0000null", result.args)
    }
}
