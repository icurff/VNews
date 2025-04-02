package com.example.vnews.data.repository

import com.example.vnews.data.local.dao.ExtensionDao
import com.example.vnews.data.local.entity.ExtensionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionRepository @Inject constructor(
    private val extensionDao: ExtensionDao,
) {
    fun getInstalledExt(): Flow<List<ExtensionEntity>> {
        return extensionDao.getExtensions()
    }

    suspend fun installExtension(ext: ExtensionEntity) {
        extensionDao.insertExtension(ext)
    }

    suspend fun deleteInstalledExt(extension: ExtensionEntity) {
        withContext(Dispatchers.IO) {
            extensionDao.deleteExtension(extension)
        }
    }
}