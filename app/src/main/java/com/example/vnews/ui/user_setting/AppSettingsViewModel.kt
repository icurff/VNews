package com.example.vnews.ui.user_setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vnews.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val appSettingsManager = AppSettingsManager(context)

    val appSettings: StateFlow<AppSettings> = appSettingsManager.appSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun setDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            appSettingsManager.setDarkTheme(isDarkTheme)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            appSettingsManager.setLanguage(language)
            LanguageManager.setLocale(context, language)
        }
    }

    fun getCurrentLanguage(): String {
        return LanguageManager.getCurrentLanguage(context)
    }
} 