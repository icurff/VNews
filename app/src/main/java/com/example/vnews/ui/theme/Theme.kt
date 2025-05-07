package com.example.vnews.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vnews.ui.user_setting.AppSettingsViewModel

private val DarkColorScheme = darkColorScheme(
    primary = NewsBlue,
    secondary = NewsBlue,
)

private val LightColorScheme = lightColorScheme(
    primary = NewsBlue,
    secondary = NewsBlue,
)

@Composable
fun VNewsTheme(
    appSettingsViewModel: AppSettingsViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val appSettings by appSettingsViewModel.appSettings.collectAsState()
    val darkTheme = appSettings.isDarkTheme

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context).copy(
                primary = NewsBlue,
                secondary = NewsBlue,
            ) else dynamicLightColorScheme(context).copy(
                primary = NewsBlue,
                secondary = NewsBlue,
            )
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}