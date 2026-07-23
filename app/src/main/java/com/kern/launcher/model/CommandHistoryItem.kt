package com.kern.launcher.model

data class CommandHistoryItem(
    val id: Long = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis(),
    val executionCount: Int = 1
)
