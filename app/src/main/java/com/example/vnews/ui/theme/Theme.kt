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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vnews.ui.user_setting.AppSettingsViewModel

// Lighter versions of dark colors for dark theme
private val LighterDarkGradientStart = Color(0xFF00967E)
private val LighterDarkGradientEnd = Color(0xFF0059A3)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryColor,
    secondary = LighterDarkGradientStart,
    tertiary = LighterDarkGradientEnd
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryColor,
    secondary = LightGradientStart,
    tertiary = LightGradientEnd

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
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
                primary = DarkPrimaryColor,
                secondary = LighterDarkGradientStart,
                tertiary = LighterDarkGradientEnd
            ) else dynamicLightColorScheme(context).copy(
                primary = LightPrimaryColor,
                secondary = LightGradientStart,
                tertiary = LightGradientEnd
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