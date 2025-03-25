package com.example.vnews.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "articles",
)
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "extension_name") val extensionName: String?,
    @ColumnInfo(name = "extension_icon") val extensionIcon: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "summary") val summary: String?,
    @ColumnInfo(name = "source") val source: String?,
    @ColumnInfo(name = "thumbnail") val thumbnail: String?,
    @ColumnInfo(name = "post_time") val postTime: Long = System.currentTimeMillis() / 1000
)

