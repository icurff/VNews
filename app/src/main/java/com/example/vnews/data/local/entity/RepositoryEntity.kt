package com.example.vnews.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceName: String,
    val source: String,
    val isDefault: Boolean = false,
)