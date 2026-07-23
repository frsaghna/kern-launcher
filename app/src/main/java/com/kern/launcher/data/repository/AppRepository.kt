package com.kern.launcher.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.kern.launcher.data.room.dao.AppUsageDao
import com.kern.launcher.data.room.entity.AppUsageEntity
import com.kern.launcher.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class AppRepository(
    private val context: Context,
    private val appUsageDao: AppUsageDao
) {

    fun getInstalledAppsFlow(): Flow<List<AppInfo>> {
        return appUsageDao.getAllAppUsages().combine(scanAppsFlow()) { usages, scannedApps ->
            val usageMap = usages.associateBy { it.packageName }
            scannedApps.map { app ->
                val usage = usageMap[app.packageName]
                if (usage != null) {
                    app.copy(
                        usageCount = usage.usageCount,
                        lastUsedTime = usage.lastUsedTime
                    )
                } else {
                    app
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun scanAppsFlow(): Flow<List<AppInfo>> = callbackFlow {
        // Emit initial scan
        trySend(scanApps())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                // When package added, removed, or changed -> re-scan apps immediately
                trySend(scanApps())
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }

        context.registerReceiver(receiver, filter)

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }.flowOn(Dispatchers.IO)

    fun scanApps(): List<AppInfo> {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        return resolveInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            val activityName = resolveInfo.activityInfo.name
            val label = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)

            if (packageName.isBlank()) null
            else AppInfo(
                packageName = packageName,
                activityName = activityName,
                label = label,
                icon = icon
            )
        }.distinctBy { it.packageName }
    }

    suspend fun incrementAppUsage(packageName: String, activityName: String) = withContext(Dispatchers.IO) {
        val existing = appUsageDao.getAppUsage(packageName)
        val newCount = (existing?.usageCount ?: 0) + 1
        val updated = AppUsageEntity(
            packageName = packageName,
            activityName = activityName,
            usageCount = newCount,
            lastUsedTime = System.currentTimeMillis()
        )
        appUsageDao.insertOrUpdate(updated)
    }
}
