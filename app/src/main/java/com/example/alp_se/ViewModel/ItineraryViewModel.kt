package com.example.alp_se.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_se.Model.ItineraryModel
import com.example.alp_se.Repository.ItineraryRepository
import com.example.alp_se.TourronApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ItineraryViewModel(
    private val itineraryRepository: ItineraryRepository
): ViewModel() {

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

    private val _itineraryModel = MutableStateFlow<MutableList<ItineraryModel>>(mutableListOf())

    val itineraryModel: StateFlow<List<ItineraryModel>>
        get() {
            return _itineraryModel.asStateFlow()
        }

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