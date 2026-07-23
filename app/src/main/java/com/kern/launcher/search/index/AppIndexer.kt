package com.kern.launcher.search.index

import com.kern.launcher.data.repository.AppRepository
import com.kern.launcher.model.AppInfo
import kotlinx.coroutines.flow.Flow

class AppIndexer(
    private val appRepository: AppRepository
) {
    fun getAppsFlow(): Flow<List<AppInfo>> = appRepository.getInstalledAppsFlow()
}
