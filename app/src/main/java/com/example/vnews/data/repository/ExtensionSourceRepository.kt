package com.example.vnews.data.repository

import com.example.vnews.data.local.dao.RepoDao
import com.example.vnews.data.local.entity.RepositoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionSourceRepository @Inject constructor(
    private val repoDao: RepoDao
) {

    fun getRepos(): Flow<List<RepositoryEntity>> {
        return repoDao.getRepos()
    }

    suspend fun addRepo(sourceName: String, sourceUrl: String) {
        val repoEntity = RepositoryEntity(sourceName = sourceName, source = sourceUrl)
        repoDao.insertRepo(repoEntity)
    }

    suspend fun deleteRepo(repo: RepositoryEntity) {
        withContext(Dispatchers.IO) {
            repoDao.deleteRepo(repo)
        }
    }

    suspend fun updateRepo(repo: RepositoryEntity) {
        withContext(Dispatchers.IO) {
            repoDao.updateRepo(repo)
        }
    }
}