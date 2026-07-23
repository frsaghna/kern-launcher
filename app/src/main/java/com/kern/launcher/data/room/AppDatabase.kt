package com.kern.launcher.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kern.launcher.data.room.dao.AliasDao
import com.kern.launcher.data.room.dao.AppUsageDao
import com.kern.launcher.data.room.dao.CommandHistoryDao
import com.kern.launcher.data.room.dao.HiddenAppDao
import com.kern.launcher.data.room.entity.AliasEntity
import com.kern.launcher.data.room.entity.AppUsageEntity
import com.kern.launcher.data.room.entity.CommandHistoryEntity
import com.kern.launcher.data.room.entity.HiddenAppEntity

@Database(
    entities = [
        AppUsageEntity::class,
        CommandHistoryEntity::class,
        AliasEntity::class,
        HiddenAppEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appUsageDao(): AppUsageDao
    abstract fun commandHistoryDao(): CommandHistoryDao
    abstract fun aliasDao(): AliasDao
    abstract fun hiddenAppDao(): HiddenAppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kern_launcher.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
