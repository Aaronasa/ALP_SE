package com.example.alp_se.Repository


import com.example.alp_se.Model.CreateItineraryDayRequest
import com.example.alp_se.Model.GeneralResponseModel
import com.example.alp_se.Model.GetAllItineraryDayResponse
import com.example.alp_se.Model.UpdateItineraryDayRequest
import com.example.alp_se.Service.ItineraryDayService // Use the ItineraryDayService
import retrofit2.Response


interface ItineraryDayRepository {
    suspend fun getAllItineraryDays(): Response<GetAllItineraryDayResponse>
    suspend fun createItineraryDay(itineraryDay: CreateItineraryDayRequest): Response<GeneralResponseModel>
    suspend fun updateItineraryDay(id: Int, itineraryDay: UpdateItineraryDayRequest): Response<GeneralResponseModel>
    suspend fun deleteItineraryDay(id: Int): Response<GeneralResponseModel>
}

class NetworkItineraryDayRepository(
    private val itineraryDayService: ItineraryDayService // Inject ItineraryDayService
): ItineraryDayRepository {
    override suspend fun getAllItineraryDays(): Response<GetAllItineraryDayResponse> {
        return itineraryDayService.getAllItineraryDays()
    }

    override suspend fun createItineraryDay(itineraryDay: CreateItineraryDayRequest): Response<GeneralResponseModel> {
        return itineraryDayService.createItineraryDay(itineraryDay)
    }

    override suspend fun updateItineraryDay(id: Int, itineraryDay: UpdateItineraryDayRequest): Response<GeneralResponseModel> {
        return itineraryDayService.updateItineraryDay(id, itineraryDay)
    }

    override suspend fun deleteItineraryDay(id: Int): Response<GeneralResponseModel> {
        return itineraryDayService.deleteItineraryDay(id)
    }
}


