package com.example.vnews.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vnews.data.local.entity.RepositoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repositories")
    fun getRepos(): Flow<List<RepositoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo: RepositoryEntity)

    @Delete
    suspend fun deleteRepo(repo: RepositoryEntity)

    @Update
    suspend fun updateRepo(repo: RepositoryEntity)
} 