package com.example.alp_se.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_se.Model.*
import com.example.alp_se.Repository.ItineraryDayRepository
import com.example.alp_se.TourronApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItineraryDayViewModel(
    private val itineraryDayRepository: ItineraryDayRepository
) : ViewModel() {

    // State untuk form input
    var day by mutableStateOf("")
    var start_time by mutableStateOf("")
    var end_time by mutableStateOf("")
    var activity_description by mutableStateOf("")
    var meeting_time by mutableStateOf("")
    var itineraryId by mutableStateOf(0)

    // State untuk response data
    private val _itineraryDayModel = MutableStateFlow<List<ItineraryDayModel>>(emptyList())
    val itineraryDayModel: StateFlow<List<ItineraryDayModel>> = _itineraryDayModel

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun updateDay(value: String) { day = value }
    fun updateStartTime(value: String) { start_time = value }
    fun updateEndTime(value: String) { end_time = value }
    fun updateActivityDescription(value: String) { activity_description = value }
    fun updateMeetingTime(value: String) { meeting_time = value }
    fun updateItineraryId(value: Int) { itineraryId = value }

    fun getAllItineraryDays() {
        viewModelScope.launch {
            try {
                val response = itineraryDayRepository.getAllItineraryDays()
                if (response.isSuccessful) {
                    _itineraryDayModel.value = response.body()?.data ?: emptyList()
                    _statusMessage.value = "Data loaded successfully."
                } else {
                    _statusMessage.value = "Failed to load data: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun createItineraryDay(request: CreateItineraryDayRequest) {
        viewModelScope.launch {
            try {
                val response = itineraryDayRepository.createItineraryDay(request)
                if (response.isSuccessful) {
                    _statusMessage.value = "Created successfully."
                    getAllItineraryDays()
                } else {
                    _statusMessage.value = "Failed to create: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateItineraryDay(id: Int, request: UpdateItineraryDayRequest) {
        viewModelScope.launch {
            try {
                val response = itineraryDayRepository.updateItineraryDay(id, request)
                if (response.isSuccessful) {
                    _statusMessage.value = "Updated successfully."
                    getAllItineraryDays()
                } else {
                    _statusMessage.value = "Failed to update: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteItineraryDay(id: Int) {
        viewModelScope.launch {
            try {
                val response = itineraryDayRepository.deleteItineraryDay(id)
                if (response.isSuccessful) {
                    _statusMessage.value = "Deleted successfully."
                    getAllItineraryDays()
                } else {
                    _statusMessage.value = "Failed to delete: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TourronApplication)
                val repository = application.container.itineraryDayRepository
                ItineraryDayViewModel(repository)
            }
        }
    }

}