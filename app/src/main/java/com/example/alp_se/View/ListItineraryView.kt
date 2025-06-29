package com.example.alp_se.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_se.Component.StatusSnackbar
import com.example.alp_se.Model.ItineraryModel
import com.example.alp_se.Route.listScreen
import com.example.alp_se.ViewModel.ItineraryViewModel

private fun formatDateString(dateString: String): String {
    return try {
        dateString.substringBefore('T')
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun ListItineraryView(
    itineraryViewModel: ItineraryViewModel = viewModel(factory = ItineraryViewModel.Factory),
    navController: NavController? = null
) {
    // Collect state from ViewModel
    val itineraries by itineraryViewModel.itineraryModel.collectAsState()
    val statusMessage by itineraryViewModel.statusMessage.collectAsState()

    LaunchedEffect (Unit) {
        itineraryViewModel.getAllItineraries()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFFF5F5F5)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TravelExplore,
                            contentDescription = "Travel",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = "Daftar itinerary",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle
                    Text(
                        text = "Jelajahi petuangalanmu berikutnya!",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            count = itineraries.size.toString(),
                            label = "Jadwal"
                        )
                        StatItem(
                            count = itineraries.sumOf { it.total_person }.toString(),
                            label = "Jumlah Orang"
                        )
                        StatItem(
                            count = itineraries.map { it.country }.toSet().size.toString(),
                            label = "Negara"
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(top = 20.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (itineraries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada itinerary. Buat dulu",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    items(itineraries) { itinerary ->
                        println("Card: ${itinerary.title}, ID: ${itinerary.id}")

                        ItineraryCard(
                            itinerary = itinerary,
                            title = itinerary.title,
                            startDate = formatDateString(itinerary.start_date),
                            endDate = formatDateString(itinerary.end_date),
                            location = itinerary.location,
                            participantCount = itinerary.total_person,
                            onEdit = { itineraryId ->
                                // Fixed: Use navigation arguments instead of savedStateHandle
                                navController?.navigate("${listScreen.UpdateItineraryView.name}/$itineraryId")
                            },
                            onDelete = { itineraryId ->
                                itineraryViewModel.deleteItinerary(itineraryId)
                            },
                            onClick = {
                                navController?.currentBackStackEntry?.savedStateHandle?.set(
                                    "itineraryId",
                                    itinerary.id
                                )
                                navController?.navigate(listScreen.ListItineraryDayView.name)
                            }
                        )
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                navController?.navigate(listScreen.CreateItineraryView.name)
            },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF667eea),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Create Itinerary",
                modifier = Modifier.size(70.dp)
            )
        }

        // Status Snackbar - positioned at bottom
        StatusSnackbar(
            statusMessage = statusMessage,
            onDismiss = {
                itineraryViewModel.clearStatusMessage()
            }
        )
    }
}

@Composable
private fun StatItem(
    count: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListItineraryViewPreview() {
    MaterialTheme {
        ListItineraryView()
    }
}