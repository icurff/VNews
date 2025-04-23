package com.example.vnews.utils

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

object PermissionManager {
    @Composable
    fun RequestPermission(
        permission: String,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit = {}
    ) {
        var permissionRequested by remember { mutableStateOf(false) }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

        LaunchedEffect(permissionRequested) {
            if (!permissionRequested) {
                permissionLauncher.launch(permission)
                permissionRequested = true
            }
        }
    }
} 