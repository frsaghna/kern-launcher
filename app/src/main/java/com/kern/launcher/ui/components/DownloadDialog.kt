package com.kern.launcher.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kern.launcher.service.intent.IntentFactory
import com.kern.launcher.ui.theme.KernDarkSurfaceBorder
import com.kern.launcher.ui.theme.KernGreen
import com.kern.launcher.ui.theme.KernTextMuted
import com.kern.launcher.ui.theme.KernTextSecondary
import com.kern.launcher.ui.theme.KernYellow

private data class PlatformInfo(
    val name: String,
    val badgeColor: Color
)

private fun getPlatformInfo(url: String): PlatformInfo {
    val lower = url.lowercase()
    return when {
        lower.contains("youtube.com") || lower.contains("youtu.be") -> PlatformInfo("YOUTUBE", Color(0xFFFF4444))
        lower.contains("tiktok.com") -> PlatformInfo("TIKTOK", Color(0xFF00F2FE))
        lower.contains("instagram.com") -> PlatformInfo("INSTAGRAM", Color(0xFFE1306C))
        lower.contains("twitter.com") || lower.contains("x.com") -> PlatformInfo("X / TWITTER", Color(0xFF1DA1F2))
        lower.contains("reddit.com") || lower.contains("v.redd.it") -> PlatformInfo("REDDIT", Color(0xFFFF5700))
        lower.contains("facebook.com") || lower.contains("fb.watch") -> PlatformInfo("FACEBOOK", Color(0xFF1877F2))
        lower.contains("soundcloud.com") -> PlatformInfo("SOUNDCLOUD", Color(0xFFFF7700))
        lower.contains("twitch.tv") -> PlatformInfo("TWITCH", Color(0xFF9146FF))
        lower.contains("bilibili.com") || lower.contains("bilibili.tv") -> PlatformInfo("BILIBILI", Color(0xFF00A1D6))
        lower.contains("vimeo.com") -> PlatformInfo("VIMEO", Color(0xFF1AB7EA))
        lower.contains("pinterest.com") -> PlatformInfo("PINTEREST", Color(0xFFBD081C))
        lower.startsWith("http://") || lower.startsWith("https://") -> PlatformInfo("WEB MEDIA", KernGreen)
        else -> PlatformInfo("LINK", KernTextSecondary)
    }
}

