package com.kern.launcher.search.ranking

import com.kern.launcher.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchRankerTest {

    private fun createApp(label: String, usageCount: Int = 0, lastUsedTime: Long = 0L): AppInfo {
        return AppInfo(
            packageName = "com.test.${label.lowercase().replace(" ", "")}",
            activityName = "com.test.MainActivity",
            label = label,
            usageCount = usageCount,
            lastUsedTime = lastUsedTime
        )
    }

    @Test
    fun testDeterminismForIdenticalInput() {
        val app = createApp("Settings", usageCount = 5, lastUsedTime = 1000L)
        val score1 = SearchRanker.calculateScore(app, "set")
        val score2 = SearchRanker.calculateScore(app, "set")
        assertEquals(score1, score2, 0.0001)
    }

    @Test
    fun testExactMatchOutranksPrefixMatchOutranksSubstring() {
        val exact = createApp("Cal")
        val prefix = createApp("Calculator")
        val substring = createApp("My Calculator")

        val scoreExact = SearchRanker.calculateScore(exact, "cal")
        val scorePrefix = SearchRanker.calculateScore(prefix, "cal")
        val scoreSubstring = SearchRanker.calculateScore(substring, "cal")

        assertTrue("Exact match should score higher than prefix match", scoreExact > scorePrefix)
        assertTrue("Prefix match should score higher than substring match", scorePrefix > scoreSubstring)
    }

    @Test
    fun testUsageCountInfluencesOrder() {
        val frequentApp = createApp("Browser", usageCount = 20)
        val infrequentApp = createApp("Browser", usageCount = 1)

        val scoreFrequent = SearchRanker.calculateScore(frequentApp, "browser")
        val scoreInfrequent = SearchRanker.calculateScore(infrequentApp, "browser")

        assertTrue("Higher usage count should yield higher score", scoreFrequent > scoreInfrequent)
    }

    @Test
    fun testEmptyQuery() {
        val appWithUsage = createApp("Music", usageCount = 10)
        val appWithoutUsage = createApp("Files", usageCount = 0)

        val scoreWithUsage = SearchRanker.calculateScore(appWithUsage, "")
        val scoreWithoutUsage = SearchRanker.calculateScore(appWithoutUsage, "")

        assertTrue("Empty query should grant usage bonus", scoreWithUsage > scoreWithoutUsage)
    }

    @Test
    fun testTieBreakingIsStable() {
        val appA = createApp("Camera", usageCount = 0)
        val appB = createApp("Camera", usageCount = 0)

        val scoreA = SearchRanker.calculateScore(appA, "cam")
        val scoreB = SearchRanker.calculateScore(appB, "cam")

        assertEquals("Identical applications must have identical score for deterministic sorting", scoreA, scoreB, 0.0001)
    }
}
