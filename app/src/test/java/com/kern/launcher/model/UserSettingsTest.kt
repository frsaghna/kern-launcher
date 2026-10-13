package com.kern.launcher.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserSettingsTest {

    @Test
    fun testDefaultUserSettingsValues() {
        val settings = UserSettings()

        // Theme and Visual defaults
        assertTrue("isDarkMode default should be true", settings.isDarkMode)
        assertTrue("isOledBlack default should be true", settings.isOledBlack)
        assertEquals("VS_CODE_DARK", settings.themePalette)
        assertEquals("#0D1117", settings.customBgColor)
        assertEquals("#58A6FF", settings.customAccentColor)
        assertEquals("#C9D1D9", settings.customTextColor)
        assertTrue("sharpCorners default should be true", settings.sharpCorners)
        assertEquals("MONOSPACE", settings.fontStyle)
        assertFalse("isTransparentBg default should be false", settings.isTransparentBg)
        assertTrue("showAppListOutlines default should be true", settings.showAppListOutlines)

        // App and Search behavior defaults
        assertTrue("showAppIcons default should be true", settings.showAppIcons)
        assertTrue("autoLaunchSingleMatch default should be true", settings.autoLaunchSingleMatch)
        assertFalse("tuiViewMode default should be false", settings.tuiViewMode)
        assertTrue("autoFocusKeyboard default should be true", settings.autoFocusKeyboard)
        assertEquals("", settings.swipeLeftPackage)
        assertEquals("", settings.swipeRightPackage)
        assertEquals("CHATGPT", settings.aiProvider)

        // Clock and Date defaults
        assertTrue("showClock default should be true", settings.showClock)
        assertTrue("clockFormat24h default should be true", settings.clockFormat24h)
        assertTrue("showDate default should be true", settings.showDate)
        assertEquals("LEFT", settings.clockAlignment)
        assertEquals("LEFT", settings.appListAlignment)
        assertEquals("MEDIUM", settings.clockFontSize)
        assertEquals("MEDIUM", settings.appListFontSize)
    }

    @Test
    fun testUserSettingsCopy() {
        val original = UserSettings()
        val modified = original.copy(
            themePalette = "DRACULA",
            sharpCorners = false,
            tuiViewMode = true
        )

        assertEquals("DRACULA", modified.themePalette)
        assertFalse(modified.sharpCorners)
        assertTrue(modified.tuiViewMode)
        assertTrue(modified.isDarkMode) // Unmodified field remains default
    }
}
