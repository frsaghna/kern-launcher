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
import com.kern.launcher.model.Alias
import com.kern.launcher.ui.theme.KernTextSecondary

@Composable
fun HelpDialog(
    aliases: List<Alias>,
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
                        text = "KERN MANUAL v1.0",
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
                    text = "BUILT-IN AI & DEEP SEARCH COMMANDS:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                HelpItem("help / ?", "Display this manual")
                HelpItem("ai / gpt <prompt>", "Ask AI provider (e.g. ai jelaskan relativitas)")
                HelpItem("gemini <prompt>", "Ask Google Gemini directly")
                HelpItem("log <text>", "Send text to LazyLogs (e.g. log makan siang 25k)")
                HelpItem("<scheme>://...", "Direct execute any custom App Deep Link URI")
                HelpItem("spot / music <query>", "Search music on Spotify app")
                HelpItem("play / store <query>", "Search apps on Play Store")
                HelpItem("yt <query>", "Search videos on YouTube app")
                HelpItem("gh <query>", "Search repositories on GitHub")
                HelpItem("wiki <query>", "Search articles on Wikipedia")
                HelpItem("reddit / r <query>", "Search posts/subreddits on Reddit")
                HelpItem("x / tw <query>", "Search tweets on X / Twitter")
                HelpItem("ddg <query>", "Search privacy web on DuckDuckGo")
                HelpItem("g <query>", "Search web on Google Search")
                HelpItem("maps <location>", "Search location on Google Maps")
                HelpItem("info <app>", "Open system settings for app")
                HelpItem("hide <app>", "Hide app from launcher search")
                HelpItem("unhide <app>", "Restore hidden app to search")
                HelpItem("hidden / secret", "View and manage all hidden applications")
                HelpItem("tui / tuiview", "Toggle experimental retro TUI Mode")
                HelpItem("calc <expr>", "Evaluate math expression (e.g., 15*12)")
                HelpItem("timer <duration>", "Set timer alarm (e.g., timer 20m, 30s)")
                HelpItem("settings", "Open launcher settings screen")

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ACTIVE CUSTOM ALIASES:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (aliases.isEmpty()) {
                    Text(
                        text = "No custom aliases created yet. Add them in Settings.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary, fontSize = 12.sp)
                    )
                } else {
                    aliases.forEach { alias ->
                        HelpItem(alias.alias, "➔ ${alias.targetCommandOrPackage}")
                    }
                }

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
