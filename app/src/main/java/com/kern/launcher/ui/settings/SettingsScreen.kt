package com.kern.launcher.ui.settings

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AlignHorizontalLeft
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kern.launcher.ui.components.AliasManagerView
import com.kern.launcher.ui.components.AppPickerDialog
import com.kern.launcher.ui.components.HelpDialog
import com.kern.launcher.ui.components.HiddenAppsManagerView
import com.kern.launcher.ui.theme.CyberpunkAccent
import com.kern.launcher.ui.theme.CyberpunkBg
import com.kern.launcher.ui.theme.DraculaAccent
import com.kern.launcher.ui.theme.DraculaBg
import com.kern.launcher.ui.theme.GruvboxAccent
import com.kern.launcher.ui.theme.GruvboxBg
import com.kern.launcher.ui.theme.KernDarkSurfaceBorder
import com.kern.launcher.ui.theme.KernRed
import com.kern.launcher.ui.theme.KernTextSecondary
import com.kern.launcher.ui.theme.MonokaiAccent
import com.kern.launcher.ui.theme.MonokaiBg
import com.kern.launcher.ui.theme.NordAccent
import com.kern.launcher.ui.theme.NordBg
import com.kern.launcher.ui.theme.OledMonoAccent
import com.kern.launcher.ui.theme.OledMonoBg
import com.kern.launcher.ui.theme.OneDarkAccent
import com.kern.launcher.ui.theme.OneDarkBg
import com.kern.launcher.ui.theme.TokyoNightAccent
import com.kern.launcher.ui.theme.TokyoNightBg
import com.kern.launcher.ui.theme.VsCodeDarkAccent
import com.kern.launcher.ui.theme.VsCodeDarkBg
import com.kern.launcher.ui.theme.parseHexColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val aliases by viewModel.aliases.collectAsState()
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()

    val context = LocalContext.current
    val cardCorner = if (userSettings.sharpCorners) 0.dp else 12.dp

    var showHelpDialog by remember { mutableStateOf(false) }
    var pickingGestureSide by remember { mutableStateOf<String?>(null) }

    if (showHelpDialog) {
        HelpDialog(
            aliases = aliases,
            sharpCorners = userSettings.sharpCorners,
            onDismiss = { showHelpDialog = false }
        )
    }

    if (pickingGestureSide != null) {
        val isLeft = pickingGestureSide == "LEFT"
        AppPickerDialog(
            title = if (isLeft) "Select Swipe Left Shortcut App" else "Select Swipe Right Shortcut App",
            apps = installedApps,
            sharpCorners = userSettings.sharpCorners,
            onAppSelected = { pkg ->
                if (isLeft) viewModel.setSwipeLeftPackage(pkg)
                else viewModel.setSwipeRightPackage(pkg)
            },
            onClear = {
                if (isLeft) viewModel.setSwipeLeftPackage("")
                else viewModel.setSwipeRightPackage("")
            },
            onDismiss = { pickingGestureSide = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kern Settings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Default Launcher Setting Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Set Kern as Default Launcher",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Choose Kern as your primary home app",
                            style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary, fontSize = 12.sp)
                        )
                    }
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(if (userSettings.sharpCorners) 0.dp else 8.dp)
                    ) {
                        Text("Set Default", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Command Help Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Command Manual & Help",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "View command syntax, shortcuts & aliases",
                            style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary, fontSize = 12.sp)
                        )
                    }
                    Button(
                        onClick = { showHelpDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(if (userSettings.sharpCorners) 0.dp else 8.dp)
                    ) {
                        Text("Open Manual", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Provider Selector Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Command Provider ('ai <prompt>')",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Choose which AI app opens when typing 'ai <prompt>'",
                        style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary, fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FontOptionChip("ChatGPT (Default)", userSettings.aiProvider == "CHATGPT", userSettings.sharpCorners, { viewModel.setAiProvider("CHATGPT") })
                        FontOptionChip("Google Gemini", userSettings.aiProvider == "GEMINI", userSettings.sharpCorners, { viewModel.setAiProvider("GEMINI") })
                        FontOptionChip("Perplexity AI", userSettings.aiProvider == "PERPLEXITY", userSettings.sharpCorners, { viewModel.setAiProvider("PERPLEXITY") })
                        FontOptionChip("Claude AI", userSettings.aiProvider == "CLAUDE", userSettings.sharpCorners, { viewModel.setAiProvider("CLAUDE") })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Palette Theme Picker Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Color Palettes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val customBgParsed = parseHexColor(userSettings.customBgColor, Color(0xFF0D1117))
                    val customAccentParsed = parseHexColor(userSettings.customAccentColor, Color(0xFF58A6FF))

                    val palettes = listOf(
                        Triple("VS_CODE_DARK", "VS Code Dark+", VsCodeDarkBg to VsCodeDarkAccent),
                        Triple("OLED_MONOCHROME", "OLED Mono", OledMonoBg to OledMonoAccent),
                        Triple("DRACULA", "Dracula", DraculaBg to DraculaAccent),
                        Triple("MONOKAI", "Monokai Pro", MonokaiBg to MonokaiAccent),
                        Triple("ONE_DARK", "One Dark", OneDarkBg to OneDarkAccent),
                        Triple("TOKYO_NIGHT", "Tokyo Night", TokyoNightBg to TokyoNightAccent),
                        Triple("GRUVBOX", "Gruvbox", GruvboxBg to GruvboxAccent),
                        Triple("NORD", "Nord", NordBg to NordAccent),
                        Triple("CYBERPUNK", "Cyberpunk", CyberpunkBg to CyberpunkAccent),
                        Triple("CUSTOM", "Custom Theme", customBgParsed to customAccentParsed)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        palettes.forEach { (id, name, colors) ->
                            val isSelected = userSettings.themePalette.equals(id, ignoreCase = true)
                            ThemeChip(
                                label = name,
                                bg = colors.first,
                                accent = colors.second,
                                isSelected = isSelected,
                                sharpCorners = userSettings.sharpCorners,
                                onClick = { viewModel.setThemePalette(id) }
                            )
                        }
                    }
                }
            }

            // Custom Theme Builder Card (Visible when CUSTOM palette is active)
            if (userSettings.themePalette.equals("CUSTOM", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(cardCorner)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ColorLens, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Custom Theme Color Picker",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. Background Color Picker
                        ColorPickerSection(
                            title = "Background Color",
                            currentColorHex = userSettings.customBgColor,
                            quickPresets = listOf("#000000", "#0D1117", "#181825", "#1E1E1E", "#11001C", "#0F172A"),
                            sharpCorners = userSettings.sharpCorners,
                            onColorChanged = { viewModel.setCustomBgColor(it) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Primary Accent Color Picker
                        ColorPickerSection(
                            title = "Primary Accent Color",
                            currentColorHex = userSettings.customAccentColor,
                            quickPresets = listOf("#00FF66", "#58A6FF", "#00E5FF", "#FF79C6", "#FF9900", "#FFD700"),
                            sharpCorners = userSettings.sharpCorners,
                            onColorChanged = { viewModel.setCustomAccentColor(it) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. Text Color Picker
                        ColorPickerSection(
                            title = "Text / Font Color",
                            currentColorHex = userSettings.customTextColor,
                            quickPresets = listOf("#FFFFFF", "#C9D1D9", "#80D4FF", "#FFB703", "#F8F8F2", "#A6ADC8"),
                            sharpCorners = userSettings.sharpCorners,
                            onColorChanged = { viewModel.setCustomTextColor(it) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Normal Mode Layout & Alignments Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AlignHorizontalLeft, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clock & App List Alignment (Normal Mode)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Clock Alignment", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FontOptionChip("Left", userSettings.clockAlignment == "LEFT", userSettings.sharpCorners, { viewModel.setClockAlignment("LEFT") }, Modifier.weight(1f))
                        FontOptionChip("Center", userSettings.clockAlignment == "CENTER", userSettings.sharpCorners, { viewModel.setClockAlignment("CENTER") }, Modifier.weight(1f))
                        FontOptionChip("Right", userSettings.clockAlignment == "RIGHT", userSettings.sharpCorners, { viewModel.setClockAlignment("RIGHT") }, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("App List Alignment", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FontOptionChip("Left", userSettings.appListAlignment == "LEFT", userSettings.sharpCorners, { viewModel.setAppListAlignment("LEFT") }, Modifier.weight(1f))
                        FontOptionChip("Center", userSettings.appListAlignment == "CENTER", userSettings.sharpCorners, { viewModel.setAppListAlignment("CENTER") }, Modifier.weight(1f))
                        FontOptionChip("Right", userSettings.appListAlignment == "RIGHT", userSettings.sharpCorners, { viewModel.setAppListAlignment("RIGHT") }, Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Font Sizes Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FormatSize, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Font Sizes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Clock Font Size", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FontOptionChip("Small (48sp)", userSettings.clockFontSize == "SMALL", userSettings.sharpCorners, { viewModel.setClockFontSize("SMALL") }, Modifier.weight(1f))
                        FontOptionChip("Medium (72sp)", userSettings.clockFontSize == "MEDIUM", userSettings.sharpCorners, { viewModel.setClockFontSize("MEDIUM") }, Modifier.weight(1f))
                        FontOptionChip("Large (96sp)", userSettings.clockFontSize == "LARGE", userSettings.sharpCorners, { viewModel.setClockFontSize("LARGE") }, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("App List Font Size", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FontOptionChip("Small (13sp)", userSettings.appListFontSize == "SMALL", userSettings.sharpCorners, { viewModel.setAppListFontSize("SMALL") }, Modifier.weight(1f))
                        FontOptionChip("Medium (15sp)", userSettings.appListFontSize == "MEDIUM", userSettings.sharpCorners, { viewModel.setAppListFontSize("MEDIUM") }, Modifier.weight(1f))
                        FontOptionChip("Large (18sp)", userSettings.appListFontSize == "LARGE", userSettings.sharpCorners, { viewModel.setAppListFontSize("LARGE") }, Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Swipe Gestures Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Gesture, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Swipe Gesture Shortcuts",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val leftApp = installedApps.find { it.packageName == userSettings.swipeLeftPackage }
                    val rightApp = installedApps.find { it.packageName == userSettings.swipeRightPackage }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Swipe Left App Shortcut", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                text = leftApp?.label ?: if (userSettings.swipeLeftPackage.isBlank()) "None (Tap to set)" else userSettings.swipeLeftPackage,
                                style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary, fontSize = 12.sp)
                            )
                        }
                        Button(
                            onClick = { pickingGestureSide = "LEFT" },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(if (userSettings.sharpCorners) 0.dp else 8.dp)
                        ) {
                            Text("Select App", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Swipe Right App Shortcut", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                text = rightApp?.label ?: if (userSettings.swipeRightPackage.isBlank()) "None (Tap to set)" else userSettings.swipeRightPackage,
                                style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary, fontSize = 12.sp)
                            )
                        }
                        Button(
                            onClick = { pickingGestureSide = "RIGHT" },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(if (userSettings.sharpCorners) 0.dp else 8.dp)
                        ) {
                            Text("Select App", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Font Selector Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.TextFields, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Font Family Style",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FontOptionChip("JetBrains Mono (Raw)", userSettings.fontStyle == "MONOSPACE", userSettings.sharpCorners, { viewModel.setFontStyle("MONOSPACE") })
                        FontOptionChip("Sans Serif", userSettings.fontStyle == "SANS_SERIF", userSettings.sharpCorners, { viewModel.setFontStyle("SANS_SERIF") })
                        FontOptionChip("Serif", userSettings.fontStyle == "SERIF", userSettings.sharpCorners, { viewModel.setFontStyle("SERIF") })
                        FontOptionChip("Cursive / Script", userSettings.fontStyle == "CURSIVE", userSettings.sharpCorners, { viewModel.setFontStyle("CURSIVE") })
                        FontOptionChip("System Default", userSettings.fontStyle == "DEFAULT", userSettings.sharpCorners, { viewModel.setFontStyle("DEFAULT") })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Toggles Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Raw UI & Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SettingToggleItem(
                        title = "Transparent Background (See Wallpaper)",
                        subtitle = "Allow device wallpaper to show through launcher background",
                        checked = userSettings.isTransparentBg,
                        onCheckedChange = { viewModel.toggleTransparentBg(it) }
                    )

                    SettingToggleItem(
                        title = "Show App List Square Outlines",
                        subtitle = "Display border lines around app search result items",
                        checked = userSettings.showAppListOutlines,
                        onCheckedChange = { viewModel.toggleShowAppListOutlines(it) }
                    )

                    SettingToggleItem(
                        title = "Experimental: TUI View Mode",
                        subtitle = "ASCII retro terminal UI with framed window boxes",
                        checked = userSettings.tuiViewMode,
                        onCheckedChange = { viewModel.toggleTuiViewMode(it) }
                    )

                    SettingToggleItem(
                        title = "Auto-launch Single Match App",
                        subtitle = "Open application immediately when only 1 match remains",
                        checked = userSettings.autoLaunchSingleMatch,
                        onCheckedChange = { viewModel.toggleAutoLaunchSingleMatch(it) }
                    )

                    SettingToggleItem(
                        title = "Sharp Corners (No Smooth Angle)",
                        subtitle = "Square 0.dp box corners without rounding",
                        checked = userSettings.sharpCorners,
                        onCheckedChange = { viewModel.toggleSharpCorners(it) }
                    )

                    SettingToggleItem(
                        title = "Show App Icons",
                        subtitle = "Disable for pure text minimalist mode",
                        checked = userSettings.showAppIcons,
                        onCheckedChange = { viewModel.toggleShowAppIcons(it) }
                    )

                    SettingToggleItem(
                        title = "Dark Mode",
                        subtitle = "Use dark color scheme",
                        checked = userSettings.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )

                    SettingToggleItem(
                        title = "24-Hour Clock Format",
                        subtitle = "Use 24-hour time format (e.g., 21:30)",
                        checked = userSettings.clockFormat24h,
                        onCheckedChange = { viewModel.toggleClock24h(it) }
                    )

                    SettingToggleItem(
                        title = "Show Date",
                        subtitle = "Display day and date on home screen",
                        checked = userSettings.showDate,
                        onCheckedChange = { viewModel.toggleShowDate(it) }
                    )

                    SettingToggleItem(
                        title = "Auto Focus Keyboard",
                        subtitle = "Automatically open keyboard on home screen",
                        checked = userSettings.autoFocusKeyboard,
                        onCheckedChange = { viewModel.toggleAutoFocusKeyboard(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hidden Apps Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                HiddenAppsManagerView(
                    hiddenApps = hiddenApps,
                    onUnhideApp = { pkg -> viewModel.unhideApp(pkg) },
                    sharpCorners = userSettings.sharpCorners,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Aliases Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                AliasManagerView(
                    aliases = aliases,
                    onAddAlias = { alias, target -> viewModel.addAlias(alias, target) },
                    onDeleteAlias = { alias -> viewModel.deleteAlias(alias) },
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Clear History Button
            OutlinedButton(
                onClick = { viewModel.clearHistory() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KernRed),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = KernRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Command History", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerSection(
    title: String,
    currentColorHex: String,
    quickPresets: List<String>,
    sharpCorners: Boolean,
    onColorChanged: (String) -> Unit
) {
    var textValue by remember(currentColorHex) { mutableStateOf(currentColorHex) }
    val parsedColor = parseHexColor(textValue, Color.Gray)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(if (sharpCorners) 0.dp else 8.dp))
                    .background(parsedColor)
                    .border(1.dp, KernDarkSurfaceBorder, RoundedCornerShape(if (sharpCorners) 0.dp else 8.dp))
            )

            OutlinedTextField(
                value = textValue,
                onValueChange = { input ->
                    textValue = input
                    if (input.startsWith("#") && (input.length == 7 || input.length == 9)) {
                        onColorChanged(input)
                    }
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(if (sharpCorners) 0.dp else 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = KernDarkSurfaceBorder
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickPresets.forEach { hex ->
                val swatchColor = parseHexColor(hex, Color.Black)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(swatchColor)
                        .border(if (textValue.equals(hex, ignoreCase = true)) 2.dp else 1.dp, if (textValue.equals(hex, ignoreCase = true)) MaterialTheme.colorScheme.primary else KernDarkSurfaceBorder, CircleShape)
                        .clickable {
                            textValue = hex
                            onColorChanged(hex)
                        }
                )
            }
        }
    }
}

@Composable
fun ThemeChip(
    label: String,
    bg: Color,
    accent: Color,
    isSelected: Boolean,
    sharpCorners: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) accent else KernDarkSurfaceBorder
    val shape = RoundedCornerShape(if (sharpCorners) 0.dp else 8.dp)

    Row(
        modifier = Modifier
            .clip(shape)
            .background(bg)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = Color.White,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun FontOptionChip(
    label: String,
    isSelected: Boolean,
    sharpCorners: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else KernDarkSurfaceBorder
    val shape = RoundedCornerShape(if (sharpCorners) 0.dp else 8.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
            .border(1.dp, borderColor, shape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun SettingToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary, fontSize = 12.sp)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.background,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
