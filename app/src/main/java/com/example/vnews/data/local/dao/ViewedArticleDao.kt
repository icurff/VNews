package com.example.vnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vnews.data.local.entity.ArticleEntity
import com.example.vnews.data.local.entity.ViewedArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ViewedArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markArticleAsViewed(viewedArticle: ViewedArticleEntity)

}