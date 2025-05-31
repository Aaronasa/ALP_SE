package com.example.alp_se.Route

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alp_se.AppContainer
import com.example.alp_se.View.CreateItineraryView
import com.example.alp_se.View.HomeView
import com.example.alp_se.View.ListItineraryDayView
import com.example.alp_se.View.ListItineraryView
import com.example.alp_se.ViewModel.ItineraryViewModel

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
fun AppRouting(
    itineraryViewModel: ItineraryViewModel = viewModel(factory = ItineraryViewModel.Factory),
    navController: NavController? = null
) {
    val NavController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = NavController,
            startDestination = listScreen.HomeView.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = listScreen.HomeView.name) {
                HomeView(
                    onSplashFinish = {
                        NavController.navigate(listScreen.HomeView.name) {
                            popUpTo(listScreen.HomeView.name) { inclusive = true }
                        }
                    },
                    navController = NavController
                )
            }

            composable(
                route = listScreen.ListItineraryView.name,
            ) {
                ListItineraryView(
                    navController = NavController,
                    itineraryViewModel = itineraryViewModel
                )
            }

            composable(
                route = listScreen.CreateItineraryView.name,
            ) {
                CreateItineraryView(
                    navController = NavController,
                    itineraryViewModel = itineraryViewModel
                )
            }

//            composable(
//                route = listScreen.ListItineraryDayView.name + "/{itinerary_id}",
//                arguments = listOf(
//                    navArgument("itinerary_id") { type = NavType.IntType }
//                )
//            ) { backStackEntry ->
//                val itinerary_id = backStackEntry.arguments?.getInt("itinerary_id")
//                requireNotNull(itinerary_id) { "itinerary_id is required to navigate to ListItineraryDayView" }
//
//                ListItineraryDayView(
//                    navController = NavController,
//                    // itineraryDayViewModel, // You'll need to add this parameter if required
//                    itinerary_id
//                )
//            }
        }
    }
}