package com.example.vnews.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import com.example.vnews.MainActivity
import com.example.vnews.R
import com.example.vnews.data.data_provider.DefaultExtension
import com.example.vnews.data.data_provider.ExtensionEntities
import com.example.vnews.utils.DateTimeUtil
import com.prof18.rssparser.RssParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

class LatestArticleWidget : AppWidgetProvider() {
    
    private val widgetCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateInterval = TimeUnit.MINUTES.toMillis(5) // 5 minutes

    companion object {
        private const val ACTION_REFRESH = "com.example.vnews.ACTION_WIDGET_REFRESH"
        private const val ACTION_NEXT = "com.example.vnews.ACTION_WIDGET_NEXT"
        private const val ACTION_PREVIOUS = "com.example.vnews.ACTION_WIDGET_PREVIOUS"
        private const val CURRENT_ARTICLE_INDEX = "current_article_index"
        private const val MAX_ARTICLES = 5
        

        private val NEWS_SOURCES = ExtensionEntities.tinMoi

        private var cachedArticles = ArrayList<ArticleInfo>()

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, LatestArticleWidget::class.java)
            )
            
            val intent = Intent(context, LatestArticleWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            
            context.sendBroadcast(intent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, LatestArticleWidget::class.java)
        )
        
        when (intent.action) {
            ACTION_REFRESH -> {
                fetchArticles(context, appWidgetManager, appWidgetIds)
            }
            ACTION_NEXT -> {
                for (appWidgetId in appWidgetIds) {
                    val preferences = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                    val currentIndex = preferences.getInt("$CURRENT_ARTICLE_INDEX$appWidgetId", 0)
                    val nextIndex = if (currentIndex >= cachedArticles.size - 1) 0 else currentIndex + 1
                    
                    preferences.edit().putInt("$CURRENT_ARTICLE_INDEX$appWidgetId", nextIndex).apply()
                    updateArticleDisplay(context, appWidgetManager, appWidgetId, nextIndex)
                }
            }
            ACTION_PREVIOUS -> {
                for (appWidgetId in appWidgetIds) {
                    val preferences = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                    val currentIndex = preferences.getInt("$CURRENT_ARTICLE_INDEX$appWidgetId", 0)
                    val prevIndex = if (currentIndex <= 0) (cachedArticles.size - 1) else currentIndex - 1
                    
                    preferences.edit().putInt("$CURRENT_ARTICLE_INDEX$appWidgetId", prevIndex).apply()
                    updateArticleDisplay(context, appWidgetManager, appWidgetId, prevIndex)
                }
            }
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                if (cachedArticles.isEmpty()) {
                    fetchArticles(context, appWidgetManager, appWidgetIds)
                } else {
                    for (appWidgetId in appWidgetIds) {
                        val preferences = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                        val currentIndex = preferences.getInt("$CURRENT_ARTICLE_INDEX$appWidgetId", 0)
                        updateArticleDisplay(context, appWidgetManager, appWidgetId, currentIndex)
                    }
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        if (cachedArticles.isEmpty()) {
            fetchArticles(context, appWidgetManager, appWidgetIds)
        } else {
        appWidgetIds.forEach { appWidgetId ->
                val preferences = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                val currentIndex = preferences.getInt("$CURRENT_ARTICLE_INDEX$appWidgetId", 0)
                updateArticleDisplay(context, appWidgetManager, appWidgetId, currentIndex)
            }
        }
        
        scheduleNextUpdate(context)
    }
    
    private fun scheduleNextUpdate(context: Context) {
        updateHandler.removeCallbacksAndMessages(null)
        updateHandler.postDelayed({
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, LatestArticleWidget::class.java)
            )
            fetchArticles(context, appWidgetManager, appWidgetIds)
        }, updateInterval)
    }
    
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        updateHandler.removeCallbacksAndMessages(null)
    }
    
    private fun fetchArticles(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val loadingViews = RemoteViews(context.packageName, R.layout.widget_latest_article)
            loadingViews.setTextViewText(R.id.widget_article_title, "Loading latest news...")
            
            val refreshIntent = Intent(context, LatestArticleWidget::class.java).apply {
                action = ACTION_REFRESH
            }
            
            val refreshPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, flags)
            
            loadingViews.setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, loadingViews)
        }
        
        widgetCoroutineScope.launch {
            val allArticles = withContext(Dispatchers.IO) {
                val tempArticles = ArrayList<ArticleInfo>()
                
                NEWS_SOURCES.forEach { extension ->
                    val articles = fetchArticlesFromSource(extension)
                    tempArticles.addAll(articles)
                }
                
                tempArticles.sortByDescending { it.pubTime }
                tempArticles.take(MAX_ARTICLES)
            }
            
            synchronized(cachedArticles) {
                cachedArticles.clear()
                cachedArticles.addAll(allArticles)
            }
            
            if (cachedArticles.isNotEmpty()) {
                appWidgetIds.forEach { appWidgetId ->
                    val preferences = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                    preferences.edit().putInt("$CURRENT_ARTICLE_INDEX$appWidgetId", 0).apply()
                    updateArticleDisplay(context, appWidgetManager, appWidgetId, 0)
                }
                } else {
                appWidgetIds.forEach { appWidgetId ->
                    val errorViews = RemoteViews(context.packageName, R.layout.widget_latest_article)
                    errorViews.setTextViewText(R.id.widget_article_title, "Could not load latest news. Tap to retry.")
                    
                    val refreshIntent = Intent(context, LatestArticleWidget::class.java).apply {
                        action = ACTION_REFRESH
                    }
                    
                    val refreshPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, flags)
                    
                    errorViews.setOnClickPendingIntent(R.id.widget_container, refreshPendingIntent)
                    errorViews.setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)
                    
                    appWidgetManager.updateAppWidget(appWidgetId, errorViews)
                }
            }
        }
    }
    
    private fun updateArticleDisplay(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        articleIndex: Int
    ) {
        if (cachedArticles.isEmpty() || articleIndex >= cachedArticles.size) {
            return
        }
        
        val article = cachedArticles[articleIndex]
        val views = RemoteViews(context.packageName, R.layout.widget_latest_article)
        
        views.setTextViewText(R.id.widget_article_title, article.title)
        views.setTextViewText(R.id.widget_source_name, article.extensionName)
        views.setTextViewText(R.id.widget_pub_time, DateTimeUtil.getRelativeTimeString(article.pubTime))
        
        val openIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            
            putExtra("SOURCE_URL", article.source.takeIf { it.isNotEmpty() } ?: "https://vnexpress.net")
            putExtra("ARTICLE_TITLE", article.title.takeIf { it.isNotEmpty() } ?: "No Title")
            putExtra("ARTICLE_SUMMARY", article.summary.takeIf { it.isNotEmpty() } ?: "No summary available")
            putExtra("ARTICLE_THUMBNAIL", article.thumbnail.takeIf { it.isNotEmpty() } ?: "")
            putExtra("ARTICLE_PUBTIME", article.pubTime.takeIf { it > 0 } ?: System.currentTimeMillis())
            putExtra("EXTENSION_NAME", article.extensionName.takeIf { it.isNotEmpty() } ?: "Unknown Source")
            putExtra("EXTENSION_ICON", article.extensionIcon.takeIf { it.isNotEmpty() } ?: "")
            putExtra("OPEN_ARTICLE", true)
        }
        
        val openPendingIntent = PendingIntent.getActivity(context, appWidgetId, openIntent, flags)
        
        val prevIntent = Intent(context, LatestArticleWidget::class.java).apply {
            action = ACTION_PREVIOUS
        }
        
        val nextIntent = Intent(context, LatestArticleWidget::class.java).apply {
            action = ACTION_NEXT
        }
        
        val refreshIntent = Intent(context, LatestArticleWidget::class.java).apply {
            action = ACTION_REFRESH
        }
        
        val prevPendingIntent = PendingIntent.getBroadcast(context, 10, prevIntent, flags)
        val nextPendingIntent = PendingIntent.getBroadcast(context, 11, nextIntent, flags)
        val refreshPendingIntent = PendingIntent.getBroadcast(context, 12, refreshIntent, flags)
        
        views.setOnClickPendingIntent(R.id.widget_container, openPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_article_title, openPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_btn_previous, prevPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_btn_next, nextPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    private suspend fun fetchArticlesFromSource(extension: DefaultExtension): List<ArticleInfo> {
        return try {
            val rssParser = RssParser()
            val channel = rssParser.getRssChannel(extension.source)
            
            channel.items.map { item ->
                ArticleInfo(
                    title = Jsoup.parse(item.title ?: "").text().replace("&apos;", "'"),
                    summary = Jsoup.parse(item.description ?: "").text(),
                    source = item.link ?: "",
                    pubTime = DateTimeUtil.parseDateToUnix(item.pubDate ?: ""),
                    thumbnail = item.image ?: "",
                    extensionName = extension.name,
                    extensionIcon = extension.icon
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    

    data class ArticleInfo(
        val title: String,
        val summary: String,
        val source: String,
        val pubTime: Long,
        val thumbnail: String,
        val extensionName: String,
        val extensionIcon: String
    )
} 