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
import androidx.compose.ui.text.style.TextAlign
import com.kern.launcher.service.intent.IntentFactory
import com.kern.launcher.ui.theme.KernTextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DateView(
    alignmentOption: String = "LEFT",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentDate by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentDate = Date()
            delay(60000)
        }
    }

    val formatter = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }
    val formattedDate = formatter.format(currentDate)

    val textAlign = when (alignmentOption.uppercase()) {
        "CENTER" -> TextAlign.Center
        "RIGHT" -> TextAlign.End
        else -> TextAlign.Start
    }

    Text(
        text = formattedDate,
        textAlign = textAlign,
        style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
            color = KernTextSecondary
        ),
        modifier = modifier.clickable {
            IntentFactory.openCalendar(context)
        }
    )
}
