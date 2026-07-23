package com.kern.launcher.data.repository

import com.kern.launcher.data.room.dao.HiddenAppDao
import com.kern.launcher.data.room.entity.HiddenAppEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class HiddenAppItem(
    val packageName: String,
    val appName: String,
    val hiddenAt: Long
)

class HiddenAppRepository(
    private val hiddenAppDao: HiddenAppDao
) {
    fun getHiddenApps(): Flow<List<HiddenAppItem>> {
        return hiddenAppDao.getAllHiddenApps().map { list ->
            list.map { entity ->
                HiddenAppItem(
                    packageName = entity.packageName,
                    appName = entity.appName,
                    hiddenAt = entity.hiddenAt
                )
            }
        }
    }

    suspend fun isHidden(packageName: String): Boolean = withContext(Dispatchers.IO) {
        hiddenAppDao.getHiddenApp(packageName) != null
    }

    suspend fun hideApp(packageName: String, appName: String) = withContext(Dispatchers.IO) {
        val entity = HiddenAppEntity(packageName = packageName, appName = appName)
        hiddenAppDao.hideApp(entity)
    }

    suspend fun unhideApp(packageName: String) = withContext(Dispatchers.IO) {
        hiddenAppDao.unhideApp(packageName)
    }
}
