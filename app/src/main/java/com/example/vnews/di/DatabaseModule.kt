package com.example.vnews.di

import android.content.Context
import androidx.room.Room
import com.example.vnews.data.local.AppDatabase
import com.example.vnews.data.local.dao.ArticleDao
import com.example.vnews.data.local.dao.ExtensionDao
import com.example.vnews.data.local.dao.RepoDao
import com.example.vnews.data.local.dao.SavedArticleDao
import com.example.vnews.data.local.dao.ViewedArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "vnews_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(database: AppDatabase): ArticleDao {
        return database.articleDao()
    }

    @Provides
    @Singleton
    fun provideViewedArticleDao(database: AppDatabase): ViewedArticleDao {
        return database.viewedArticleDao()
    }

    @Provides
    @Singleton
    fun provideSavedArticleDao(database: AppDatabase): SavedArticleDao {
        return database.savedArticleDao()
    }

    @Provides
    @Singleton
    fun provideRepoDao(database: AppDatabase): RepoDao {
        return database.repoDao()
    }

    @Provides
    @Singleton
    fun provideExtensionDao(database: AppDatabase): ExtensionDao {
        return database.extensionDao()
    }
}