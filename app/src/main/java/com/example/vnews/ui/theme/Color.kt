package com.example.vnews.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


// Gradient colors for light theme
val LightGradientStart = Color(0xFF00D1B2)
val LightGradientEnd = Color(0xFF007CF0)

// Gradient colors for dark theme
val DarkGradientStart = Color(0xFF005F50)
val DarkGradientEnd = Color(0xFF003A70)

// Primary colors derived from gradient colors
val LightPrimaryColor = Color(0xFF00A5D1) // A blend of the light gradient colors
val DarkPrimaryColor = Color(0xFF0087A0) // Lighter version for dark theme

@Composable
fun appGradient(isDarkTheme: Boolean): Brush {
    return if (isDarkTheme) {
        // Darker gradient for dark theme
        Brush.linearGradient(
            colors = listOf(DarkGradientStart, DarkGradientEnd),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    } else {
        // Lighter gradient for light theme
        Brush.linearGradient(
            colors = listOf(LightGradientStart, LightGradientEnd),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    }
}