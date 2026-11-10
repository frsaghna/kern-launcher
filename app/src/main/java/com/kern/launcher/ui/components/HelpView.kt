package com.kern.launcher.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kern.launcher.ui.theme.KernTextSecondary

@Composable
fun HelpDialog(
    sharpCorners: Boolean,
    onDismiss: () -> Unit
) {
    val cornerRadius = if (sharpCorners) 0.dp else 12.dp

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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KERN MANUAL v1.0.3",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp
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

                Text(
                    text = "LAUNCHER CONFIGURATION & SYSTEM:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                HelpItem("pref / conf", "Open launcher preferences (configure themes, fonts, clock, & gestures)")
                HelpItem("? / help", "Display this help manual")
                HelpItem("set <query>", "Search 25+ Android System Settings (e.g. set wifi, set battery, set dns)")
                HelpItem("tui", "Toggle experimental retro TUI Mode")
                HelpItem("info <app>", "Open system settings page for an app")
                HelpItem("hide <app>", "Hide app from launcher search results")
                HelpItem("unhide <app>", "Restore hidden app back to search results")
                HelpItem("hidden", "View and manage all hidden applications")

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SEARCH & DEEP LINK COMMANDS:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                HelpItem("ai <prompt>", "Ask AI provider (e.g. ai explain relativity)")
                HelpItem("dl <link>", "Open yt-dlp downloader companion (YTDLnis / Cobalt)")
                HelpItem("map <location>", "Search location or directions on Google Maps")
                HelpItem("timer <duration>", "Set countdown timer alarm (e.g., timer 20m, 30s)")
                HelpItem("log <text>", "Send text note to LazyLogs app")
                HelpItem("<scheme>://...", "Direct execute any custom App Deep Link URI")
                HelpItem("g <query>", "Search on Google Search app")
                HelpItem("yt <query>", "Search videos on YouTube app")
                HelpItem("spot <query>", "Search music on Spotify app")
                HelpItem("ps <query>", "Search apps on Play Store")
                HelpItem("gh <query>", "Search repositories on GitHub")
                HelpItem("wiki <query>", "Search articles on Wikipedia")
                HelpItem("r <query>", "Search posts/subreddits on Reddit")
                HelpItem("x <query>", "Search tweets on X / Twitter")

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "KEYBOARD & GESTURE SHORTCUTS:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                HelpItem("Enter / Return", "Execute selected item or command")
                HelpItem("Up / Down Arrow", "Navigate command history")
                HelpItem("Swipe Left / Right", "Launch user assigned app shortcuts")
                HelpItem("Tap Clock / Date", "Open System Clock / Calendar apps")

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(cornerRadius),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLOSE MANUAL", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HelpItem(command: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = command,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall.copy(
                color = KernTextSecondary,
                fontSize = 12.sp
            )
        )
    }
}
