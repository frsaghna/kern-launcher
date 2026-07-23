package com.kern.launcher.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kern.launcher.data.room.entity.AliasEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AliasDao {
    @Query("SELECT * FROM aliases")
    fun getAllAliases(): Flow<List<AliasEntity>>

    @Query("SELECT * FROM aliases WHERE alias = :alias LIMIT 1")
    suspend fun getAlias(alias: String): AliasEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(alias: AliasEntity)

    @Delete
    suspend fun delete(alias: AliasEntity)
}