@Composable
fun DownloadDialog(
    initialUrl: String,
    sharpCorners: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val cornerRadius = if (sharpCorners) 0.dp else 12.dp

    var urlInput by remember { mutableStateOf(initialUrl.trim()) }
    val clipboardText = remember {
        clipboardManager.getText()?.text?.trim() ?: ""
    }

    // Auto-prefill from clipboard if initialUrl is blank and clipboard contains a valid URL
    LaunchedEffect(Unit) {
        if (urlInput.isBlank() && (clipboardText.startsWith("http://") || clipboardText.startsWith("https://"))) {
            urlInput = clipboardText
        }
    }

    val isSealInstalled = remember { IntentFactory.isPackageInstalled(context, IntentFactory.PACKAGE_SEAL) }
    val isYtdlnisInstalled = remember { IntentFactory.isPackageInstalled(context, IntentFactory.PACKAGE_YTDLNIS) }
    val isNewPipeInstalled = remember {
        IntentFactory.isPackageInstalled(context, IntentFactory.PACKAGE_NEWPIPE) ||
                IntentFactory.isPackageInstalled(context, IntentFactory.PACKAGE_NEWPIPE_LEGACY)
    }

    val platformInfo = getPlatformInfo(urlInput)
    val hasValidUrl = urlInput.isNotBlank()

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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YT-DLP COMPANION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace
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

                Spacer(modifier = Modifier.height(10.dp))

                // Platform Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TARGET SOURCE:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = KernTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(if (sharpCorners) 0.dp else 4.dp))
                            .background(platformInfo.badgeColor.copy(alpha = 0.18f))
                            .border(1.dp, platformInfo.badgeColor.copy(alpha = 0.6f), RoundedCornerShape(if (sharpCorners) 0.dp else 4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "[ ${platformInfo.name} ]",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = platformInfo.badgeColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // URL Input Field Box
                Text(
                    text = "MEDIA URL:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(if (sharpCorners) 0.dp else 6.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, KernDarkSurfaceBorder, RoundedCornerShape(if (sharpCorners) 0.dp else 6.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BasicTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it.trim() },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (urlInput.isEmpty()) {
                                    Text(
                                        text = "Paste or type media link...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = KernTextMuted,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        )

                        if (urlInput.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = KernTextSecondary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { urlInput = "" }
                            )
                        }
                    }
                }

                // Quick Paste from Clipboard button if clipboard has link
                if (clipboardText.isNotBlank() && clipboardText != urlInput &&
                    (clipboardText.startsWith("http://") || clipboardText.startsWith("https://"))
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { urlInput = clipboardText }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste",
                            tint = KernYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Paste from clipboard: ${clipboardText.take(38)}...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KernYellow,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Downloader Actions Section
                Text(
                    text = "DOWNLOAD ENGINES & ACTIONS:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 1. Seal Client Option
                if (isSealInstalled) {
                    DownloaderActionCard(
                        title = "DOWNLOAD WITH SEAL",
                        subtitle = "Material 3 yt-dlp client • Format picker & muxing",
                        icon = Icons.Default.RocketLaunch,
                        accentColor = MaterialTheme.colorScheme.primary,
                        sharpCorners = sharpCorners,
                        badge = "INSTALLED",
                        onClick = {
                            if (hasValidUrl) {
                                try {
                                    val intent = IntentFactory.createShareTextIntent(IntentFactory.PACKAGE_SEAL, urlInput)
                                    context.startActivity(intent)
                                    onDismiss()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not launch Seal", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 2. YTDLnis Client Option
                if (isYtdlnisInstalled) {
                    DownloaderActionCard(
                        title = "DOWNLOAD WITH YTDLNIS",
                        subtitle = "Advanced yt-dlp client • Terminal args & batch mode",
                        icon = Icons.Default.Terminal,
                        accentColor = KernYellow,
                        sharpCorners = sharpCorners,
                        badge = "INSTALLED",
                        onClick = {
                            if (hasValidUrl) {
                                try {
                                    val intent = IntentFactory.createShareTextIntent(IntentFactory.PACKAGE_YTDLNIS, urlInput)
                                    context.startActivity(intent)
                                    onDismiss()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not launch YTDLnis", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 3. NewPipe Option
                if (isNewPipeInstalled) {
                    val newPipePkg = if (IntentFactory.isPackageInstalled(context, IntentFactory.PACKAGE_NEWPIPE)) {
                        IntentFactory.PACKAGE_NEWPIPE
                    } else {
                        IntentFactory.PACKAGE_NEWPIPE_LEGACY
                    }
                    DownloaderActionCard(
                        title = "OPEN IN NEWPIPE",
                        subtitle = "Stream or download media offline",
                        icon = Icons.Default.PlayArrow,
                        accentColor = KernGreen,
                        sharpCorners = sharpCorners,
                        badge = "INSTALLED",
                        onClick = {
                            if (hasValidUrl) {
                                try {
                                    val intent = IntentFactory.createOpenUrlIntent(urlInput).apply {
                                        setPackage(newPipePkg)
                                    }
                                    context.startActivity(intent)
                                    onDismiss()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not launch NewPipe", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 4. Fallback / Cobalt Web Downloader Option
                DownloaderActionCard(
                    title = "DOWNLOAD VIA COBALT (WEB)",
                    subtitle = "Fast, ad-free open-source downloader • 30+ sites",
                    icon = Icons.Default.Download,
                    accentColor = if (!isSealInstalled && !isYtdlnisInstalled) MaterialTheme.colorScheme.primary else KernTextSecondary,
                    sharpCorners = sharpCorners,
                    badge = if (!isSealInstalled && !isYtdlnisInstalled) "ONLINE" else null,
                    onClick = {
                        if (hasValidUrl) {
                            // Copy link to clipboard for convenience and open Cobalt
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Media URL", urlInput))
                            Toast.makeText(context, "URL copied! Opening Cobalt...", Toast.LENGTH_SHORT).show()
                            val intent = IntentFactory.createOpenUrlIntent(IntentFactory.URL_COBALT_WEB)
                            context.startActivity(intent)
                            onDismiss()
                        } else {
                            val intent = IntentFactory.createOpenUrlIntent(IntentFactory.URL_COBALT_WEB)
                            context.startActivity(intent)
                            onDismiss()
                        }
                    }
                )

                // If no native yt-dlp app is installed, provide 1-tap install helper
                if (!isSealInstalled && !isYtdlnisInstalled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(if (sharpCorners) 0.dp else 6.dp))
                            .background(KernYellow.copy(alpha = 0.1f))
                            .border(1.dp, KernYellow.copy(alpha = 0.4f), RoundedCornerShape(if (sharpCorners) 0.dp else 6.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Tip: Install Seal for seamless offline yt-dlp downloads on Android.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = KernYellow,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        context.startActivity(IntentFactory.createOpenUrlIntent(IntentFactory.URL_SEAL_GITHUB))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = KernYellow),
                                    shape = RoundedCornerShape(if (sharpCorners) 0.dp else 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("GET SEAL", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        context.startActivity(IntentFactory.createOpenUrlIntent(IntentFactory.URL_YTDLNIS_GITHUB))
                                    },
                                    shape = RoundedCornerShape(if (sharpCorners) 0.dp else 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("GET YTDLNIS", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Utilities Row: Copy Link & Open in Browser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (hasValidUrl) {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Media URL", urlInput))
                                Toast.makeText(context, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "No URL to copy", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(if (sharpCorners) 0.dp else 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("COPY", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }

                    OutlinedButton(
                        onClick = {
                            if (hasValidUrl) {
                                try {
                                    context.startActivity(IntentFactory.createOpenUrlIntent(urlInput))
                                    onDismiss()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(if (sharpCorners) 0.dp else 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BROWSER", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloaderActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    sharpCorners: Boolean,
    badge: String? = null,
    onClick: () -> Unit
) {
    val cornerRadius = if (sharpCorners) 0.dp else 8.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(cornerRadius))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(if (sharpCorners) 0.dp else 6.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (badge != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(if (sharpCorners) 0.dp else 3.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = KernTextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}
