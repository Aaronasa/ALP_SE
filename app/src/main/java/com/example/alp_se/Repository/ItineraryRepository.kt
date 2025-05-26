package com.example.alp_se.Repository

import com.example.alp_se.Model.CreateItineraryRequest
import com.example.alp_se.Model.GeneralResponseModel
import com.example.alp_se.Model.GetAllItineraryResponse
import com.example.alp_se.Model.UpdateItineraryRequest
import com.example.alp_se.Service.ItineraryService
import retrofit2.Response

interface ItineraryRepository {
    suspend fun getAllItineraries(): Response<GetAllItineraryResponse>
    suspend fun createItinerary(itinerary: CreateItineraryRequest): Response<GeneralResponseModel>
    suspend fun updateItinerary(id: Int, itinerary: UpdateItineraryRequest): Response<GeneralResponseModel>
    suspend fun deleteItinerary(id: Int): Response<GeneralResponseModel>
}

class NetworkItineraryRepository(
    private val itineraryService: ItineraryService
): ItineraryRepository {
    override suspend fun getAllItineraries(): Response<GetAllItineraryResponse> {
        return itineraryService.getAllItineraries()
    }

    override suspend fun createItinerary(itinerary: CreateItineraryRequest): Response<GeneralResponseModel> {
        return itineraryService.createItinerary(itinerary)
    }

    override suspend fun updateItinerary(id: Int ,itinerary: UpdateItineraryRequest): Response<GeneralResponseModel> {
        return itineraryService.updateItinerary(id, itinerary)
    }

    override suspend fun deleteItinerary(id: Int): Response<GeneralResponseModel> {
        return itineraryService.deleteItinerary(id)
    }
}