package com.kern.launcher.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kern.launcher.data.room.dao.AliasDao
import com.kern.launcher.data.room.dao.AppUsageDao
import com.kern.launcher.data.room.dao.CommandHistoryDao
import com.kern.launcher.data.room.dao.HiddenAppDao
import com.kern.launcher.data.room.entity.AliasEntity
import com.kern.launcher.data.room.entity.AppUsageEntity
import com.kern.launcher.data.room.entity.CommandHistoryEntity
import com.kern.launcher.data.room.entity.HiddenAppEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var aliasDao: AliasDao
    private lateinit var appUsageDao: AppUsageDao
    private lateinit var commandHistoryDao: CommandHistoryDao
    private lateinit var hiddenAppDao: HiddenAppDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        aliasDao = db.aliasDao()
        appUsageDao = db.appUsageDao()
        commandHistoryDao = db.commandHistoryDao()
        hiddenAppDao = db.hiddenAppDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testAliasDaoRoundTrip() = runBlocking {
        val alias = AliasEntity(alias = "yt", targetCommandOrPackage = "com.google.android.youtube")
        aliasDao.insertOrUpdate(alias)

        val retrieved = aliasDao.getAlias("yt")
        assertNotNull(retrieved)
        assertEquals("com.google.android.youtube", retrieved?.targetCommandOrPackage)

        val all = aliasDao.getAllAliases().first()
        assertEquals(1, all.size)

        aliasDao.delete(alias)
        assertNull(aliasDao.getAlias("yt"))
    }

    @Test
    fun testAppUsageDaoRoundTrip() = runBlocking {
        val usage = AppUsageEntity(
            packageName = "com.example.app",
            activityName = "com.example.app.MainActivity",
            usageCount = 5,
            lastUsedTime = 123456789L
        )
        appUsageDao.insertOrUpdate(usage)

        val retrieved = appUsageDao.getAppUsage("com.example.app")
        assertNotNull(retrieved)
        assertEquals(5, retrieved?.usageCount)
    }

    @Test
    fun testCommandHistoryDaoRoundTrip() = runBlocking {
        val item = CommandHistoryEntity(
            query = "wifi off",
            timestamp = 1000L,
            executionCount = 1
        )
        commandHistoryDao.insert(item)

        val retrieved = commandHistoryDao.findByQuery("wifi off")
        assertNotNull(retrieved)
        assertEquals(1000L, retrieved?.timestamp)

        commandHistoryDao.clearHistory()
        assertNull(commandHistoryDao.findByQuery("wifi off"))
    }

    @Test
    fun testHiddenAppDaoRoundTrip() = runBlocking {
        val hidden = HiddenAppEntity(packageName = "com.secret.app", appName = "Secret App")
        hiddenAppDao.hideApp(hidden)

        val retrieved = hiddenAppDao.getHiddenApp("com.secret.app")
        assertNotNull(retrieved)

        hiddenAppDao.unhideApp("com.secret.app")
        assertNull(hiddenAppDao.getHiddenApp("com.secret.app"))
    }
}
