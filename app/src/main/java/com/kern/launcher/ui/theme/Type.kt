package com.kern.launcher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun createKernTypography(fontStyle: String): Typography {
    val family = when (fontStyle.uppercase()) {
        "MONOSPACE" -> FontFamily.Monospace
        "SANS_SERIF" -> FontFamily.SansSerif
        "SERIF" -> FontFamily.Serif
        "CURSIVE" -> FontFamily.Cursive
        "DEFAULT" -> FontFamily.Default
        else -> FontFamily.Monospace
    }

    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 72.sp,
            lineHeight = 76.sp,
            letterSpacing = (-0.5).sp
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}

fun getTypography(fontStyle: String): Typography = createKernTypography(fontStyle)
