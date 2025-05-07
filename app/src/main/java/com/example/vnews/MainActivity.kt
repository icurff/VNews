package com.example.vnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.vnews.ui.navigation.NavGraph
import com.example.vnews.ui.theme.VNewsTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.vnews.data.datastore.AppSettingsManager
import com.example.vnews.utils.LanguageManager
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appSettingsManager: AppSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize AppSettingsManager
        appSettingsManager = AppSettingsManager(this)
        
        // Apply saved language before setting content
        runBlocking {
            val appSettings = appSettingsManager.getAppSettings()
            appSettings?.language?.let { language ->
                LanguageManager.setLocale(this@MainActivity, language)
            }
        }
        
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            VNewsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        appSettingsManager = appSettingsManager
                    )
                }
            }
        }
    }
}
