package com.example.alp_se.Model

import com.google.gson.annotations.SerializedName

data class GetAllItineraryResponse(
    val data: List<ItineraryModel>
)

data class ItineraryModel(
    val id: Int = 0,
    val title: String = "",
    val start_date: String = "",
    val end_date: String = "",
    val date: String = "",
    val estimate_Start: String = "",
    val estimate_End: String = "",
    val total_person: Int = 0,
    val country: String = "",
    val location: String = ""
)

data class CreateItineraryRequest(
    val title: String,
    val start_date: String,     // e.g. "2025-06-10T08:00:00"
    val end_date: String,
    val estimate_start: String,
    val estimate_end: String,
    val total_person: Int,
    val country: String,
    val location: String,
    val userId: Int
)

data class UpdateItineraryRequest(
    val id: Int,
    val title: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val estimate_start: String? = null,
    val estimate_end: String? = null,
    val total_person: Int? = null,
    val country: String? = null,
    val location: String? = null,
    val userId: Int? = null
)

data class DeleteItineraryRequest(
    val id: Int
)