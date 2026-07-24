package com.kern.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kern.launcher.ui.ViewModelFactory
import com.kern.launcher.ui.home.HomeScreen
import com.kern.launcher.ui.home.HomeViewModel
import com.kern.launcher.ui.settings.SettingsScreen
import com.kern.launcher.ui.settings.SettingsViewModel
import com.kern.launcher.ui.theme.KernTheme

enum class Screen {
    HOME,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val factory by lazy { ViewModelFactory(applicationContext) }
    private val homeViewModel: HomeViewModel by viewModels { factory }
    private val settingsViewModel: SettingsViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val userSettings by homeViewModel.userSettings.collectAsState()
            val query by homeViewModel.query.collectAsState()

            KernTheme(userSettings = userSettings) {
                var currentScreen by remember { mutableStateOf(Screen.HOME) }

                BackHandler(enabled = true) {
                    if (currentScreen == Screen.SETTINGS) {
                        currentScreen = Screen.HOME
                    } else {
                        if (query.isNotEmpty()) {
                            homeViewModel.onQueryChange("")
                        } else {
                            // Consumed on Home screen - prevents Activity from restarting or finishing
                        }
                    }
                }

                when (currentScreen) {
                    Screen.HOME -> {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onOpenSettings = { currentScreen = Screen.SETTINGS }
                        )
                    }
                    Screen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { currentScreen = Screen.HOME }
                        )
                    }
                }
            }
        }
    }
}
