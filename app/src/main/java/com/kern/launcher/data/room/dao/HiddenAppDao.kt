package com.kern.launcher.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kern.launcher.data.room.entity.HiddenAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenAppDao {
    @Query("SELECT * FROM hidden_apps")
    fun getAllHiddenApps(): Flow<List<HiddenAppEntity>>

    @Query("SELECT * FROM hidden_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getHiddenApp(packageName: String): HiddenAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun hideApp(entity: HiddenAppEntity)

    @Query("DELETE FROM hidden_apps WHERE packageName = :packageName")
    suspend fun unhideApp(packageName: String)
}
