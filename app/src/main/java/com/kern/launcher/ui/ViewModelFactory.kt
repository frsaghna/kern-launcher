package com.kern.launcher.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kern.launcher.data.datastore.SettingsDataStore
import com.kern.launcher.data.repository.AliasRepository
import com.kern.launcher.data.repository.AppRepository
import com.kern.launcher.data.repository.CommandHistoryRepository
import com.kern.launcher.data.repository.HiddenAppRepository
import com.kern.launcher.data.room.AppDatabase
import com.kern.launcher.ui.home.HomeViewModel
import com.kern.launcher.ui.settings.SettingsViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val db = AppDatabase.getInstance(context)
    private val appRepository = AppRepository(context, db.appUsageDao())
    private val historyRepository = CommandHistoryRepository(db.commandHistoryDao())
    private val aliasRepository = AliasRepository(db.aliasDao())
    private val hiddenAppRepository = HiddenAppRepository(db.hiddenAppDao())
    private val settingsDataStore = SettingsDataStore(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(appRepository, historyRepository, aliasRepository, hiddenAppRepository, settingsDataStore, context) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsDataStore, aliasRepository, historyRepository, hiddenAppRepository, appRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
