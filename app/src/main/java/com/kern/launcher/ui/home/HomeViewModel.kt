package com.kern.launcher.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kern.launcher.command.executor.CommandExecutor
import com.kern.launcher.data.datastore.SettingsDataStore
import com.kern.launcher.data.repository.AliasRepository
import com.kern.launcher.data.repository.AppRepository
import com.kern.launcher.data.repository.CommandHistoryRepository
import com.kern.launcher.data.repository.HiddenAppItem
import com.kern.launcher.data.repository.HiddenAppRepository
import com.kern.launcher.model.Alias
import com.kern.launcher.model.AppInfo
import com.kern.launcher.model.CommandHistoryItem
import com.kern.launcher.model.SearchResult
import com.kern.launcher.model.SearchResultType
import com.kern.launcher.model.UserSettings
import com.kern.launcher.search.engine.SearchEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val appRepository: AppRepository,
    private val historyRepository: CommandHistoryRepository,
    private val aliasRepository: AliasRepository,
    private val hiddenAppRepository: HiddenAppRepository,
    private val settingsDataStore: SettingsDataStore,
    context: Context
) : ViewModel() {

    private val searchEngine = SearchEngine()
    private val commandExecutor = CommandExecutor(context, appRepository, historyRepository, hiddenAppRepository)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    private val historyIndex = MutableStateFlow(-1)

    val userSettings: StateFlow<UserSettings> = settingsDataStore.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    private val installedApps: StateFlow<List<AppInfo>> = appRepository.getInstalledAppsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aliases: StateFlow<List<Alias>> = aliasRepository.getAllAliases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hiddenApps: StateFlow<List<HiddenAppItem>> = hiddenAppRepository.getHiddenApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<CommandHistoryItem>> = historyRepository.getRecentHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<List<SearchResult>> = combine(
        _query,
        installedApps,
        aliases,
        hiddenApps,
        userSettings
    ) { queryText, appsList, aliasList, hiddenList, settings ->
        val hiddenPkgs = hiddenList.map { it.packageName }.toSet()
        searchEngine.search(queryText, appsList, aliasList, hiddenPkgs, aiProvider = settings.aiProvider)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            searchResults.collect { results ->
                val currentQuery = _query.value.trim().lowercase()
                val builtinKeywords = listOf("settings", "setting", "config", "pref", "kern", "help", "?", "manual", "tui", "tuiview", "terminal", "hidden", "secret", "info", "hide", "unhide", "ai", "gpt", "chatgpt", "gemini", "log", "lazylogs", "calc", "timer", "g", "google", "yt", "youtube", "spot", "spotify", "music", "play", "store", "ps", "gh", "github", "wiki", "wikipedia", "reddit", "r", "x", "tw", "twitter", "ddg", "duck", "maps", "map")

                // Check if current query matches or is a prefix of any built-in command keyword
                val isBuiltinPrefix = builtinKeywords.any { it.startsWith(currentQuery) || currentQuery.startsWith(it) }

                if (currentQuery.isNotEmpty() && !isBuiltinPrefix && userSettings.value.autoLaunchSingleMatch) {
                    val appResults = results.filter { it.type == SearchResultType.APP }
                    val builtinResults = results.filter { it.type == SearchResultType.BUILTIN_COMMAND && it.id != "fallback_search" }
                    val aliasResults = results.filter { it.type == SearchResultType.ALIAS }
                    val calcResults = results.filter { it.type == SearchResultType.CALCULATOR_RESULT }

                    if (appResults.size == 1 && builtinResults.isEmpty() && aliasResults.isEmpty() && calcResults.isEmpty()) {
                        val singleApp = appResults.first()
                        executeResult(singleApp, onOpenSettings = {}, onOpenHelp = {}, onOpenHiddenApps = {})
                    }
                }
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        _selectedIndex.value = 0
        historyIndex.value = -1
    }

    fun unhideApp(packageName: String) = viewModelScope.launch {
        hiddenAppRepository.unhideApp(packageName)
    }

    fun toggleTuiViewMode() = viewModelScope.launch {
        val current = userSettings.value.tuiViewMode
        settingsDataStore.setTuiViewMode(!current)
    }

    fun navigateHistoryUp() {
        val currentHistory = history.value
        if (currentHistory.isEmpty()) return

        val newIndex = (historyIndex.value + 1).coerceAtMost(currentHistory.size - 1)
        historyIndex.value = newIndex
        _query.value = currentHistory[newIndex].query
        _selectedIndex.value = 0
    }

    fun navigateHistoryDown() {
        val currentHistory = history.value
        if (currentHistory.isEmpty() || historyIndex.value <= 0) {
            historyIndex.value = -1
            _query.value = ""
            _selectedIndex.value = 0
            return
        }

        val newIndex = historyIndex.value - 1
        historyIndex.value = newIndex
        _query.value = currentHistory[newIndex].query
        _selectedIndex.value = 0
    }

    fun executeCurrentSelection(
        onOpenSettings: () -> Unit,
        onOpenHelp: () -> Unit,
        onOpenHiddenApps: () -> Unit
    ) = viewModelScope.launch {
        val results = searchResults.value
        val index = selectedIndex.value
        val targetResult = results.getOrNull(index) ?: results.firstOrNull()

        if (targetResult != null) {
            val success = commandExecutor.execute(
                command = targetResult.actionCommand,
                onOpenSettings = onOpenSettings,
                onOpenHelp = onOpenHelp,
                onOpenHiddenApps = onOpenHiddenApps,
                onToggleTuiView = { toggleTuiViewMode() }
            )
            if (success) {
                _query.value = ""
                _selectedIndex.value = 0
                historyIndex.value = -1
            }
        }
    }

    fun executeResult(
        result: SearchResult,
        onOpenSettings: () -> Unit,
        onOpenHelp: () -> Unit,
        onOpenHiddenApps: () -> Unit
    ) = viewModelScope.launch {
        val success = commandExecutor.execute(
            command = result.actionCommand,
            onOpenSettings = onOpenSettings,
            onOpenHelp = onOpenHelp,
            onOpenHiddenApps = onOpenHiddenApps,
            onToggleTuiView = { toggleTuiViewMode() }
        )
        if (success) {
            _query.value = ""
            _selectedIndex.value = 0
            historyIndex.value = -1
        }
    }
}
