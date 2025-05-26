package com.example.alp_se.service


sealed class UserStatusUIState {
    object Idle : UserStatusUIState() // Initial state or no actions
    object Loading : UserStatusUIState() // When an action is in progress
    object Success : UserStatusUIState() // When an action (e.g., update, delete) succeeds
    data class Error(val message: String) : UserStatusUIState() // When an action fails
}

