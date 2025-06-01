package com.example.alp_se.Component

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@Composable
fun StatusSnackbar(
    statusMessage: String?,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    LaunchedEffect(statusMessage) {
        statusMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            // Auto dismiss after showing
            delay(3000)
            onDismiss()
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = if (statusMessage?.contains("successfully") == true) {
                    Color(0xFF4CAF50) // Green for success
                } else {
                    Color(0xFFF44336) // Red for error
                },
                contentColor = Color.White,
                actionColor = Color.White
            )
        }
    )
}

// Extension function to clear status message
@Composable
fun rememberStatusSnackbar(
    statusMessage: String?,
    onStatusMessageCleared: () -> Unit
): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            // Clear the status message after showing
            delay(100)
            onStatusMessageCleared()
        }
    }

    return snackbarHostState
}