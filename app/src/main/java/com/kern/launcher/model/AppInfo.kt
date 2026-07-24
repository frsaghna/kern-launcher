package com.kern.launcher.model

import androidx.compose.ui.graphics.ImageBitmap

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val usageCount: Int = 0,
    val lastUsedTime: Long = 0L,
    val iconBitmap: ImageBitmap? = null
)
