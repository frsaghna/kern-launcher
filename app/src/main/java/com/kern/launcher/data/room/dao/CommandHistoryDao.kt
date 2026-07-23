package com.kern.launcher.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kern.launcher.data.room.entity.CommandHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandHistoryDao {
    @Query("SELECT * FROM command_history ORDER BY timestamp DESC LIMIT 50")
    fun getRecentHistory(): Flow<List<CommandHistoryEntity>>

    @Query("SELECT * FROM command_history WHERE query = :query LIMIT 1")
    suspend fun findByQuery(query: String): CommandHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CommandHistoryEntity)

    @Query("DELETE FROM command_history")
    suspend fun clearHistory()
}
