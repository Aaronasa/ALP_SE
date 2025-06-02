package com.example.alp_se.Route

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.alp_se.View.CreateItineraryDayView
import androidx.navigation.navArgument
import com.example.alp_se.AppContainer
import com.example.alp_se.View.CreateItineraryView
import com.example.alp_se.View.HomeView
import com.example.alp_se.View.ItineraryDayDetailView
import com.example.alp_se.View.ListItineraryDayView
import com.example.alp_se.View.ListItineraryView
import com.example.alp_se.View.UpdateItineraryDayView
import com.example.alp_se.View.UpdateItineraryView
import com.example.alp_se.ViewModel.ItineraryViewModel

enum class listScreen(){
    HomeView,
    CreateItineraryView,
    UpdateItineraryView,
    ListItineraryView,
    CreateItineraryDayView,
    UpdateItineraryDayView,
    ListItineraryDayView,
    ItineraryDayDetailView
}

@RequiresApi(Build.VERSION_CODES.O)
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
                HomeView(onSplashFinish = {
                    NavController.navigate(listScreen.ListItineraryView.name) {
                        popUpTo(listScreen.HomeView.name) { inclusive = true }
                    }
                })
            }

            composable(route = listScreen.CreateItineraryView.name) {
                CreateItineraryView(navController = NavController)
            }

            composable(route = listScreen.ListItineraryView.name) {
                ListItineraryView(navController = NavController)
            }

            composable(route = listScreen.ListItineraryDayView.name) {
                ListItineraryDayView(navController = NavController)
            }

            composable(route = listScreen.CreateItineraryDayView.name) {
                CreateItineraryDayView(navController = NavController)
            }

            composable(route = listScreen.ItineraryDayDetailView.name) {
                ItineraryDayDetailView(navController = NavController)
            }

            composable(route = listScreen.UpdateItineraryDayView.name) {
                UpdateItineraryDayView(navController = NavController)
            }

            composable(
                route = "${listScreen.UpdateItineraryView.name}/{itineraryId}",
                arguments = listOf(
                    navArgument("itineraryId") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val itineraryId = backStackEntry.arguments?.getInt("itineraryId") ?: 0
                println("AppRouting - Received itineraryId: $itineraryId") // Debug log
                UpdateItineraryView(
                    navController = NavController,
                    itineraryId = itineraryId
                )
            }
        }
    }
}