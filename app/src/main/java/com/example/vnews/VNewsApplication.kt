package com.example.vnews

import android.app.Application
import com.example.vnews.widget.LatestArticleWidget
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VNewsApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()

        LatestArticleWidget.scheduleWidgetUpdates(this)
    }
} 