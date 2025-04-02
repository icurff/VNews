package com.example.vnews.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vnews.data.local.entity.ExtensionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtensionDao {
    @Query("SELECT * FROM extensions")
    fun getExtensions(): Flow<List<ExtensionEntity>>

    @Query("SELECT * FROM extensions WHERE source = :source")
    fun getExtensionsByLink(source: String): Flow<List<ExtensionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExtension(extension: ExtensionEntity)

    @Delete
    suspend fun deleteExtension(extension: ExtensionEntity)
}