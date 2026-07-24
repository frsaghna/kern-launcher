package com.kern.launcher.search.ranking

import com.kern.launcher.model.AppInfo

object SearchRanker {

    fun calculateScore(app: AppInfo, query: String): Double {
        val q = query.trim().lowercase()
        val label = app.label.lowercase()

        if (q.isEmpty()) {
            val usageBonus = app.usageCount * 10.0
            val recencyBonus = if (app.lastUsedTime > 0) {
                val hoursAgo = ((System.currentTimeMillis() - app.lastUsedTime) / (1000 * 3600)).coerceAtLeast(0)
                (100.0 / (hoursAgo + 1.0))
            } else 0.0
            return usageBonus + recencyBonus
        }

        var matchScore = 0.0

        when {
            label == q -> matchScore = 1000.0
            label.startsWith(q) -> matchScore = 800.0 - (label.length - q.length)
            label.contains(" $q") -> matchScore = 600.0
            label.contains(q) -> matchScore = 400.0
            else -> return 0.0
        }

        val usageScore = app.usageCount * 5.0
        val recencyScore = if (app.lastUsedTime > 0) {
            val hoursAgo = ((System.currentTimeMillis() - app.lastUsedTime) / (1000 * 3600)).coerceAtLeast(0)
            (50.0 / (hoursAgo + 1.0))
        } else 0.0

        return matchScore + usageScore + recencyScore
    }
}
