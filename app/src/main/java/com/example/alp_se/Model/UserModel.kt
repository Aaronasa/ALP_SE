package com.example.alp_se.Model

data class UserResponses(
    val data: List<UserModel>
)

data class EmailRequest(
    val email: String
)

data class UserResponse(
    val data: UserModel?,
    val status: String,
    val message: String?
)

data class UserModel(
    val id: Int,
    val username: String,
    val email: String,
    val token: String,
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val data: UserModel
)

data class LogoutResponse(
    val message: String
)

data class DeleteResponse(
    val message: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)

data class UpdateUserRequest(
    val username: String,
    val email: String
)
data class RegisterResponse(
    val message: String,
    val data: UserData
)

data class UserData(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val token: String?,
)