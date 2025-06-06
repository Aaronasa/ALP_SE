package com.example.alp_se.View

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.FileDownload
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alp_se.Component.StatusSnackbar
import com.example.alp_se.Component.rememberStatusSnackbar
import com.example.alp_se.Model.ItineraryDayModel
import com.example.alp_se.Route.listScreen
import com.example.alp_se.ViewModel.ItineraryDayViewModel
import com.example.alp_se.ViewModel.ItineraryViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
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
    val snackbarHostState = rememberStatusSnackbar(statusMessage) {
        viewModel.clearStatusMessage()
    }

    val itineraryViewModel: ItineraryViewModel = viewModel(factory = ItineraryViewModel.Factory)
    val allItineraries by itineraryViewModel.itineraryModel.collectAsState()

    // Fetch the data once this screen appears
    LaunchedEffect(itineraryId) {
        viewModel.getAllItineraryDays()
        itineraryViewModel.getAllItineraries()
    }

    val mergedDays = itineraryDays
        .filter { it.itineraryId == itineraryId }
        .groupBy { it.day }
        .map { (day, activities) ->
            val earliestStart = activities.minByOrNull { it.start_time }?.start_time ?: ""
            val latestEnd = activities.maxByOrNull { it.end_time }?.end_time ?: ""
            ItineraryDayModel(
                id = activities.first().id,
                day = day,
                start_time = earliestStart,
                end_time = latestEnd,
                activity_description = "", // Optional: combine text if you want
                meeting_time = "", // Optional: set logic if needed
                itineraryId = itineraryId
            )
        }
    val itinerary = allItineraries.find { it.id == itineraryId }
    val tripTitle = itinerary?.title ?: "Trip ID $itineraryId"
    val startDateFormatted = itinerary?.start_date?.let {
        OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH))
    }
    val endDateFormatted = itinerary?.end_date?.let {
        OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH))
    }
    val tripDuration = "$startDateFormatted – $endDateFormatted"

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
        Column(modifier = Modifier.fillMaxSize()) {
            ItineraryListScreen(
                itineraryDays = mergedDays,
                rawItineraryDays = itineraryDays,
                tripTitle = tripTitle,
                tripDuration = tripDuration,
                onBackPressed = {
                    navController.popBackStack()
                },
                onSharePressed = onSharePressed,
                onDayClicked = { dayIndex ->
                    val selectedDay = OffsetDateTime.parse(mergedDays[dayIndex].day).toLocalDate().toString()
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("selectedDay", selectedDay)
                        set("itineraryId", itineraryId)
                    }
                    navController.navigate(listScreen.ItineraryDayDetailView.name)
                }
            )
        }
        StatusSnackbar(
            statusMessage = statusMessage,
            onDismiss = { viewModel.clearStatusMessage() },
            snackbarHostState = snackbarHostState
        )

        // ✅ Tombol Create di kanan bawah
        FloatingActionButton(
            onClick = {
                Log.d("DEBUG", "Navigating to Create View with itineraryId = $itineraryId")
                navController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("itineraryId", itineraryId)
                    set("startDate", itinerary?.start_date)
                    set("endDate", itinerary?.end_date)

                }

                navController.navigate(listScreen.CreateItineraryDayView.name)
            },
            containerColor = Color(0xFF667eea),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create", tint = Color.White)

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItineraryListScreen(
    itineraryDays: List<ItineraryDayModel>,
    rawItineraryDays: List<ItineraryDayModel>,
    tripTitle: String = "My Trip",
    tripDuration: String = "",
    onBackPressed: () -> Unit = {},
    onSharePressed: () -> Unit = {},
    onDayClicked: (Int) -> Unit = {}
) {
    val viewModel: ItineraryDayViewModel = viewModel(factory = ItineraryDayViewModel.Factory)
    val context = LocalContext.current
    val showMenu by viewModel.showMenu.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()




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

                        Box {
                            IconButton(
                                onClick = { viewModel.showMenu(true)},
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = "Export",
                                    tint = Color.White
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { viewModel.showMenu(false) },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Export as PDF") },
                                    onClick = {
                                        viewModel.showMenu(false)
                                        viewModel.exportToPdf(itineraryDays, context)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Export as Excel") },
                                    onClick = {
                                        viewModel.showMenu(false)
                                        viewModel.exportToExcel(itineraryDays, context)
                                    }
                                )
                            }
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
                            text = "Itinerary Harian",
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

                            val originalDayRaw = itineraryDay.day

                            val parsedDate = OffsetDateTime.parse(itineraryDay.day)
                            val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd-MMMM", Locale.ENGLISH))

                            val wibZone = java.time.ZoneId.of("Asia/Jakarta")
                            val timeFormatter = DateTimeFormatter.ofPattern("HH.mm", Locale.ENGLISH).withZone(wibZone)

                            val startTime = OffsetDateTime.parse(itineraryDay.start_time).atZoneSameInstant(wibZone).format(timeFormatter)
                            val endTime = OffsetDateTime.parse(itineraryDay.end_time).atZoneSameInstant(wibZone).format(timeFormatter)

                            // Create a new itineraryDay with formatted time
                            val displayModel = itineraryDay.copy(
//                                day = "Day ${index + 1} ($formattedDate)",
                                start_time = startTime,
                                end_time = endTime
                            )

                            ModernItineraryDayCard(
                                itineraryDay = displayModel,
                                dayNumber = index + 1,
                                gradientColors = getGradientForDay(index),
                                onClick = {
                                    viewModel.selectDay(index)
                                    onDayClicked(index)
                                },
                                onDeleteAllByDate = {
                                    val targetDate = OffsetDateTime.parse(originalDayRaw).toLocalDate()
                                    val itemsToDelete = rawItineraryDays.filter {
                                        OffsetDateTime.parse(it.day).toLocalDate() == targetDate
                                    }
                                    itemsToDelete.forEach {
                                        viewModel.deleteItineraryDay(it.id)
                                    }
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


@RequiresApi(Build.VERSION_CODES.O)
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
            rawItineraryDays = sampleItineraryDays,
            tripTitle = "Bali Adventure 2025",
            tripDuration = "March 15 - March 20, 2025"
        )
    }
}