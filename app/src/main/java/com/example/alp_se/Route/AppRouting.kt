package com.example.alp_se.Route

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
//                SplashScreen(onSplashFinish = {
//                    NavController.navigate(listScreen.OnBoardingScreen.name) {
//                        popUpTo(listScreen.SplashScreen.name) { inclusive = true }
//                    }
//                })
            }
        }
    }
}