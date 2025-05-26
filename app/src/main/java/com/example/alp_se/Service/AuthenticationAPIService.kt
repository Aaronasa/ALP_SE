package com.example.alp_se.Service


import android.util.Log
import com.example.alp_se.Model.DeleteResponse
import com.example.alp_se.Model.EmailRequest
import com.example.alp_se.Model.LoginRequest
import com.example.alp_se.Model.LoginResponse
import com.example.alp_se.Model.LogoutResponse
import com.example.alp_se.Model.RegisterRequest
import com.example.alp_se.Model.RegisterResponse
import com.example.alp_se.Model.UpdateUserRequest
import com.example.alp_se.Model.UserResponse
import com.example.alp_se.Model.UserResponses
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthenticationAPIService {

    // Create User (Register)
    @POST("public/create")
    suspend fun registerUser(@Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("public/login")
    suspend fun loginUser(@Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("/auth/read")
    suspend fun getUserData(
        @Header("x-API-Token") token: String,
        @Body emailRequest: EmailRequest
    ): Response<UserResponse>

    @POST("/auth/logout")
    suspend fun logout(
        @Header("x-API-Token") token: String
    ): Response<LogoutResponse>

    @PUT("/auth/update")
    suspend fun updateUser(
        @Header("x-API-Token") token: String,
        @Body request: UpdateUserRequest
    ): Response<UserResponse>

    @DELETE("/auth/delete")
    suspend fun deleteUser(
        @Header("x-API-Token") token: String
    ): Response<DeleteResponse>

    @GET("/admin/read/all")
    suspend fun getAllUsers(
        @Header("x-API-Token") token: String
    ): UserResponses

}
