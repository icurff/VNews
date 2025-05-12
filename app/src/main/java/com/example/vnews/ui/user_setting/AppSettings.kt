package com.example.vnews.ui.user_setting

import com.example.vnews.ui.home.LayoutType

data class AppSettings(
    val isDarkTheme: Boolean = false,
    val language: String = "en",
    val layoutType: LayoutType = LayoutType.LIST
) 