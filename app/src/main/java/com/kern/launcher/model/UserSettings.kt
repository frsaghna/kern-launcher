package com.kern.launcher.model

data class UserSettings(
    val isDarkMode: Boolean = true,
    val isOledBlack: Boolean = true,
    val themePalette: String = "VS_CODE_DARK", // VS_CODE_DARK, OLED_MONOCHROME, DRACULA, MONOKAI, ONE_DARK, TOKYO_NIGHT, GRUVBOX, NORD, CYBERPUNK, CUSTOM
    val customBgColor: String = "#0D1117",
    val customAccentColor: String = "#58A6FF",
    val customTextColor: String = "#C9D1D9",
    val sharpCorners: Boolean = true,
    val fontStyle: String = "MONOSPACE", // MONOSPACE, SANS_SERIF, SERIF, CURSIVE, MONO_BOLD, DEFAULT
    val showAppIcons: Boolean = true,
    val autoLaunchSingleMatch: Boolean = true,
    val tuiViewMode: Boolean = false,
    val showClock: Boolean = true,
    val clockFormat24h: Boolean = true,
    val showDate: Boolean = true,
    val autoFocusKeyboard: Boolean = true,
    val swipeLeftPackage: String = "",
    val swipeRightPackage: String = "",
    val aiProvider: String = "CHATGPT", // CHATGPT, GEMINI, PERPLEXITY, CLAUDE
    val clockAlignment: String = "LEFT", // LEFT, CENTER, RIGHT
    val appListAlignment: String = "LEFT", // LEFT, CENTER, RIGHT
    val clockFontSize: String = "MEDIUM", // SMALL, MEDIUM, LARGE
    val appListFontSize: String = "MEDIUM", // SMALL, MEDIUM, LARGE
    val isTransparentBg: Boolean = false,
    val showAppListOutlines: Boolean = true
)
