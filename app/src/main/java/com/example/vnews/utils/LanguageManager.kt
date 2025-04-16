package com.example.vnews.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)

        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

    fun getCurrentLanguage(context: Context): String {
        return context.resources.configuration.locales[0].language
    }
}