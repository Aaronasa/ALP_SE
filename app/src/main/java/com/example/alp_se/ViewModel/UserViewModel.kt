//package com.example.alp_se.ViewModel
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import android.content.Context
//import androidx.navigation.NavHostController
//import com.example.alp_se.Model.UserModel
//import com.example.alp_se.models.RestaurantModel
//import com.example.alp_se.repositories.AuthenticationRepository
//import com.example.alp_se.repositories.NetworkAuthenticationRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//
//
//class UserViewModel : ViewModel() {
//
//    private  val repository = NetworkAuthenticationRepository(AppContainer.authService)
//
//    // LiveData for User UI State
//    private val _userState = MutableLiveData<UserUIState>()
//    val userState: LiveData<UserUIState> get() = _userState
//
//    // LiveData for User Status UI State
//    private val _statusState = MutableLiveData<UserStatusUIState>()
//    val statusState: LiveData<UserStatusUIState> get() = _statusState
//
//    private val _AllUser = MutableStateFlow<List<UserModel>>(emptyList())
//    val AllUser = _AllUser
//
//    // API Services from AppContainer
//    private val authService = AppContainer.authService
//
//
//    // Register User
//    fun registerUser(username: String, email: String, password: String) {
//        if (username.isBlank() || email.isBlank() || password.isBlank()) {
//            _statusState.postValue(UserStatusUIState.Error("All fields are required"))
//            return
//        }
//
//        _statusState.value = UserStatusUIState.Loading
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val request = RegisterRequest(email, password, username)
//                val response = authService.registerUser(request)
//
//                if (response.isSuccessful) {
//                    val registerResponse = response.body()
//                    if (registerResponse != null) {
//                        Log.d("RegisterViewModel", "Registration successful: ${registerResponse.message}")
//                        _statusState.postValue(UserStatusUIState.Success)
//                    } else {
//                        Log.e("RegisterViewModel", "Error: Response body is null")
//                        _statusState.postValue(UserStatusUIState.Error("Response body is null"))
//                    }
//                } else {
//                    Log.e("RegisterViewModel", "Error: ${response.code()}, Message: ${response.message()}")
//                    _statusState.postValue(UserStatusUIState.Error("Error: ${response.code()}, Message: ${response.message()}"))
//                }
//            } catch (e: Exception) {
//                Log.e("RegisterViewModel", "Error during registration: ${e.localizedMessage}")
//                _statusState.postValue(UserStatusUIState.Error(e.localizedMessage ?: "Failed to register user"))
//            }
//        }
//    }
//
//    fun logout(token: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // Panggil fungsi logout dari service
//                val response = authService.logout(token)
//
//                val result: Result<String> = if (response.isSuccessful) {
//                    // Ambil pesan dari body jika berhasil
//                    val message = response.body()?.message ?: "Logout successful."
//                    Result.success(message)
//                } else {
//                    // Ambil pesan error dari errorBody
//                    val errorMessage = response.errorBody()?.string() ?: "Failed to logout"
//                    Result.failure(Exception(errorMessage))
//                }
//
//                // Kembali ke thread utama
//                withContext(Dispatchers.Main) {
//                    result.fold(
//                        onSuccess = { message -> onSuccess(message) },
//                        onFailure = { error -> onError(error.localizedMessage ?: "An error occurred") }
//                    )
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    onError(e.localizedMessage ?: "An unexpected error occurred")
//                }
//            }
//        }
//    }
//
//
//
//    fun loginUser(email: String, password: String, context: Context, navController: NavHostController) {
//        if (email.isBlank() || password.isBlank()) {
//            _statusState.postValue(UserStatusUIState.Error("Email and password are required"))
//            return
//        }
//
//        _statusState.value = UserStatusUIState.Loading
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val request = LoginRequest(email, password)
//                val response = authService.loginUser(request)
//
//                withContext(Dispatchers.Main) {
//                    if (response.isSuccessful) {
//                        val loginResponse = response.body()
//
//                        // Log the entire response for debugging
//                        Log.d("LoginResponse", "Response body: $loginResponse")
//
//                        if (loginResponse != null) {
//                            val token = loginResponse.data.token
//                            val user = loginResponse.data
//
//                            if (user != null) {
//                                // Save the token and email in SharedPreferences (or another storage method)
//                                saveUserSession(context, token, email)
//
//                                // Load the user data (optional)
//                                loadUserData(token, email)
//
//                                _userState.postValue(UserUIState.Success(user))
//                                _statusState.postValue(UserStatusUIState.Success)
//
//                                // Check the user role
//                                if (user.roleId == 1) {
//                                    // Navigate to Admin Page if the user is an admin
//                                    navController.navigate("adminPage")
//                                } else {
//                                    // Navigate to Homepage if the user is a regular user
//                                    navController.navigate("AppDescView")
//                                }
//                                Log.d("UserViewModel", "User logged in: $user")
//                            } else {
//                                _statusState.postValue(UserStatusUIState.Error("User data is null"))
//                                Log.e("LoginViewModel", "Error: User data is null")
//                            }
//                        } else {
//                            _statusState.postValue(UserStatusUIState.Error("Response body is null"))
//                            Log.e("LoginViewModel", "Error: Response body is null")
//                        }
//                    } else {
//                        _statusState.postValue(
//                            UserStatusUIState.Error("Error: ${response.code()}, Message: ${response.message()}")
//                        )
//                        Log.e("LoginViewModel", "Error: ${response.code()} - ${response.message()}")
//                    }
//                }
//            } catch (e: Exception) {
//                _statusState.postValue(UserStatusUIState.Error(e.localizedMessage ?: "Login failed"))
//                Log.e("LoginViewModel", "Error during login: ${e.localizedMessage}")
//            }
//        }
//    }
//
//
//    fun updateUser(token: String, username: String, email: String) {
//        _statusState.value = UserStatusUIState.Loading
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // Prepare the UpdateUserRequest with the new username and email
//                val request = UpdateUserRequest(username, email)  // Only send username and email in the body
//                val response = authService.updateUser(token, request) // Pass token in header
//
//                withContext(Dispatchers.Main) {
//                    if (response.isSuccessful) {
//                        _statusState.value = UserStatusUIState.Success
//                    } else {
//                        _statusState.value = UserStatusUIState.Error(response.message())
//                    }
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    _statusState.value = UserStatusUIState.Error(e.localizedMessage ?: "Failed to update user")
//                }
//            }
//        }
//    }
//
//
//    // Save user session
//    // Save session data in SharedPreferences
//    fun saveUserSession(context: Context, token: String, email: String) {
//        val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString("token", token)
//        editor.putString("email", email)
//        editor.apply()
//    }
//
//    // Load User Data (get user information using the token)
//    fun loadUserData(token: String, email: String) {
//        if (token.isNullOrEmpty()) {
//            _userState.postValue(UserUIState.Error("User not logged in"))
//            return
//        }
//
//        _userState.value = UserUIState.Loading
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // Assuming you need to pass email to the service
//                val emailRequest = EmailRequest(email)
//
//                // Make API call and get response
//                val response = authService.getUserData(token, emailRequest)
//
//                withContext(Dispatchers.Main) {
//                    if (response.isSuccessful) {
//                        val userResponse = response.body()
//
//                        // Check if the response body is not null
//                        if (userResponse != null) {
//                            val user = userResponse.data  // Extract the UserModel from the response
//
//                            if (user != null) {
//                                _userState.postValue(UserUIState.Success(user))  // Pass UserModel
//                            } else {
//                                _userState.postValue(UserUIState.Error("User data is null"))
//                            }
//                        } else {
//                            _userState.postValue(UserUIState.Error("Failed to load user data after success"))
//                        }
//                    } else {
//                        _userState.postValue(UserUIState.Error("Failed to load user data response fail"))
//                        Log.e("UserViewModel", "Error loading user data: ${response.message()}")
//                    }
//                }
//            } catch (e: Exception) {
//                _userState.postValue(UserUIState.Error(e.localizedMessage ?: "Failed to load user data in email and token"))
//                Log.e("UserViewModel", "Error loading user data: ${e.localizedMessage}")
//            }
//        }
//    }
//
//    fun delete(token: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // Panggil fungsi logout dari service
//                val response = authService.deleteUser(token) // retrofit2.Response<LogoutResponse>
//
//                val result: Result<String> = if (response.isSuccessful) {
//                    // Ambil pesan dari body jika berhasil
//                    val message = response.body()?.message ?: "Delete successful."
//                    Result.success(message)
//                } else {
//                    // Ambil pesan error dari errorBody
//                    val errorMessage = response.errorBody()?.string() ?: "Failed to Delete"
//                    Result.failure(Exception(errorMessage))
//                }
//
//                // Kembali ke thread utama
//                withContext(Dispatchers.Main) {
//                    result.fold(
//                        onSuccess = { message -> onSuccess(message) },
//                        onFailure = { error -> onError(error.localizedMessage ?: "An error occurred") }
//                    )
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    onError(e.localizedMessage ?: "An unexpected error occurred")
//                }
//            }
//        }
//    }
//
//
//    fun getallUser(token: String) {
//        viewModelScope.launch(Dispatchers.IO) {
////            _userState.value = UserUIState.Loading
//            try {
//                val fetchUser = repository.getalluser(token)
//                _AllUser.value = fetchUser
////                _userState.value = UserUIState.Success(fetchUser)
//            }catch (e: Exception){
////                _userState.value = UserUIState.Error(e.localizedMessage ?: "Failed to load user data in email and token")
//                Log.e("UserViewModel", "Error loading user data: ${e.localizedMessage}")
//            }
//
//        }
//    }
//
//
//}
//
//
//
