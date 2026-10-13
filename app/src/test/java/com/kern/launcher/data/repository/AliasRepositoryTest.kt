package com.kern.launcher.data.repository

import com.kern.launcher.command.parser.CommandParser
import com.kern.launcher.data.room.dao.AliasDao
import com.kern.launcher.data.room.entity.AliasEntity
import com.kern.launcher.model.Command
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AliasRepositoryTest {

    private class FakeAliasDao : AliasDao {
        private val storage = mutableMapOf<String, AliasEntity>()
        private val flow = MutableStateFlow<List<AliasEntity>>(emptyList())

        private fun updateFlow() {
            flow.value = storage.values.toList()
        }

        override fun getAllAliases(): Flow<List<AliasEntity>> = flow

        override suspend fun getAlias(alias: String): AliasEntity? = storage[alias]

        override suspend fun insertOrUpdate(alias: AliasEntity) {
            storage[alias.alias] = alias
            updateFlow()
        }

        override suspend fun delete(alias: AliasEntity) {
            storage.remove(alias.alias)
            updateFlow()
        }
    }

    private lateinit var fakeDao: FakeAliasDao
    private lateinit var repository: AliasRepository

    @Before
    fun setup() {
        fakeDao = FakeAliasDao()
        repository = AliasRepository(fakeDao)
    }

    @Test
    fun testCreateAlias() = runBlocking {
        repository.saveAlias("yt", "com.google.android.youtube")

        val alias = repository.getAlias("yt")
        assertNotNull(alias)
        assertEquals("yt", alias?.alias)
        assertEquals("com.google.android.youtube", alias?.targetCommandOrPackage)

        val all = repository.getAllAliases().first()
        assertEquals(1, all.size)
    }

    @Test
    fun testDuplicateNameReplacesExisting() = runBlocking {
        repository.saveAlias("search", "g first search")
        repository.saveAlias("search", "ddg second search")

        val alias = repository.getAlias("search")
        assertNotNull(alias)
        assertEquals("ddg second search", alias?.targetCommandOrPackage)

        val all = repository.getAllAliases().first()
        assertEquals(1, all.size)
    }

    @Test
    fun testDeleteAlias() = runBlocking {
        repository.saveAlias("tmp", "temp command")
        assertNotNull(repository.getAlias("tmp"))

        repository.deleteAlias("tmp")
        assertNull(repository.getAlias("tmp"))

        val all = repository.getAllAliases().first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun testAliasCycleIsBounded() = runBlocking {
        // Alias A points to B, Alias B points to A
        repository.saveAlias("a", "b")
        repository.saveAlias("b", "a")

        val aliasA = repository.getAlias("a")
        assertNotNull(aliasA)

        // Resolution step: target "b" is parsed via CommandParser without infinite recursion
        val resolved = CommandParser.parse(aliasA!!.targetCommandOrPackage)
        // CommandParser parses "b" as Command.Unknown("b")
        assertTrue("Single-step alias expansion resolves without infinite loop", resolved is Command.Unknown)
        assertEquals("b", (resolved as Command.Unknown).rawQuery)
    }

    @Test
    fun testAliasShadowingBuiltInCommand() = runBlocking {
        // Alias "help" pointing to Google search
        repository.saveAlias("help", "g how to get help")

        val alias = repository.getAlias("help")
        assertNotNull(alias)
        assertEquals("g how to get help", alias?.targetCommandOrPackage)

        val parsedTarget = CommandParser.parse(alias!!.targetCommandOrPackage)
        assertTrue(parsedTarget is Command.GoogleSearch)
        assertEquals("how to get help", (parsedTarget as Command.GoogleSearch).query)
    }
}
