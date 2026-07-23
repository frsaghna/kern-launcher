package com.kern.launcher.data.repository

import com.kern.launcher.data.room.dao.CommandHistoryDao
import com.kern.launcher.data.room.entity.CommandHistoryEntity
import com.kern.launcher.model.CommandHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CommandHistoryRepository(
    private val commandHistoryDao: CommandHistoryDao
) {
    fun getRecentHistory(): Flow<List<CommandHistoryItem>> {
        return commandHistoryDao.getRecentHistory().map { list ->
            list.map { entity ->
                CommandHistoryItem(
                    id = entity.id,
                    query = entity.query,
                    timestamp = entity.timestamp,
                    executionCount = entity.executionCount
                )
            }
        }
    }

    suspend fun recordCommand(query: String) = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@withContext

        val existing = commandHistoryDao.findByQuery(trimmed)
        if (existing != null) {
            val updated = existing.copy(
                timestamp = System.currentTimeMillis(),
                executionCount = existing.executionCount + 1
            )
            commandHistoryDao.insert(updated)
        } else {
            val newEntry = CommandHistoryEntity(
                query = trimmed,
                timestamp = System.currentTimeMillis(),
                executionCount = 1
            )
            commandHistoryDao.insert(newEntry)
        }
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        commandHistoryDao.clearHistory()
    }
}
