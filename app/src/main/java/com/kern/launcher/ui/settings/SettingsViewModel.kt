package com.kern.launcher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kern.launcher.data.datastore.SettingsDataStore
import com.kern.launcher.data.repository.AliasRepository
import com.kern.launcher.data.repository.AppRepository
import com.kern.launcher.data.repository.CommandHistoryRepository
import com.kern.launcher.data.repository.HiddenAppItem
import com.kern.launcher.data.repository.HiddenAppRepository
import com.kern.launcher.model.Alias
import com.kern.launcher.model.AppInfo
import com.kern.launcher.model.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore,
    private val aliasRepository: AliasRepository,
    private val historyRepository: CommandHistoryRepository,
    private val hiddenAppRepository: HiddenAppRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = settingsDataStore.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val aliases: StateFlow<List<Alias>> = aliasRepository.getAllAliases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hiddenApps: StateFlow<List<HiddenAppItem>> = hiddenAppRepository.getHiddenApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val installedApps: StateFlow<List<AppInfo>> = appRepository.getInstalledAppsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleDarkMode(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setDarkMode(enabled)
    }

    fun setThemePalette(palette: String) = viewModelScope.launch {
        settingsDataStore.setThemePalette(palette)
    }

    fun setCustomBgColor(colorHex: String) = viewModelScope.launch {
        settingsDataStore.setCustomBgColor(colorHex)
    }

    fun setCustomAccentColor(colorHex: String) = viewModelScope.launch {
        settingsDataStore.setCustomAccentColor(colorHex)
    }

    fun setCustomTextColor(colorHex: String) = viewModelScope.launch {
        settingsDataStore.setCustomTextColor(colorHex)
    }

    fun toggleSharpCorners(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setSharpCorners(enabled)
    }

    fun setFontStyle(style: String) = viewModelScope.launch {
        settingsDataStore.setFontStyle(style)
    }

    fun toggleShowAppIcons(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setShowAppIcons(enabled)
    }

    fun toggleAutoLaunchSingleMatch(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setAutoLaunchSingleMatch(enabled)
    }

    fun toggleTuiViewMode(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setTuiViewMode(enabled)
    }

    fun toggleShowClock(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setShowClock(enabled)
    }

    fun toggleClock24h(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setClock24h(enabled)
    }

    fun toggleShowDate(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setShowDate(enabled)
    }

    fun toggleAutoFocusKeyboard(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setAutoFocusKeyboard(enabled)
    }

    fun setSwipeLeftPackage(packageName: String) = viewModelScope.launch {
        settingsDataStore.setSwipeLeftPackage(packageName)
    }

    fun setSwipeRightPackage(packageName: String) = viewModelScope.launch {
        settingsDataStore.setSwipeRightPackage(packageName)
    }

    fun setAiProvider(provider: String) = viewModelScope.launch {
        settingsDataStore.setAiProvider(provider)
    }

    fun setClockAlignment(alignment: String) = viewModelScope.launch {
        settingsDataStore.setClockAlignment(alignment)
    }

    fun setAppListAlignment(alignment: String) = viewModelScope.launch {
        settingsDataStore.setAppListAlignment(alignment)
    }

    fun setClockFontSize(size: String) = viewModelScope.launch {
        settingsDataStore.setClockFontSize(size)
    }

    fun setAppListFontSize(size: String) = viewModelScope.launch {
        settingsDataStore.setAppListFontSize(size)
    }

    fun toggleTransparentBg(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setTransparentBg(enabled)
    }

    fun toggleShowAppListOutlines(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.setShowAppListOutlines(enabled)
    }

    fun addAlias(alias: String, target: String) = viewModelScope.launch {
        aliasRepository.saveAlias(alias, target)
    }

    fun deleteAlias(alias: String) = viewModelScope.launch {
        aliasRepository.deleteAlias(alias)
    }

    fun unhideApp(packageName: String) = viewModelScope.launch {
        hiddenAppRepository.unhideApp(packageName)
    }

    fun clearHistory() = viewModelScope.launch {
        historyRepository.clearHistory()
    }
}
