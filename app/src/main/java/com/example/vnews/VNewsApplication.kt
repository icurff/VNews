package com.example.vnews

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.example.vnews.widget.LatestArticleWidget
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VNewsApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()

        Handler(Looper.getMainLooper()).postDelayed({
            LatestArticleWidget.updateAllWidgets(this)
        }, 3000)
    }
} 