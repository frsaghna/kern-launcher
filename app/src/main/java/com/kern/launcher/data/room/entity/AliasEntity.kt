package com.kern.launcher.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aliases")
data class AliasEntity(
    @PrimaryKey val alias: String,
    val targetCommandOrPackage: String
)
