package com.kern.launcher.model

import androidx.compose.ui.graphics.ImageBitmap

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
    val iconBitmap: ImageBitmap? = null,
    val actionCommand: Command,
    val score: Double = 0.0
)
