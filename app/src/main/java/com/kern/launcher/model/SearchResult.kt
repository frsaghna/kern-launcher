package com.kern.launcher.model

import android.graphics.drawable.Drawable

enum class SearchResultType {
    APP,
    BUILTIN_COMMAND,
    CALCULATOR_RESULT,
    ALIAS,
    HISTORY
}

data class SearchResult(
    val id: String,
    val type: SearchResultType,
    val title: String,
    val subtitle: String,
    val icon: Drawable? = null,
    val actionCommand: Command,
    val score: Double = 0.0
)
