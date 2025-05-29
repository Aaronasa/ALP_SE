package com.example.alp_se.Model

data class GetAllItineraryDayResponse(
    val data: List<ItineraryDayModel>
)


data class ItineraryDayModel(
    val id: Int = 0,
    val day: String = "",
    val start_time: String = "",
    val end_time: String = "",
    val activity_description: String = "",
    val meeting_time: String = "",
    val itineraryId: Int = 0
)

data class CreateItineraryDayRequest(
    val day: String,
    val start_time: String,
    val end_time: String,
    val activity_description: String,
    val meeting_time: String,
    val itineraryId: Int
)

data class UpdateItineraryDayRequest(
    val id: Int,
    val day: String? = null,
    val start_time: String? = null,
    val end_time: String? = null,
    val activity_description: String? = null,
    val meeting_time: String? = null,
    val itineraryId: Int? = null
)

data class DeleteItineraryDayRequest(
    val id: Int
)
