package com.kern.launcher.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kern.launcher.ui.components.ClockView
import com.kern.launcher.ui.components.CommandBar
import com.kern.launcher.ui.components.DateView
import com.kern.launcher.ui.components.HelpDialog
import com.kern.launcher.ui.components.HiddenAppsDialog
import com.kern.launcher.ui.search.SearchResultList
import com.kern.launcher.ui.tui.TuiHomeScreen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.query.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedIndex by viewModel.selectedIndex.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val aliases by viewModel.aliases.collectAsState()
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val context = LocalContext.current

    var showHelpDialog by remember { mutableStateOf(false) }
    var showHiddenAppsDialog by remember { mutableStateOf(false) }

    if (userSettings.tuiViewMode) {
        TuiHomeScreen(
            viewModel = viewModel,
            onOpenSettings = onOpenSettings,
            modifier = modifier
        )
        return
    }

    if (showHelpDialog) {
        HelpDialog(
            aliases = aliases,
            sharpCorners = userSettings.sharpCorners,
            onDismiss = { showHelpDialog = false }
        )
    }

    if (showHiddenAppsDialog) {
        HiddenAppsDialog(
            hiddenApps = hiddenApps,
            onUnhideApp = { pkg -> viewModel.unhideApp(pkg) },
            sharpCorners = userSettings.sharpCorners,
            onDismiss = { showHiddenAppsDialog = false }
        )
    }

    val containerBg = if (userSettings.isTransparentBg) Color.Transparent else MaterialTheme.colorScheme.background

    val clockColumnAlign = when (userSettings.clockAlignment.uppercase()) {
        "CENTER" -> Alignment.CenterHorizontally
        "RIGHT" -> Alignment.End
        else -> Alignment.Start
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = containerBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(userSettings.swipeLeftPackage, userSettings.swipeRightPackage) {
                    var totalDragX = 0f
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (totalDragX < -120f && userSettings.swipeLeftPackage.isNotBlank()) {
                                val intent = context.packageManager.getLaunchIntentForPackage(userSettings.swipeLeftPackage)
                                if (intent != null) context.startActivity(intent)
                            } else if (totalDragX > 120f && userSettings.swipeRightPackage.isNotBlank()) {
                                val intent = context.packageManager.getLaunchIntentForPackage(userSettings.swipeRightPackage)
                                if (intent != null) context.startActivity(intent)
                            }
                            totalDragX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            totalDragX += dragAmount
                        }
                    )
                }
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 2.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Clock & Date Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = clockColumnAlign
                ) {
                    ClockView(
                        is24Hour = userSettings.clockFormat24h,
                        fontSizeOption = userSettings.clockFontSize,
                        alignmentOption = userSettings.clockAlignment
                    )

                    if (userSettings.showDate) {
                        Spacer(modifier = Modifier.height(4.dp))
                        DateView(alignmentOption = userSettings.clockAlignment)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Command Bar
                CommandBar(
                    query = query,
                    onQueryChange = { viewModel.onQueryChange(it) },
                    onExecute = {
                        viewModel.executeCurrentSelection(
                            onOpenSettings = onOpenSettings,
                            onOpenHelp = { showHelpDialog = true },
                            onOpenHiddenApps = { showHiddenAppsDialog = true }
                        )
                    },
                    onNavigateHistoryUp = { viewModel.navigateHistoryUp() },
                    onNavigateHistoryDown = { viewModel.navigateHistoryDown() },
                    onOpenSettings = onOpenSettings,
                    autoFocus = userSettings.autoFocusKeyboard,
                    sharpCorners = userSettings.sharpCorners,
                    isTransparentBg = userSettings.isTransparentBg
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search Results Dropdown List
                AnimatedVisibility(
                    visible = searchResults.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    SearchResultList(
                        results = searchResults,
                        selectedIndex = selectedIndex,
                        showAppIcons = userSettings.showAppIcons,
                        sharpCorners = userSettings.sharpCorners,
                        alignmentOption = userSettings.appListAlignment,
                        fontSizeOption = userSettings.appListFontSize,
                        showOutlines = userSettings.showAppListOutlines,
                        onResultSelected = { result ->
                            viewModel.executeResult(
                                result = result,
                                onOpenSettings = onOpenSettings,
                                onOpenHelp = { showHelpDialog = true },
                                onOpenHiddenApps = { showHiddenAppsDialog = true }
                            )
                        }
                    )
                }
            }
        }
    }
}
