package com.example.vnews.ui.user_setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import com.example.vnews.ui.home.LayoutType

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppSettingsManager(private val context: Context) {
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val languageKey = stringPreferencesKey("language")
    private val layoutTypeKey = stringPreferencesKey("layout_type")

    val appSettings: Flow<AppSettings> = context.dataStore.data
        .map { preferences ->
            AppSettings(
                isDarkTheme = preferences[darkThemeKey] ?: false,
                language = preferences[languageKey] ?: "en",
                layoutType = LayoutType.valueOf(preferences[layoutTypeKey] ?: LayoutType.LIST.name)
            )
        }

    suspend fun setDarkTheme(isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[darkThemeKey] = isDarkTheme
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = language
        }
    }
    
    suspend fun setLayoutType(layoutType: LayoutType) {
        context.dataStore.edit { preferences ->
            preferences[layoutTypeKey] = layoutType.name
        }
    }

    suspend fun getAppSettings(): AppSettings? {
        return try {
            appSettings.first()
        } catch (e: Exception) {
            null
        }
    }
} 