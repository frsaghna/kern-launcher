package com.kern.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.kern.launcher.ui.theme.KernDarkSurfaceBorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CommandBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onExecute: () -> Unit,
    onNavigateHistoryUp: () -> Unit,
    onNavigateHistoryDown: () -> Unit,
    onOpenSettings: (() -> Unit)? = null,
    autoFocus: Boolean = true,
    sharpCorners: Boolean = true,
    isTransparentBg: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val cornerRadius = if (sharpCorners) 0.dp else 12.dp

    // Safe Re-focus and show soft keyboard every time screen unlocks or Activity resumes
    DisposableEffect(lifecycleOwner, autoFocus) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && autoFocus) {
                coroutineScope.launch {
                    delay(100)
                    try {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    } catch (e: Exception) {
                        // Safe catch for focus race condition
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val barBgColor = if (isTransparentBg) MaterialTheme.colorScheme.surface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(barBgColor)
            .border(1.dp, KernDarkSurfaceBorder, RoundedCornerShape(cornerRadius))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "> ",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable {
                    onOpenSettings?.invoke()
                }
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyDown) {
                            when (keyEvent.key) {
                                Key.DirectionUp -> {
                                    onNavigateHistoryUp()
                                    true
                                }
                                Key.DirectionDown -> {
                                    onNavigateHistoryDown()
                                    true
                                }
                                else -> false
                            }
                        } else false
                    },
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = { onExecute() },
                    onDone = { onExecute() }
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = "Type help, set, app, maps, yt...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                    fontSize = 16.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}
