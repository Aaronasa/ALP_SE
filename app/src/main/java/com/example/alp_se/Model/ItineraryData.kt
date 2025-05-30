package com.example.alp_se.Model

data class ItineraryData(
    val dayTitle: String,
    val meetingTime: String,
    val sneakPeekDesc: String,
    val location: String? = null,
    val isCompleted: Boolean = false
)