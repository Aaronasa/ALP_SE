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
import com.example.alp_se.Model.CreateItineraryRequest
import com.example.alp_se.Model.ItineraryModel
import com.example.alp_se.Model.UpdateItineraryRequest
import com.example.alp_se.Repository.ItineraryRepository
import com.example.alp_se.TourronApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItineraryViewModel(
    private val itineraryRepository: ItineraryRepository
) : ViewModel() {

    var id by mutableStateOf(0)
        private set
    var title by mutableStateOf("")
        private set
    var start_date by mutableStateOf("")
        private set
    var end_date by mutableStateOf("")
        private set
    var estimate_start by mutableStateOf("")
        private set
    var estimate_end by mutableStateOf("")
        private set
    var total_person by mutableStateOf("")
        private set
    var country by mutableStateOf("")
        private set
    var location by mutableStateOf("")
        private set

    private val _itineraryModel = MutableStateFlow<List<ItineraryModel>>(emptyList())
    val itineraryModel: StateFlow<List<ItineraryModel>> = _itineraryModel


    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()


    fun updateTitle(newTitle: String) {
        title = newTitle
    }

    fun updateStartDate(newStartDate: String) {
        start_date = newStartDate
    }

    fun updateEndDate(newEndDate: String) {
        end_date = newEndDate
    }

    fun updateEstimateStart(newEstimateStart: String) {
        estimate_start = newEstimateStart
    }

    fun updateEstimateEnd(newEstimateEnd: String) {
        estimate_end = newEstimateEnd
    }

    fun updateTotalPerson(newTotalPerson: String) {
        total_person = newTotalPerson
    }

    fun updateCountry(newCountry: String) {
        country = newCountry
    }

    fun updateLocation(newLocation: String) {
        location = newLocation
    }

    fun getAllItineraries() {
        viewModelScope.launch {
            try {
                val response = itineraryRepository.getAllItineraries()
                if (response.isSuccessful) {
                    _itineraryModel.value = response.body()?.data as MutableList<ItineraryModel>
                    _statusMessage.value = "Itineraries loaded successfully."
                } else {
                    _statusMessage.value = "Failed to load itineraries: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun createItinerary(itinerary: CreateItineraryRequest) {
        viewModelScope.launch {
            try {
                val response = itineraryRepository.createItinerary(itinerary)
                if (response.isSuccessful) {
                    _statusMessage.value = "Itinerary created successfully."
                } else {
                    _statusMessage.value = "Failed to create itinerary: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateItinerary(id: Int, itinerary: UpdateItineraryRequest) {
        viewModelScope.launch {
            try {
                val response = itineraryRepository.updateItinerary(id, itinerary)
                if (response.isSuccessful) {
                    _statusMessage.value = "Itinerary updated successfully."
                } else {
                    _statusMessage.value = "Failed to update itinerary: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteItinerary(id: Int) {
        viewModelScope.launch {
            try {
                val response = itineraryRepository.deleteItinerary(id)
                if (response.isSuccessful) {
                    _statusMessage.value = "Itinerary deleted successfully."
                    getAllItineraries()
                } else {
                    _statusMessage.value = "Failed to delete itinerary: ${response.message()}"
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
                val itineraryRepository = application.container.itineraryRepository
                ItineraryViewModel(
                    itineraryRepository
                )
            }
        }
    }
}