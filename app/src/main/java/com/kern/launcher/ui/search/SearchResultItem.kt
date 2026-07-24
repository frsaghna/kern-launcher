package com.kern.launcher.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kern.launcher.model.Command
import com.kern.launcher.model.SearchResult
import com.kern.launcher.model.SearchResultType
import com.kern.launcher.ui.theme.KernDarkSurfaceBorder
import com.kern.launcher.ui.theme.KernGreen
import com.kern.launcher.ui.theme.KernTextMuted
import com.kern.launcher.ui.theme.KernTextSecondary
import com.kern.launcher.ui.theme.KernYellow

@Composable
fun SearchResultItem(
    result: SearchResult,
    isSelected: Boolean,
    showAppIcons: Boolean,
    sharpCorners: Boolean,
    alignmentOption: String = "LEFT",
    fontSizeOption: String = "MEDIUM",
    showOutlines: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerRadius = if (sharpCorners) 0.dp else 10.dp

    val backgroundColor = if (showOutlines) {
        if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
    } else {
        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
    }

    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else KernDarkSurfaceBorder

    val itemFontSize = when (fontSizeOption.uppercase()) {
        "SMALL" -> 13.sp
        "LARGE" -> 18.sp
        else -> 15.sp
    }

    val textAlign = when (alignmentOption.uppercase()) {
        "CENTER" -> TextAlign.Center
        "RIGHT" -> TextAlign.End
        else -> TextAlign.Start
    }

    val columnAlignment = when (alignmentOption.uppercase()) {
        "CENTER" -> Alignment.CenterHorizontally
        "RIGHT" -> Alignment.End
        else -> Alignment.Start
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .then(
                if (showOutlines) Modifier.border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
                else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Render Icon / Badge (if enabled)
        if (showAppIcons) {
            SearchResultIcon(result = result, sharpCorners = sharpCorners)
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = columnAlignment
        ) {
            Text(
                text = result.title,
                textAlign = textAlign,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    fontSize = itemFontSize
                )
            )
            if (result.subtitle.isNotBlank()) {
                Text(
                    text = result.subtitle,
                    textAlign = textAlign,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = KernTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun SearchResultIcon(result: SearchResult, sharpCorners: Boolean) {
    val cornerRadius = if (sharpCorners) 0.dp else 6.dp
    val iconBitmap = result.iconBitmap

    if (iconBitmap != null) {
        Image(
            bitmap = iconBitmap,
            contentDescription = result.title,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(cornerRadius))
        )
    } else {
        val (iconVector, iconColor) = when (result.actionCommand) {
            is Command.Help -> Icons.AutoMirrored.Filled.Help to MaterialTheme.colorScheme.primary
            is Command.GoogleMaps -> Icons.Default.Map to KernGreen
            is Command.YoutubeSearch -> Icons.Default.VideoLibrary to KernYellow
            is Command.Timer -> Icons.Default.Timer to MaterialTheme.colorScheme.primary
            is Command.OpenSettings -> Icons.Default.Settings to MaterialTheme.colorScheme.primary
            is Command.CustomAlias -> Icons.Default.Code to KernGreen
            else -> when (result.type) {
                SearchResultType.HISTORY -> Icons.Default.History to KernTextMuted
                else -> Icons.Default.Search to KernTextSecondary
            }
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(if (sharpCorners) RoundedCornerShape(0.dp) else CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
