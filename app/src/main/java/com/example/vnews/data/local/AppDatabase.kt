package com.example.vnews.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vnews.data.local.dao.ArticleDao
import com.example.vnews.data.local.dao.ExtensionDao
import com.example.vnews.data.local.dao.RepoDao
import com.example.vnews.data.local.dao.SavedArticleDao
import com.example.vnews.data.local.dao.ViewedArticleDao
import com.example.vnews.data.local.entity.ArticleEntity
import com.example.vnews.data.local.entity.ExtensionEntity
import com.example.vnews.data.local.entity.RepositoryEntity
import com.example.vnews.data.local.entity.SavedArticleEntity
import com.example.vnews.data.local.entity.ViewedArticleEntity

@Database(
    entities = [
        ArticleEntity::class,
        SavedArticleEntity::class,
        ViewedArticleEntity::class,
        ExtensionEntity::class,
        RepositoryEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun savedArticleDao(): SavedArticleDao
    abstract fun viewedArticleDao(): ViewedArticleDao
    abstract fun repoDao(): RepoDao
    abstract fun extensionDao(): ExtensionDao
}

