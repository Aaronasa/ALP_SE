package com.example.alp_se.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alp_se.Model.ItineraryDayModel
import com.example.alp_se.ViewModel.ItineraryDayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItineraryDayView(
    navController: NavController = rememberNavController(),
    onBackPressed: () -> Unit = {},
    onSharePressed: () -> Unit = {},
    onDayClicked: (Int) -> Unit = {}
) {
    val itineraryId = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<Int>("itineraryId") ?: 0

    val viewModel: ItineraryDayViewModel = viewModel(factory = ItineraryDayViewModel.Factory)
    val itineraryDays by viewModel.itineraryDayModel.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    // Fetch the data once this screen appears
    LaunchedEffect(Unit) {
        viewModel.getAllItineraryDays()
    }

    val filteredDays = itineraryDays.filter { it.itineraryId == itineraryId }

    ItineraryListScreen(
        itineraryDays = filteredDays,
        tripTitle = "Trip ID $itineraryId",
        tripDuration = "",
        onBackPressed = onBackPressed,
        onSharePressed = onSharePressed,
        onDayClicked = onDayClicked
    )
}

@Composable
fun ItineraryListScreen(
    itineraryDays: List<ItineraryDayModel>,
    tripTitle: String = "My Trip",
    tripDuration: String = "",
    onBackPressed: () -> Unit = {},
    onSharePressed: () -> Unit = {},
    onDayClicked: (Int) -> Unit = {}
) {
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFFF8F9FA)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            Surface(
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .statusBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackPressed,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = onSharePressed,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }

                    // Trip Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tripTitle,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        if (tripDuration.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = tripDuration,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TripStatItem(
                                value = itineraryDays.size.toString(),
                                label = "Days",
                                color = Color.White
                            )

                            TripStatItem(
                                value = itineraryDays.distinctBy { it.itineraryId }.size.toString(),
                                label = "Activities",
                                color = Color(0xFF10B981)
                            )

                            TripStatItem(
                                value = "${if (itineraryDays.isNotEmpty()) 100 else 0}%",
                                label = "Planned",
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                }
            }

            // Content Section
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
                color = Color(0xFFF8F9FA)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Section Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(24.dp)
                                .background(
                                    Color(0xFF667eea),
                                    RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Daily Itinerary",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )
                    }

                    // Itinerary List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(itineraryDays) { index, itineraryDay ->
                            ModernItineraryDayCard(
                                itineraryDay = itineraryDay,
                                dayNumber = index + 1,
                                gradientColors = getGradientForDay(index),
                                onClick = {
                                    selectedDay = index
                                    onDayClicked(index)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripStatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

fun getGradientForDay(dayIndex: Int): List<Color> {
    val gradients = listOf(
        listOf(Color(0xFF667eea), Color(0xFF764ba2)),
        listOf(Color(0xFFf093fb), Color(0xFFf5576c)),
        listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
        listOf(Color(0xFF43e97b), Color(0xFF38f9d7)),
        listOf(Color(0xFFfa709a), Color(0xFFfee140)),
        listOf(Color(0xFFa8edea), Color(0xFFfed6e3)),
        listOf(Color(0xFFffecd2), Color(0xFFfcb69f))
    )
    return gradients[dayIndex % gradients.size]
}

//data class ItineraryDayModel(
//    val id: Int = 0,
//    val day: String = "",
//    val start_time: String = "",
//    val end_time: String = "",
//    val activity_description: String = "",
//    val meeting_time: String = "",
//    val itineraryId: Int = 0
//)

@Preview(showBackground = true)
@Composable
fun ItineraryListScreenPreview() {
    val sampleItineraryDays = listOf(
        ItineraryDayModel(
            id = 1,
            day = "Day 1 - Arrival",
            start_time = "09:00",
            end_time = "18:00",
            activity_description = "Airport pickup, hotel check-in, welcome dinner at beachfront restaurant with traditional Balinese cuisine and cultural performance.",
            meeting_time = "09:00",
            itineraryId = 1
        ),
        ItineraryDayModel(
            id = 2,
            day = "Day 2 - Temple Tour",
            start_time = "08:00",
            end_time = "17:00",
            activity_description = "Visit ancient temples including Tanah Lot and Uluwatu, traditional art villages, and enjoy sunset kecak dance performance.",
            meeting_time = "08:00",
            itineraryId = 1
        ),
        ItineraryDayModel(
            id = 3,
            day = "Day 3 - Mount Batur Sunrise",
            start_time = "02:00",
            end_time = "14:00",
            activity_description = "Early morning hike to Mount Batur for spectacular sunrise views, followed by hot springs relaxation and coffee plantation tour.",
            meeting_time = "02:00",
            itineraryId = 1
        ),
        ItineraryDayModel(
            id = 4,
            day = "Day 4 - Beach Activities",
            start_time = "09:00",
            end_time = "16:00",
            activity_description = "Snorkeling at Blue Lagoon, beach hopping in Nusa Penida, and traditional seafood lunch by the ocean.",
            meeting_time = "09:00",
            itineraryId = 1
        ),
        ItineraryDayModel(
            id = 5,
            day = "Day 5 - Cultural Workshop",
            start_time = "10:00",
            end_time = "17:00",
            activity_description = "Learn traditional Balinese crafts, visit local markets, and enjoy authentic cooking class with local family.",
            meeting_time = "10:00",
            itineraryId = 1
        )
    )

    MaterialTheme {
        ItineraryListScreen(
            itineraryDays = sampleItineraryDays,
            tripTitle = "Bali Adventure 2025",
            tripDuration = "March 15 - March 20, 2025"
        )
    }
}