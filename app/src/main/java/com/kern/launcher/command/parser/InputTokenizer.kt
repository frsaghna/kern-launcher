package com.kern.launcher.command.parser

data class TokenizedInput(
    val keyword: String,
    val args: String,
    val raw: String
)

object InputTokenizer {
    fun tokenize(input: String): TokenizedInput {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return TokenizedInput("", "", "")
        
        val parts = trimmed.split("\\s+".toRegex(), limit = 2)
        val keyword = parts[0].lowercase()
        val args = if (parts.size > 1) parts[1] else ""
        
        return TokenizedInput(keyword = keyword, args = args, raw = trimmed)
    }
}
