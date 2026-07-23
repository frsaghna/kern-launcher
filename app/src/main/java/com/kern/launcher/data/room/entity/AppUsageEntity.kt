package com.kern.launcher.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage")
data class AppUsageEntity(
    @PrimaryKey val packageName: String,
    val activityName: String,
    val usageCount: Int,
    val lastUsedTime: Long
)
