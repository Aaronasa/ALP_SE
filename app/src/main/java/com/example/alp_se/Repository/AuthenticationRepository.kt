package com.example.alp_se.Repository

import android.util.Log
import com.example.alp_se.Model.DeleteResponse
import com.example.alp_se.Model.LoginRequest
import com.example.alp_se.Model.LoginResponse
import com.example.alp_se.Model.LogoutResponse
import com.example.alp_se.Model.RegisterRequest
import com.example.alp_se.Model.RegisterResponse
import com.example.alp_se.Model.UpdateUserRequest
import com.example.alp_se.Model.UserModel
import com.example.alp_se.Model.UserResponse
import com.example.alp_se.Service.AuthenticationAPIService
import retrofit2.Response
import retrofit2.http.Header

// Interface for AuthenticationRepository
interface AuthenticationRepository {
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Response<RegisterResponse>

    suspend fun login(email: String, password: String): Response<LoginResponse>
    suspend fun logout(token: String): Result<String>
    suspend fun delete(token: String): Result<String>
    suspend fun updateUser(token: String, username: String, email: String): Response<UserResponse>
    suspend fun getalluser(token: String): List<UserModel>
}

// Network Authentication Repository implementation
class NetworkAuthenticationRepository(
    private val authenticationAPIService: AuthenticationAPIService
) : AuthenticationRepository {

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Response<RegisterResponse> {
        // Validate input
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("All fields must be filled")
        }

        // Prepare the registration request
        val request = RegisterRequest(username, email, password)

        return try {
            // Call the API to register the user
            authenticationAPIService.registerUser(request)
        } catch (e: Exception) {
            throw Exception("Failed to register: ${e.localizedMessage}", e)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Response<LoginResponse> {
        // Validate input
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Email and password are required")
        }

        // Prepare the login request
        val request = LoginRequest(email, password)

        return try {
            // Call the API to login the user
            authenticationAPIService.loginUser(request)
        } catch (e: Exception) {
            throw Exception("Failed to login: ${e.localizedMessage}", e)
        }
    }

    override suspend fun logout(token: String): Result<String> {
        return try {
            val response: Response<LogoutResponse> =
                authenticationAPIService.logout(token.trim()) // Tipe eksplisit
            if (response.isSuccessful) {
                val message: String = response.body()?.message ?: "Logout successful."
                Result.success(message)
            } else {
                val errorMessage: String = response.errorBody()?.string() ?: "Failed to logout"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete (token: String): Result<String> {
        return try {
            val response: Response<DeleteResponse> =
                authenticationAPIService.deleteUser(token.trim()) // Tipe eksplisit
            if (response.isSuccessful) {
                val message: String = response.body()?.message ?: "Delete successful."
                Result.success(message)
            } else {
                val errorMessage: String = response.errorBody()?.string() ?: "Failed to logout"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(
        token: String,
        username: String,
        email: String
    ): Response<UserResponse> {
        if (username.isBlank() || email.isBlank()) {
            throw IllegalArgumentException("All fields must be filled")
        }
        // Prepare the update request
        val request = UpdateUserRequest(username, email)

        return try {
            // Call the API to update user information
            authenticationAPIService.updateUser(token, request)
        } catch (e: Exception) {
            throw Exception("Failed to update user: ${e.localizedMessage}", e)
        }
    }

    override suspend fun getalluser(token: String): List<UserModel> {
        return try {
            val response = authenticationAPIService.getAllUsers(token)
            response.data

        } catch (e: Exception) {
            throw Exception("Failed to get all users: ${e.localizedMessage}", e)
        }
    }
}
