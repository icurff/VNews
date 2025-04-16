package com.example.vnews.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vnews.data.local.AppDatabase
import com.example.vnews.data.local.dao.ArticleDao
import com.example.vnews.data.local.dao.ExtensionDao
import com.example.vnews.data.local.dao.RepoDao
import com.example.vnews.data.local.dao.SavedArticleDao
import com.example.vnews.data.local.dao.ViewedArticleDao
import com.example.vnews.data.local.entity.RepositoryEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callback: RoomDatabase.Callback
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "vnews_database"
        ).addCallback(callback).build()
    }
    
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }
    
    @Provides
    @Singleton
    fun provideRoomDatabaseCallback(
        appDatabaseProvider: Provider<AppDatabase>,
        scope: CoroutineScope
    ): RoomDatabase.Callback {
        return object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch {
                    val repoDao = appDatabaseProvider.get().repoDao()
                    repoDao.insertRepo(
                        RepositoryEntity(
                            sourceName = "Default",
                            source = "https://raw.githubusercontent.com/icurff/VNewsExtensions/main/repository.json",
                            isDefault = true,
                        )
                    )
                }
            }
        }
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