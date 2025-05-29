package com.example.alp_se.Service

import com.example.alp_se.Model.CreateItineraryRequest
import com.example.alp_se.Model.GeneralResponseModel
import com.example.alp_se.Model.GetAllItineraryResponse
import com.example.alp_se.Model.UpdateItineraryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ItineraryService {
    @GET("/itineraries")
    suspend fun getAllItineraries(): Response<GetAllItineraryResponse>

    @POST("/itineraries")
    suspend fun createItinerary(
        @Body createItineraryRequest: CreateItineraryRequest
    ): Response<GeneralResponseModel> // return string message saja

    @PUT("/itineraries/{id}")
    suspend fun updateItinerary(
        @Path("id") id: Int,
        @Body updateItineraryRequest: UpdateItineraryRequest
    ): Response<GeneralResponseModel> // return string message saja

    @DELETE("/itineraries/{id}")
    suspend fun deleteItinerary(
        @Path("id") id: Int
    ): Response<GeneralResponseModel>// return string message saja

}