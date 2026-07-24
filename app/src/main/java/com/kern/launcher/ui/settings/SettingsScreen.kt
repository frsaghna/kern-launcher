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
import androidx.compose.foundation.layout.imePadding
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
import com.kern.launcher.model.Alias
import com.kern.launcher.ui.components.AppPickerDialog
import com.kern.launcher.ui.components.ColorPickerDialog
import com.kern.launcher.ui.components.HelpDialog
import com.kern.launcher.ui.theme.KernDarkSurfaceBorder
import com.kern.launcher.ui.theme.KernRed
import com.kern.launcher.ui.theme.KernTextSecondary
import com.kern.launcher.ui.theme.MONKEYTYPE_PALETTES
import com.kern.launcher.ui.theme.parseHexColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val aliases by viewModel.aliases.collectAsState()
    val context = LocalContext.current

    var newAliasName by remember { mutableStateOf("") }
    var newAliasTarget by remember { mutableStateOf("") }
    var showHelpDialog by remember { mutableStateOf(false) }
    var themeSearchQuery by remember { mutableStateOf("") }

    // State for AppPickerDialog for Gestures
    var pickingGestureSide by remember { mutableStateOf<String?>(null) } // "LEFT" or "RIGHT"

    val cardCorner = if (userSettings.sharpCorners) 0.dp else 12.dp

    val customBgParsed = parseHexColor(userSettings.customBgColor, Color(0xFF181825))
    val customAccentParsed = parseHexColor(userSettings.customAccentColor, Color(0xFF00FF66))

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
                        text = "KERN SETTINGS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Manual",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // General Launcher Preferences Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cardCorner)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Launcher & System Defaults",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = "Set Kern as Default Home Launcher",
                        subtitle = "Open Android Settings to set Kern as default launcher",
                        checked = false,
                        showSwitch = false,
                        onClick = {
                            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }
                    )

                    SettingToggleRow(
                        title = "Auto-launch Single Search Match",
                        subtitle = "Open application immediately when search leaves only 1 result",
                        checked = userSettings.autoLaunchSingleMatch,
                        onCheckedChange = { viewModel.toggleAutoLaunchSingleMatch(it) }
                    )

                    SettingToggleRow(
                        title = "Auto-Focus Search Keyboard",
                        subtitle = "Open soft keyboard immediately when returning to Home screen",
                        checked = userSettings.autoFocusKeyboard,
                        onCheckedChange = { viewModel.toggleAutoFocusKeyboard(it) }
                    )

                    SettingToggleRow(
                        title = "Show App Icons in Search List",
                        subtitle = "Display app launcher icons next to search result text",
                        checked = userSettings.showAppIcons,
                        onCheckedChange = { viewModel.toggleShowAppIcons(it) }
                    )

                    SettingToggleRow(
                        title = "Show List & Component Outlines",
                        subtitle = "Render borders around search items & command input box",
                        checked = userSettings.showAppListOutlines,
                        onCheckedChange = { viewModel.toggleShowAppListOutlines(it) }
                    )

                    SettingToggleRow(
                        title = "Wallpaper Passthrough (Transparent BG)",
                        subtitle = "Make launcher background semi-transparent to view your system wallpaper",
                        checked = userSettings.isTransparentBg,
                        onCheckedChange = { viewModel.toggleTransparentBg(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Search Provider Preference Card
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
                        text = "Choose which AI assistant app/service is launched when using the 'ai' command:",
                        style = MaterialTheme.typography.labelSmall.copy(color = KernTextSecondary, fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val aiProviders = listOf(
                        "CHATGPT" to "ChatGPT (OpenAI)",
                        "GEMINI" to "Google Gemini",
                        "PERPLEXITY" to "Perplexity AI",
                        "CLAUDE" to "Claude (Anthropic)"
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        aiProviders.forEach { (code, label) ->
                            val isSelected = userSettings.aiProvider.equals(code, ignoreCase = true)
                            val chipBg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            val chipTextColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(if (userSettings.sharpCorners) 0.dp else 8.dp))
                                    .background(chipBg)
                                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else KernDarkSurfaceBorder, RoundedCornerShape(if (userSettings.sharpCorners) 0.dp else 8.dp))
                                    .clickable { viewModel.setAiProvider(code) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = chipTextColor,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Theme & Color Palette Selector Card
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
                            text = "Themes (${MONKEYTYPE_PALETTES.size + 1})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = themeSearchQuery,
                        onValueChange = { themeSearchQuery = it },
                        placeholder = { Text("Filter theme (e.g. serika, botanical, matrix)...", color = KernTextSecondary, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(if (userSettings.sharpCorners) 0.dp else 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = KernDarkSurfaceBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val allPalettes = listOf("CUSTOM" to (customBgParsed to customAccentParsed)) +
                            MONKEYTYPE_PALETTES.map { (code, palette) -> code to (palette.bg to palette.accent) }

                    val filteredPalettes = if (themeSearchQuery.isBlank()) {
                        allPalettes
                    } else {
                        allPalettes.filter { (code, _) ->
                            code.lowercase().contains(themeSearchQuery.trim().lowercase()) ||
                                    code.replace("_", " ").lowercase().contains(themeSearchQuery.trim().lowercase())
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredPalettes.forEach { (code, colors) ->
                            val isSelected = userSettings.themePalette.equals(code, ignoreCase = true)
                            val labelName = code.replace("_", " ").lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                            ThemeChip(
                                label = labelName,
                                bg = colors.first,
                                accent = colors.second,
                                isSelected = isSelected,
                                sharpCorners = userSettings.sharpCorners,
                                onClick = { viewModel.setThemePalette(code) }
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
                            text = "Normal Mode Layout & Alignment",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Clock Header Alignment", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OptionSelectorRow(
                        options = listOf("LEFT", "CENTER", "RIGHT"),
                        currentSelected = userSettings.clockAlignment,
                        sharpCorners = userSettings.sharpCorners,
                        onSelect = { viewModel.setClockAlignment(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "App Search List Alignment", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OptionSelectorRow(
                        options = listOf("LEFT", "CENTER", "RIGHT"),
                        currentSelected = userSettings.appListAlignment,
                        sharpCorners = userSettings.sharpCorners,
                        onSelect = { viewModel.setAppListAlignment(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Font & Typography Preferences Card
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
                            text = "Typography & Font Sizes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Font Family Style", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OptionSelectorRow(
                        options = listOf("MONOSPACE", "SANS_SERIF", "SERIF", "CURSIVE", "DEFAULT"),
                        currentSelected = userSettings.fontStyle,
                        sharpCorners = userSettings.sharpCorners,
                        onSelect = { viewModel.setFontStyle(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Clock Display Size", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OptionSelectorRow(
                        options = listOf("SMALL", "MEDIUM", "LARGE"),
                        currentSelected = userSettings.clockFontSize,
                        sharpCorners = userSettings.sharpCorners,
                        onSelect = { viewModel.setClockFontSize(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "App List Font Size", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OptionSelectorRow(
                        options = listOf("SMALL", "MEDIUM", "LARGE"),
                        currentSelected = userSettings.appListFontSize,
                        sharpCorners = userSettings.sharpCorners,
                        onSelect = { viewModel.setAppListFontSize(it) }
                    )

                    SettingToggleRow(
                        title = "Sharp 0.dp Corners (Terminal Aesthetic)",
                        subtitle = "Remove rounded corners from buttons, cards, and dialogs",
                        checked = userSettings.sharpCorners,
                        onCheckedChange = { viewModel.toggleSharpCorners(it) }
                    )

                    SettingToggleRow(
                        title = "Show Home Screen Clock",
                        subtitle = "Display clock and date header on top of the home screen",
                        checked = userSettings.showClock,
                        onCheckedChange = { viewModel.toggleShowClock(it) }
                    )

                    SettingToggleRow(
                        title = "Use 24-Hour Clock Format",
                        subtitle = "Format clock time as 24h (14:30) vs 12h (2:30 PM)",
                        checked = userSettings.clockFormat24h,
                        onCheckedChange = { viewModel.toggleClock24h(it) }
                    )

                    SettingToggleRow(
                        title = "Display Date Subtitle",
                        subtitle = "Show current day and date beneath clock display",
                        checked = userSettings.showDate,
                        onCheckedChange = { viewModel.toggleShowDate(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Swipe Gestures Shortcut Card
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
                            text = "Swipe Gestures App Shortcuts",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Swipe Left
                    val leftAppLabel = installedApps.find { it.packageName == userSettings.swipeLeftPackage }?.label ?: if (userSettings.swipeLeftPackage.isBlank()) "None (Disabled)" else userSettings.swipeLeftPackage
                    SettingToggleRow(
                        title = "Swipe Left App: $leftAppLabel",
                        subtitle = "Tap to choose app for left swipe gesture",
                        checked = false,
                        showSwitch = false,
                        onClick = { pickingGestureSide = "LEFT" }
                    )

                    // Swipe Right
                    val rightAppLabel = installedApps.find { it.packageName == userSettings.swipeRightPackage }?.label ?: if (userSettings.swipeRightPackage.isBlank()) "None (Disabled)" else userSettings.swipeRightPackage
                    SettingToggleRow(
                        title = "Swipe Right App: $rightAppLabel",
                        subtitle = "Tap to choose app for right swipe gesture",
                        checked = false,
                        showSwitch = false,
                        onClick = { pickingGestureSide = "RIGHT" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Aliases Card
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
                            text = "Custom Command Aliases",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newAliasName,
                            onValueChange = { newAliasName = it },
                            placeholder = { Text("Alias (e.g. wa)", color = KernTextSecondary) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = KernDarkSurfaceBorder
                            )
                        )

                        OutlinedTextField(
                            value = newAliasTarget,
                            onValueChange = { newAliasTarget = it },
                            placeholder = { Text("Target (e.g. whatsapp)", color = KernTextSecondary) },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = KernDarkSurfaceBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newAliasName.isNotBlank() && newAliasTarget.isNotBlank()) {
                                viewModel.addAlias(newAliasName.trim(), newAliasTarget.trim())
                                newAliasName = ""
                                newAliasTarget = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(cardCorner),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ADD ALIAS", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (aliases.isEmpty()) {
                        Text(
                            text = "No custom aliases added yet.",
                            style = MaterialTheme.typography.bodySmall.copy(color = KernTextSecondary)
                        )
                    } else {
                        aliases.forEach { alias ->
                            AliasItemRow(
                                alias = alias,
                                onDelete = { viewModel.deleteAlias(alias.alias) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(140.dp))
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    showSwitch: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (onClick != null) onClick()
                else onCheckedChange?.invoke(!checked)
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = KernTextSecondary, fontSize = 11.sp)
            )
        }
        if (showSwitch) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.surface,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = KernTextSecondary,
                    uncheckedTrackColor = KernDarkSurfaceBorder
                )
            )
        }
    }
}

@Composable
fun OptionSelectorRow(
    options: List<String>,
    currentSelected: String,
    sharpCorners: Boolean,
    onSelect: (String) -> Unit
) {
    val cornerRadius = if (sharpCorners) 0.dp else 8.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { option ->
            val isSelected = currentSelected.equals(option, ignoreCase = true)
            val bg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val textCol = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(bg)
                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else KernDarkSurfaceBorder, RoundedCornerShape(cornerRadius))
                    .clickable { onSelect(option) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.uppercase(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textCol,
                        fontSize = 11.sp
                    )
                )
            }
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
    var showColorPickerModal by remember { mutableStateOf(false) }
    val parsedColor = parseHexColor(textValue, Color.Gray)

    if (showColorPickerModal) {
        ColorPickerDialog(
            title = title,
            initialColorHex = textValue,
            sharpCorners = sharpCorners,
            onColorSelected = { selectedHex ->
                textValue = selectedHex
                onColorChanged(selectedHex)
            },
            onDismiss = { showColorPickerModal = false }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "PICK VISUAL",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { showColorPickerModal = true }
            )
        }

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
                    .clickable { showColorPickerModal = true }
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
                ),
                trailingIcon = {
                    IconButton(onClick = { showColorPickerModal = true }) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Pick Color",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
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
    val cornerRadius = if (sharpCorners) 0.dp else 8.dp

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else KernDarkSurfaceBorder, RoundedCornerShape(cornerRadius))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(bg)
                .border(1.dp, KernDarkSurfaceBorder, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun AliasItemRow(
    alias: Alias,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${alias.alias} ➔ ${alias.targetCommandOrPackage}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Alias",
                tint = KernRed
            )
        }
    }
}
