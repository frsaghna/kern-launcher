package com.kern.launcher.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val usageCount: Int = 0,
    val lastUsedTime: Long = 0L,
    val icon: Drawable? = null
)
