package com.example.alp_se.ViewModel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class ItineraryViewModel {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as LunaireApplication)
                val userRepository = application.container.userRepository
                val activityRepository = application.container.activityRepository
                val categoryRepository = application.container.categoryRepository
                HomePageViewModel(
                    userRepository,
                    activityRepository,
                    categoryRepository
                )
            }
        }
    }
}