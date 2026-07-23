package com.kern.launcher.data.repository

import com.kern.launcher.data.room.dao.AliasDao
import com.kern.launcher.data.room.entity.AliasEntity
import com.kern.launcher.model.Alias
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AliasRepository(
    private val aliasDao: AliasDao
) {
    fun getAllAliases(): Flow<List<Alias>> {
        return aliasDao.getAllAliases().map { list ->
            list.map { entity -> Alias(entity.alias, entity.targetCommandOrPackage) }
        }
    }

    suspend fun getAlias(alias: String): Alias? = withContext(Dispatchers.IO) {
        aliasDao.getAlias(alias.lowercase().trim())?.let {
            Alias(it.alias, it.targetCommandOrPackage)
        }
    }

    suspend fun saveAlias(alias: String, target: String) = withContext(Dispatchers.IO) {
        val entity = AliasEntity(alias.lowercase().trim(), target.trim())
        aliasDao.insertOrUpdate(entity)
    }

    suspend fun deleteAlias(alias: String) = withContext(Dispatchers.IO) {
        val existing = aliasDao.getAlias(alias.lowercase().trim())
        if (existing != null) {
            aliasDao.delete(existing)
        }
    }
}
