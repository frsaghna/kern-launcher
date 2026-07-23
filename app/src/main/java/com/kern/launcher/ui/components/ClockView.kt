package com.kern.launcher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.kern.launcher.service.intent.IntentFactory
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockView(
    is24Hour: Boolean = true,
    fontSizeOption: String = "MEDIUM",
    alignmentOption: String = "LEFT",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    val pattern = if (is24Hour) "HH:mm" else "hh:mm a"
    val formatter = remember(is24Hour) { SimpleDateFormat(pattern, Locale.getDefault()) }
    val formattedTime = formatter.format(currentTime)

    val fontSizeSp = when (fontSizeOption.uppercase()) {
        "SMALL" -> 48.sp
        "LARGE" -> 96.sp
        else -> 72.sp
    }

    val textAlign = when (alignmentOption.uppercase()) {
        "CENTER" -> TextAlign.Center
        "RIGHT" -> TextAlign.End
        else -> TextAlign.Start
    }

    Text(
        text = formattedTime,
        textAlign = textAlign,
        style = MaterialTheme.typography.displayLarge.copy(
            fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
            fontSize = fontSizeSp,
            lineHeight = fontSizeSp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier.clickable {
            IntentFactory.openClock(context)
        }
    )
}
