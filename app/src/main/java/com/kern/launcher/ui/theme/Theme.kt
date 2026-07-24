package com.kern.launcher.ui.theme

import android.app.Activity
import android.view.WindowManager
import androidx.compose.material3.ColorScheme
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

data class PaletteColors(
    val bg: Color,
    val accent: Color,
    val text: Color
)

val MONKEYTYPE_PALETTES = mapOf(
    "SERIKA_DARK" to PaletteColors(Color(0xFF323437), Color(0xFFE2B714), Color(0xFFD1D0C5)),
    "BOTANICAL" to PaletteColors(Color(0xFF7B9E87), Color(0xFFEAF0CE), Color(0xFFF1F7ED)),
    "CHARCOAL" to PaletteColors(Color(0xFF0F0F0F), Color(0xFFE6E6E6), Color(0xFFCCCCCC)),
    "MATRIX" to PaletteColors(Color(0xFF000000), Color(0xFF00FF41), Color(0xFF008000)),
    "8008" to PaletteColors(Color(0xFF333A42), Color(0xFFF55D7A), Color(0xFFE9ECEE)),
    "NINES" to PaletteColors(Color(0xFF080808), Color(0xFF999999), Color(0xFFCCCCCC)),
    "MILKWELL" to PaletteColors(Color(0xFF9C8B6B), Color(0xFF495E35), Color(0xFFD1C2A5)),
    "VAPORWAVE" to PaletteColors(Color(0xFF1A0933), Color(0xFFFF71CE), Color(0xFF01CDFE)),
    "BENTO" to PaletteColors(Color(0xFF2D394D), Color(0xFFFF6961), Color(0xFFFFFDF5)),
    "CARBON" to PaletteColors(Color(0xFF313131), Color(0xFFF66E0D), Color(0xFFF5E6C8)),
    "OLED_BLACK" to PaletteColors(Color(0xFF000000), Color(0xFF00FF66), Color(0xFFFFFFFF)),
    "SOYUZ" to PaletteColors(Color(0xFF8B0000), Color(0xFFFFD700), Color(0xFFFFFFFF)),
    "HAMMERHEAD" to PaletteColors(Color(0xFF030613), Color(0xFF4FCDA5), Color(0xFFE2F1F5)),
    "TAROT" to PaletteColors(Color(0xFF130F1A), Color(0xFFC39A6B), Color(0xFFE6D3B3)),
    "MERVO" to PaletteColors(Color(0xFF000B1E), Color(0xFFFF0055), Color(0xFF00FFCC)),
    "CHEESE" to PaletteColors(Color(0xFFFEEA00), Color(0xFF000000), Color(0xFF333333)),
    "OLIVIA" to PaletteColors(Color(0xFF1C1B1B), Color(0xFFDEAF9D), Color(0xFFF7F7F7)),
    "MIAMI" to PaletteColors(Color(0xFF181819), Color(0xFFE4609B), Color(0xFF47BAC0)),
    "LASER" to PaletteColors(Color(0xFF271844), Color(0xFF00E5FF), Color(0xFFFF2A6D)),
    "RUST" to PaletteColors(Color(0xFF2B1704), Color(0xFFDA6B2B), Color(0xFFE89D66)),
    "TERRA" to PaletteColors(Color(0xFF0C1821), Color(0xFF326273), Color(0xFFEEEEEE)),
    "LAVENDER" to PaletteColors(Color(0xFF2D2B55), Color(0xFFFAD000), Color(0xFFA599E2)),
    "DRAKE" to PaletteColors(Color(0xFF111111), Color(0xFF00FF87), Color(0xFFEEEEEE)),
    "BUSHIDO" to PaletteColors(Color(0xFF242933), Color(0xFFEC407A), Color(0xFFF3F4F6)),
    "SAMURAI" to PaletteColors(Color(0xFF1F0E17), Color(0xFFC81D25), Color(0xFFE6C280)),
    "MATCHA" to PaletteColors(Color(0xFF323A2C), Color(0xFF98B06F), Color(0xFFE8EBE4)),
    "MOCHA" to PaletteColors(Color(0xFF3C3836), Color(0xFFD79921), Color(0xFFEBDBB2)),
    "NORD" to PaletteColors(Color(0xFF2E3440), Color(0xFF88C0D0), Color(0xFFECEFF4)),
    "OCEAN" to PaletteColors(Color(0xFF0F1D2A), Color(0xFF00B4D8), Color(0xFFCAF0F8)),
    "COMMODORE" to PaletteColors(Color(0xFF4032B3), Color(0xFF9195F6), Color(0xFFFFFFFF)),
    "DRACULA" to PaletteColors(Color(0xFF282A36), Color(0xFFFF79C6), Color(0xFFF8F8F2)),
    "MONOKAI_PRO" to PaletteColors(Color(0xFF2D2A2E), Color(0xFFFFD866), Color(0xFFFCFCFA)),
    "VSCODE_DARK" to PaletteColors(Color(0xFF1E1E1E), Color(0xFF00FF66), Color(0xFFD4D4D4)),
    "ONE_DARK" to PaletteColors(Color(0xFF21252B), Color(0xFF61AFEF), Color(0xFFABB2BF)),
    "TOKYO_NIGHT" to PaletteColors(Color(0xFF1A1B26), Color(0xFF7AA2F7), Color(0xFFC0CAF5)),
    "SUBMECHA" to PaletteColors(Color(0xFF1D1D1D), Color(0xFF5C7CFA), Color(0xFFDCDCDC)),
    "LUNA" to PaletteColors(Color(0xFF221C35), Color(0xFFF5B070), Color(0xFFEBE5F5)),
    "PAPER" to PaletteColors(Color(0xFFEEEEEE), Color(0xFF444444), Color(0xFF111111))
)

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
fun getThemeColorScheme(palette: String, userSettings: UserSettings = UserSettings()): ColorScheme {
    val key = palette.uppercase()
    val (bg, accent, text) = if (key == "CUSTOM") {
        Triple(
            parseHexColor(userSettings.customBgColor, Color(0xFF0D1117)),
            parseHexColor(userSettings.customAccentColor, Color(0xFF58A6FF)),
            parseHexColor(userSettings.customTextColor, Color(0xFFC9D1D9))
        )
    } else {
        val theme = MONKEYTYPE_PALETTES[key] ?: MONKEYTYPE_PALETTES["SERIKA_DARK"] ?: PaletteColors(Color(0xFF323437), Color(0xFFE2B714), Color(0xFFD1D0C5))
        Triple(theme.bg, theme.accent, theme.text)
    }

    val onVariantText = text.copy(alpha = 0.7f)
    val surfaceColor = bg.copy(alpha = 0.9f)

    return darkColorScheme(
        primary = accent,
        onPrimary = bg,
        primaryContainer = accent.copy(alpha = 0.2f),
        onPrimaryContainer = accent,
        secondary = accent,
        onSecondary = bg,
        secondaryContainer = accent.copy(alpha = 0.15f),
        onSecondaryContainer = accent,
        tertiary = accent,
        onTertiary = bg,
        background = bg,
        onBackground = text,
        surface = surfaceColor,
        onSurface = text,
        surfaceVariant = surfaceColor,
        onSurfaceVariant = onVariantText,
        outline = accent.copy(alpha = 0.4f),
        outlineVariant = accent.copy(alpha = 0.2f),
        scrim = Color.Black
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00FF66),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFF00FF66),
    onSecondary = Color(0xFF000000),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF212529),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF212529),
    surfaceVariant = Color(0xFFE9ECEF),
    onSurfaceVariant = Color(0xFF495057),
    outline = Color(0xFFCED4DA)
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
