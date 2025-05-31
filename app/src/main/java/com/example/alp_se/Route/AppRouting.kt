package com.example.alp_se.Route

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alp_se.View.HomeView
import com.example.alp_se.View.ListItineraryDayView
import com.example.alp_se.View.ListItineraryView

enum class listScreen(){
    HomeView,
    CreateItineraryView,
    UpdateItineraryView,
    ListItineraryView,
    CreateItineraryDayView,
    UpdateItineraryDayView,
    ListItineraryDayView
}

@Composable
fun AppRouting() {
    val NavController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = NavController,
            startDestination = listScreen.HomeView.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = listScreen.HomeView.name) {
                HomeView(onSplashFinish = {
                    NavController.navigate(listScreen.ListItineraryView.name) {
                        popUpTo(listScreen.HomeView.name) { inclusive = true }
                    }
                })
            }

            composable(route = listScreen.ListItineraryView.name) {
                ListItineraryView(navController = NavController)
            }

            composable(route = listScreen.ListItineraryDayView.name) {
                ListItineraryDayView(navController = NavController)
            }


        }
    }
}