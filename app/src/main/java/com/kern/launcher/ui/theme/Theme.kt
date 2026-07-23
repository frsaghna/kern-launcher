package com.kern.launcher.ui.theme

import android.app.Activity
import android.view.WindowManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.kern.launcher.model.UserSettings

fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        val cleanHex = hex.trim().removePrefix("#")
        val colorInt = when (cleanHex.length) {
            6 -> android.graphics.Color.parseColor("#$cleanHex")
            8 -> android.graphics.Color.parseColor("#$cleanHex")
            else -> return fallback
        }
        Color(colorInt)
    } catch (e: Exception) {
        fallback
    }
}

@Composable
fun getThemeColorScheme(palette: String, userSettings: UserSettings = UserSettings()) = when (palette.uppercase()) {
    "CUSTOM" -> {
        val customBg = parseHexColor(userSettings.customBgColor, Color(0xFF0D1117))
        val customAccent = parseHexColor(userSettings.customAccentColor, Color(0xFF58A6FF))
        val customText = parseHexColor(userSettings.customTextColor, Color(0xFFC9D1D9))
        darkColorScheme(
            primary = customAccent,
            secondary = customAccent,
            background = customBg,
            surface = customBg.copy(alpha = 0.85f),
            onBackground = customText,
            onSurface = customText
        )
    }
    "OLED_MONOCHROME" -> darkColorScheme(
        primary = OledMonoAccent,
        secondary = OledMonoAccent,
        background = OledMonoBg,
        surface = OledMonoSurface,
        onBackground = OledMonoAccent,
        onSurface = OledMonoAccent
    )
    "DRACULA" -> darkColorScheme(
        primary = DraculaAccent,
        secondary = DraculaAccent,
        background = DraculaBg,
        surface = DraculaSurface,
        onBackground = KernTextPrimary,
        onSurface = KernTextPrimary
    )
    "MONOKAI" -> darkColorScheme(
        primary = MonokaiAccent,
        secondary = MonokaiAccent,
        background = MonokaiBg,
        surface = MonokaiSurface,
        onBackground = KernTextPrimary,
        onSurface = KernTextPrimary
    )
    "ONE_DARK" -> darkColorScheme(
        primary = OneDarkAccent,
        secondary = OneDarkAccent,
        background = OneDarkBg,
        surface = OneDarkSurface,
        onBackground = KernTextPrimary,
        onSurface = KernTextPrimary
    )
    "TOKYO_NIGHT" -> darkColorScheme(
        primary = TokyoNightAccent,
        secondary = TokyoNightAccent,
        background = TokyoNightBg,
        surface = TokyoNightSurface,
        onBackground = KernTextPrimary,
        onSurface = KernTextPrimary
    )
    "GRUVBOX" -> darkColorScheme(
        primary = GruvboxAccent,
        secondary = GruvboxAccent,
        background = GruvboxBg,
        surface = GruvboxSurface,
        onBackground = KernTextPrimary,
        onSurface = KernTextPrimary
    )
    "NORD" -> darkColorScheme(
        primary = NordAccent,
        secondary = NordAccent,
        background = NordBg,
        surface = NordSurface,
        onBackground = KernTextPrimary,
        onSurface = KernTextPrimary
    )
    "CYBERPUNK" -> darkColorScheme(
        primary = CyberpunkAccent,
        secondary = CyberpunkAccent,
        background = CyberpunkBg,
        surface = CyberpunkSurface,
        onBackground = KernTextPrimary,
        onSurface = KernTextPrimary
    )
    else -> darkColorScheme( // VS_CODE_DARK
        primary = VsCodeDarkAccent,
        secondary = VsCodeDarkAccent,
        background = VsCodeDarkBg,
        surface = VsCodeDarkSurface,
        onBackground = KernTextPrimary,
        onSurface = KernTextPrimary
    )
}

private val LightColorScheme = lightColorScheme(
    primary = VsCodeDarkAccent,
    secondary = VsCodeDarkAccent,
    background = KernLightBackground,
    surface = KernLightSurface,
    onPrimary = KernLightBackground,
    onBackground = KernLightTextPrimary,
    onSurface = KernLightTextPrimary
)

@Composable
fun KernTheme(
    userSettings: UserSettings = UserSettings(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (!userSettings.isDarkMode) {
        LightColorScheme
    } else {
        getThemeColorScheme(userSettings.themePalette, userSettings)
    }

    val typography = createKernTypography(userSettings.fontStyle)
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            if (userSettings.isTransparentBg) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
                window.setBackgroundDrawableResource(android.R.color.transparent)
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !userSettings.isDarkMode
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
