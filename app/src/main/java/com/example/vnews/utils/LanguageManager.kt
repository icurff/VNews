package com.example.vnews.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

object LanguageManager {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)

        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        config.setLocales(localeList)

        // Update the configuration for the resources
        resources.updateConfiguration(config, resources.displayMetrics)

        // Apply to context
        context.createConfigurationContext(config)

        // Apply changes to the activity if available
        if (context is Activity) {
            context.recreate()
        }
    }

    fun getCurrentLanguage(context: Context): String {
        return context.resources.configuration.locales[0].language
    }
}