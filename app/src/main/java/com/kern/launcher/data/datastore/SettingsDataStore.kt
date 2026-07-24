package com.kern.launcher.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kern.launcher.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kern_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val OLED_BLACK = booleanPreferencesKey("is_oled_black")
        val THEME_PALETTE = stringPreferencesKey("theme_palette")
        val CUSTOM_BG_COLOR = stringPreferencesKey("custom_bg_color")
        val CUSTOM_ACCENT_COLOR = stringPreferencesKey("custom_accent_color")
        val CUSTOM_TEXT_COLOR = stringPreferencesKey("custom_text_color")
        val SHARP_CORNERS = booleanPreferencesKey("sharp_corners")
        val FONT_STYLE = stringPreferencesKey("font_style")
        val SHOW_APP_ICONS = booleanPreferencesKey("show_app_icons")
        val AUTO_LAUNCH_SINGLE = booleanPreferencesKey("auto_launch_single")
        val TUI_VIEW_MODE = booleanPreferencesKey("tui_view_mode")
        val SHOW_CLOCK = booleanPreferencesKey("show_clock")
        val CLOCK_24H = booleanPreferencesKey("clock_24h")
        val SHOW_DATE = booleanPreferencesKey("show_date")
        val AUTO_FOCUS_KEYBOARD = booleanPreferencesKey("auto_focus_keyboard")
        val SWIPE_LEFT_PKG = stringPreferencesKey("swipe_left_pkg")
        val SWIPE_RIGHT_PKG = stringPreferencesKey("swipe_right_pkg")
        val AI_PROVIDER = stringPreferencesKey("ai_provider")
        val CLOCK_ALIGNMENT = stringPreferencesKey("clock_alignment")
        val APP_LIST_ALIGNMENT = stringPreferencesKey("app_list_alignment")
        val CLOCK_FONT_SIZE = stringPreferencesKey("clock_font_size")
        val APP_LIST_FONT_SIZE = stringPreferencesKey("app_list_font_size")
        val TRANSPARENT_BG = booleanPreferencesKey("transparent_bg")
        val SHOW_APP_LIST_OUTLINES = booleanPreferencesKey("show_app_list_outlines")
    }

    val userSettings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            isDarkMode = prefs[Keys.DARK_MODE] ?: true,
            isOledBlack = prefs[Keys.OLED_BLACK] ?: true,
            themePalette = prefs[Keys.THEME_PALETTE] ?: "VS_CODE_DARK",
            customBgColor = prefs[Keys.CUSTOM_BG_COLOR] ?: "#0D1117",
            customAccentColor = prefs[Keys.CUSTOM_ACCENT_COLOR] ?: "#58A6FF",
            customTextColor = prefs[Keys.CUSTOM_TEXT_COLOR] ?: "#C9D1D9",
            sharpCorners = prefs[Keys.SHARP_CORNERS] ?: true,
            fontStyle = prefs[Keys.FONT_STYLE] ?: "MONOSPACE",
            showAppIcons = prefs[Keys.SHOW_APP_ICONS] ?: true,
            autoLaunchSingleMatch = prefs[Keys.AUTO_LAUNCH_SINGLE] ?: true,
            tuiViewMode = prefs[Keys.TUI_VIEW_MODE] ?: false,
            showClock = prefs[Keys.SHOW_CLOCK] ?: true,
            clockFormat24h = prefs[Keys.CLOCK_24H] ?: true,
            showDate = prefs[Keys.SHOW_DATE] ?: true,
            autoFocusKeyboard = prefs[Keys.AUTO_FOCUS_KEYBOARD] ?: true,
            swipeLeftPackage = prefs[Keys.SWIPE_LEFT_PKG] ?: "",
            swipeRightPackage = prefs[Keys.SWIPE_RIGHT_PKG] ?: "",
            aiProvider = prefs[Keys.AI_PROVIDER] ?: "CHATGPT",
            clockAlignment = prefs[Keys.CLOCK_ALIGNMENT] ?: "LEFT",
            appListAlignment = prefs[Keys.APP_LIST_ALIGNMENT] ?: "LEFT",
            clockFontSize = prefs[Keys.CLOCK_FONT_SIZE] ?: "MEDIUM",
            appListFontSize = prefs[Keys.APP_LIST_FONT_SIZE] ?: "MEDIUM",
            isTransparentBg = prefs[Keys.TRANSPARENT_BG] ?: false,
            showAppListOutlines = prefs[Keys.SHOW_APP_LIST_OUTLINES] ?: true
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.DARK_MODE] = enabled }
    }

    suspend fun setOledBlack(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.OLED_BLACK] = enabled }
    }

    suspend fun setThemePalette(palette: String) {
        context.dataStore.edit { prefs -> prefs[Keys.THEME_PALETTE] = palette }
    }

    suspend fun setCustomBgColor(colorHex: String) {
        context.dataStore.edit { prefs -> prefs[Keys.CUSTOM_BG_COLOR] = colorHex }
    }

    suspend fun setCustomAccentColor(colorHex: String) {
        context.dataStore.edit { prefs -> prefs[Keys.CUSTOM_ACCENT_COLOR] = colorHex }
    }

    suspend fun setCustomTextColor(colorHex: String) {
        context.dataStore.edit { prefs -> prefs[Keys.CUSTOM_TEXT_COLOR] = colorHex }
    }

    suspend fun setSharpCorners(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.SHARP_CORNERS] = enabled }
    }

    suspend fun setFontStyle(style: String) {
        context.dataStore.edit { prefs -> prefs[Keys.FONT_STYLE] = style }
    }

    suspend fun setShowAppIcons(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.SHOW_APP_ICONS] = enabled }
    }

    suspend fun setAutoLaunchSingleMatch(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.AUTO_LAUNCH_SINGLE] = enabled }
    }

    suspend fun setTuiViewMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.TUI_VIEW_MODE] = enabled }
    }

    suspend fun setShowClock(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.SHOW_CLOCK] = enabled }
    }

    suspend fun setClock24h(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.CLOCK_24H] = enabled }
    }

    suspend fun setShowDate(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.SHOW_DATE] = enabled }
    }

    suspend fun setAutoFocusKeyboard(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.AUTO_FOCUS_KEYBOARD] = enabled }
    }

    suspend fun setSwipeLeftPackage(packageName: String) {
        context.dataStore.edit { prefs -> prefs[Keys.SWIPE_LEFT_PKG] = packageName }
    }

    suspend fun setSwipeRightPackage(packageName: String) {
        context.dataStore.edit { prefs -> prefs[Keys.SWIPE_RIGHT_PKG] = packageName }
    }

    suspend fun setAiProvider(provider: String) {
        context.dataStore.edit { prefs -> prefs[Keys.AI_PROVIDER] = provider }
    }

    suspend fun setClockAlignment(alignment: String) {
        context.dataStore.edit { prefs -> prefs[Keys.CLOCK_ALIGNMENT] = alignment }
    }

    suspend fun setAppListAlignment(alignment: String) {
        context.dataStore.edit { prefs -> prefs[Keys.APP_LIST_ALIGNMENT] = alignment }
    }

    suspend fun setClockFontSize(size: String) {
        context.dataStore.edit { prefs -> prefs[Keys.CLOCK_FONT_SIZE] = size }
    }

    suspend fun setAppListFontSize(size: String) {
        context.dataStore.edit { prefs -> prefs[Keys.APP_LIST_FONT_SIZE] = size }
    }

    suspend fun setTransparentBg(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.TRANSPARENT_BG] = enabled }
    }

    suspend fun setShowAppListOutlines(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.SHOW_APP_LIST_OUTLINES] = enabled }
    }
}
