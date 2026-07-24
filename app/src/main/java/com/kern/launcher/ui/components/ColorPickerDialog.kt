package com.kern.launcher.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kern.launcher.ui.theme.KernDarkSurfaceBorder
import com.kern.launcher.ui.theme.KernTextSecondary

fun hsvToColor(hue: Float, saturation: Float, value: Float): Color {
    val hsv = floatArrayOf(hue.coerceIn(0f, 360f), saturation.coerceIn(0f, 1f), value.coerceIn(0f, 1f))
    return Color(AndroidColor.HSVToColor(hsv))
}

fun hsvToHex(hue: Float, saturation: Float, value: Float): String {
    val hsv = floatArrayOf(hue.coerceIn(0f, 360f), saturation.coerceIn(0f, 1f), value.coerceIn(0f, 1f))
    val colorInt = AndroidColor.HSVToColor(hsv)
    return String.format("#%06X", 0xFFFFFF and colorInt)
}

fun hexToHsvArray(hex: String): FloatArray {
    val hsv = FloatArray(3)
    try {
        val clean = hex.trim().removePrefix("#")
        val parsedInt = when (clean.length) {
            6 -> (0xFF000000.toInt()) or clean.toInt(16)
            8 -> clean.toLong(16).toInt()
            else -> AndroidColor.BLACK
        }
        AndroidColor.colorToHSV(parsedInt, hsv)
    } catch (e: Exception) {
        hsv[0] = 0f
        hsv[1] = 0f
        hsv[2] = 0f
    }
    return hsv
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerDialog(
    title: String,
    initialColorHex: String,
    sharpCorners: Boolean,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialHsv = remember(initialColorHex) { hexToHsvArray(initialColorHex) }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    var hexText by remember { mutableStateOf(hsvToHex(hue, saturation, value)) }

    val currentColor = hsvToColor(hue, saturation, value)
    val initialColor = remember(initialColorHex) {
        try {
            val clean = initialColorHex.trim().removePrefix("#")
            Color((0xFF000000.toInt()) or clean.toInt(16))
        } catch (e: Exception) {
            Color.Black
        }
    }

    val cornerRadius = if (sharpCorners) 0.dp else 12.dp

    val quickSwatches = listOf(
        "#000000", "#181825", "#1E1E1E", "#11001C", "#0F172A",
        "#FFFFFF", "#C9D1D9", "#F8F8F2", "#A6ADC8", "#808080",
        "#00FF66", "#00E5FF", "#58A6FF", "#BD93F9", "#FF79C6",
        "#FF5555", "#FF9900", "#FFD700", "#50FA7B", "#8BE9FD"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(cornerRadius)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Color Preview Card (Old vs New)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(if (sharpCorners) 0.dp else 8.dp))
                        .border(1.dp, KernDarkSurfaceBorder, RoundedCornerShape(if (sharpCorners) 0.dp else 8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(initialColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PREVIOUS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (initialHsv[2] > 0.5f) Color.Black else Color.White,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(currentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NEW COLOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (value > 0.5f) Color.Black else Color.White,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Hue Slider
                Text(text = "HUE (${hue.toInt()}°)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Red, Color.Yellow, Color.Green,
                                    Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                                )
                            )
                        )
                )
                Slider(
                    value = hue,
                    onValueChange = {
                        hue = it
                        hexText = hsvToHex(hue, saturation, value)
                    },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Saturation Slider
                Text(text = "SATURATION (${(saturation * 100).toInt()}%)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    hsvToColor(hue, 0f, value),
                                    hsvToColor(hue, 1f, value)
                                )
                            )
                        )
                )
                Slider(
                    value = saturation,
                    onValueChange = {
                        saturation = it
                        hexText = hsvToHex(hue, saturation, value)
                    },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Brightness / Value Slider
                Text(text = "BRIGHTNESS (${(value * 100).toInt()}%)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    hsvToColor(hue, saturation, 0f),
                                    hsvToColor(hue, saturation, 1f)
                                )
                            )
                        )
                )
                Slider(
                    value = value,
                    onValueChange = {
                        value = it
                        hexText = hsvToHex(hue, saturation, value)
                    },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hex Input Field
                OutlinedTextField(
                    value = hexText,
                    onValueChange = { input ->
                        hexText = input
                        if (input.startsWith("#") && (input.length == 7 || input.length == 9)) {
                            val newHsv = hexToHsvArray(input)
                            hue = newHsv[0]
                            saturation = newHsv[1]
                            value = newHsv[2]
                        }
                    },
                    label = { Text("HEX Color Code", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(if (sharpCorners) 0.dp else 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = KernDarkSurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Swatch Presets Grid
                Text(text = "PRESET PALETTES", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = KernTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickSwatches.forEach { hex ->
                        val swatchColor = try {
                            val clean = hex.removePrefix("#")
                            Color((0xFF000000.toInt()) or clean.toInt(16))
                        } catch (e: Exception) {
                            Color.Black
                        }

                        val isSelected = hexText.equals(hex, ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(swatchColor)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else KernDarkSurfaceBorder,
                                    shape = CircleShape
                                )
                                .clickable {
                                    hexText = hex
                                    val newHsv = hexToHsvArray(hex)
                                    hue = newHsv[0]
                                    saturation = newHsv[1]
                                    value = newHsv[2]
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(cornerRadius),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CANCEL", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onColorSelected(hexText)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(cornerRadius),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("APPLY COLOR", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
