package com.example.alp_se.Service

import com.example.alp_se.Model.CreateItineraryDayRequest
import com.example.alp_se.Model.GetAllItineraryDayResponse
import com.example.alp_se.Model.GeneralResponseModel
import com.example.alp_se.Model.UpdateItineraryDayRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ItineraryDayService {
    @GET("/itineraryDays")
    suspend fun getAllItineraryDays(): Response<GetAllItineraryDayResponse>

    @POST("/itineraryDays")
    suspend fun createItineraryDay(
        @Body createItineraryDayRequest: CreateItineraryDayRequest
    ): Response<GeneralResponseModel>


    @PUT("/itineraryDays/{id}")
    suspend fun updateItineraryDay(
        @Path("id") id: Int,
        @Body updateItineraryDayRequest: UpdateItineraryDayRequest
    ): Response<GeneralResponseModel>

    @DELETE("/itineraryDays/{id}")
    suspend fun deleteItineraryDay(
        @Path("id") id: Int
    ): Response<GeneralResponseModel>
}
