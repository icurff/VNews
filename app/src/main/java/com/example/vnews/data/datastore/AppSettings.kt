package com.example.vnews.data.datastore

import com.example.vnews.ui.home.LayoutType

data class AppSettings(
    val isDarkTheme: Boolean = false,
    val language: String = "en",
    val layoutType: LayoutType = LayoutType.LIST
) 