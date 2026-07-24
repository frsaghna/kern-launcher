package com.kern.launcher.ui.tui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.kern.launcher.model.SearchResult
import com.kern.launcher.service.intent.IntentFactory
import com.kern.launcher.ui.components.HelpDialog
import com.kern.launcher.ui.components.HiddenAppsDialog
import com.kern.launcher.ui.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TuiHomeScreen(
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
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var currentDate by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentDate = Date()
            delay(1000)
        }
    }

    val timeFormatter = remember(userSettings.clockFormat24h) {
        SimpleDateFormat(if (userSettings.clockFormat24h) "HH:mm:ss" else "hh:mm:ss a", Locale.getDefault())
    }
    val dateFormatter = remember { SimpleDateFormat("EEE d MMM yyyy", Locale.getDefault()) }

    val onExecuteCurrent = {
        viewModel.executeCurrentSelection(
            onOpenSettings = onOpenSettings,
            onOpenHelp = { showHelpDialog = true },
            onOpenHiddenApps = { showHiddenAppsDialog = true }
        )
    }

    if (showHelpDialog) {
        HelpDialog(
            aliases = aliases,
            sharpCorners = true,
            onDismiss = { showHelpDialog = false }
        )
    }

    if (showHiddenAppsDialog) {
        HiddenAppsDialog(
            hiddenApps = hiddenApps,
            onUnhideApp = { pkg -> viewModel.unhideApp(pkg) },
            sharpCorners = true,
            onDismiss = { showHiddenAppsDialog = false }
        )
    }

    // Safe Re-focus and show soft keyboard when screen unlocks or Activity resumes
    DisposableEffect(lifecycleOwner, userSettings.autoFocusKeyboard) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && userSettings.autoFocusKeyboard) {
                coroutineScope.launch {
                    delay(100)
                    try {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    } catch (e: Exception) {
                        // Safe catch for composition focus race conditions
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(userSettings.swipeLeftPackage, userSettings.swipeRightPackage) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var change = down
                        var totalX = 0f
                        var totalY = 0f

                        while (change.pressed) {
                            val event = awaitPointerEvent()
                            val current = event.changes.firstOrNull() ?: break
                            val delta = current.position - current.previousPosition
                            totalX += delta.x
                            totalY += delta.y
                            change = current
                        }

                        if (kotlin.math.abs(totalX) > 150f && kotlin.math.abs(totalX) > kotlin.math.abs(totalY) * 2.5f) {
                            if (totalX < -150f && userSettings.swipeLeftPackage.isNotBlank()) {
                                val intent = context.packageManager.getLaunchIntentForPackage(userSettings.swipeLeftPackage)
                                if (intent != null) context.startActivity(intent)
                            } else if (totalX > 150f && userSettings.swipeRightPackage.isNotBlank()) {
                                val intent = context.packageManager.getLaunchIntentForPackage(userSettings.swipeRightPackage)
                                if (intent != null) context.startActivity(intent)
                            }
                        }
                    }
                }
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 2.dp)
        ) {
            // Ultra Minimal Raw Time & Date Header (Clickable Clock & Calendar)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timeFormatter.format(currentDate),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.clickable { IntentFactory.openClock(context) }
                )
                Text(
                    text = " -- ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = dateFormatter.format(currentDate).uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.clickable { IntentFactory.openCalendar(context) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Raw Shell Prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "> ",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                )

                BasicTextField(
                    value = query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = { onExecuteCurrent() },
                        onDone = { onExecuteCurrent() },
                        onSearch = { onExecuteCurrent() }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown) {
                                when (keyEvent.key) {
                                    Key.Enter, Key.NumPadEnter -> {
                                        onExecuteCurrent()
                                        true
                                    }
                                    Key.DirectionUp -> {
                                        viewModel.navigateHistoryUp()
                                        true
                                    }
                                    Key.DirectionDown -> {
                                        viewModel.navigateHistoryDown()
                                        true
                                    }
                                    else -> false
                                }
                            } else false
                        },
                    decorationBox = { innerTextField ->
                        Box {
                            if (query.isEmpty()) {
                                Text(
                                    text = "type app or command...",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                        fontSize = 18.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Plain ASCII Divider Line
            Text(
                text = "----------------------------------------------------------------",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    fontSize = 10.sp
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Pure Text Results List (fzf / dmenu style)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = searchResults,
                    key = { index, item -> "${item.id}_$index" }
                ) { index, item ->
                    val isSelected = index == selectedIndex
                    RawSearchResultRow(
                        result = item,
                        isSelected = isSelected,
                        onClick = {
                            viewModel.executeResult(
                                result = item,
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

@Composable
fun RawSearchResultRow(
    result: SearchResult,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val prefix = if (isSelected) "> " else "  "
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 5.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prefix,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp
            )
        )

        Text(
            text = result.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                fontSize = 15.sp
            ),
            modifier = Modifier.weight(1f)
        )

        if (result.subtitle.isNotBlank() && isSelected) {
            Text(
                text = result.subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            )
        }
    }
}
