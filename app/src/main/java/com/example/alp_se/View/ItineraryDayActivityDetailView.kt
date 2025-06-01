package com.example.alp_se.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_se.Model.ItineraryDayModel
import com.example.alp_se.Route.listScreen
import com.example.alp_se.ViewModel.ItineraryDayViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItineraryDayDetailView(
    navController: NavController
) {
    val selectedDay = navController.previousBackStackEntry
        ?.savedStateHandle?.get<String>("selectedDay") ?: ""

    val itineraryId = navController.previousBackStackEntry
        ?.savedStateHandle?.get<Int>("itineraryId") ?: 0

    val viewModel: ItineraryDayViewModel = viewModel(factory = ItineraryDayViewModel.Factory)
    val itineraryDays by viewModel.itineraryDayModel.collectAsState()

    LaunchedEffect(Unit) {
        println("🚀 Fetching itineraryDays from detail screen")
        viewModel.getAllItineraryDays()
    }

    val selectedDate = LocalDate.parse(selectedDay)
    println("📊 itineraryDays size: ${itineraryDays.size}")

    val filteredActivities = itineraryDays.onEach {
        println("🔍 RAW day=${it.day}, itineraryId=${it.itineraryId}")
    }.filter {
        try {
            val itemDate = OffsetDateTime.parse(it.day)
                .withOffsetSameInstant(java.time.ZoneOffset.UTC)
                .toLocalDate()

            println("🧩 Comparing itemDate=$itemDate vs selectedDate=$selectedDate | itineraryId=${it.itineraryId}")

            itemDate == selectedDate && it.itineraryId == itineraryId
        } catch (e: Exception) {
            println("❌ Error parsing: ${e.message}")
            false
        }
    }

    LaunchedEffect(itineraryDays) {
        itineraryDays.forEach {
            println("📦 it.day = '${it.day}'")
            try {
                val parsed = OffsetDateTime.parse(it.day).toLocalDate()
                println("✅ Parsed itemDate = $parsed")
            } catch (e: Exception) {
                println("❌ Error parsing: ${it.day} (${e.message})")
            }
        }
        println("📦 selectedDate = $selectedDate")
    }

    val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("en", "US")))

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
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
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
                    }

                    // Activity Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Activity Details",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = formattedDate,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                value = filteredActivities.size.toString(),
                                label = "Activities",
                                color = Color.White
                            )

                            StatItem(
                                value = if (filteredActivities.isNotEmpty()) {
                                    try {
                                        val wibZone = ZoneOffset.ofHours(7)
                                        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                                        val earliestStart = filteredActivities
                                            .minByOrNull { OffsetDateTime.parse(it.start_time) }
                                            ?.start_time
                                            ?.let {
                                                OffsetDateTime.parse(it)
                                                    .withOffsetSameInstant(wibZone)
                                                    .format(timeFormatter)
                                            }

                                        val latestEnd = filteredActivities
                                            .maxByOrNull { OffsetDateTime.parse(it.end_time) }
                                            ?.end_time
                                            ?.let {
                                                OffsetDateTime.parse(it)
                                                    .withOffsetSameInstant(wibZone)
                                                    .format(timeFormatter)
                                            }

                                        if (earliestStart != null && latestEnd != null) {
                                            "$earliestStart - $latestEnd"
                                        } else {
                                            "N/A"
                                        }
                                    } catch (e: Exception) {
                                        "N/A"
                                    }
                                } else {
                                    "N/A"
                                },
                                label = "Duration",
                                color = Color(0xFF10B981)
                            )


                            StatItem(
                                value = "${if (filteredActivities.isNotEmpty()) 100 else 0}%",
                                label = "Complete",
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
                            text = "Today's Schedule",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )
                    }

                    // Activities List
                    if (filteredActivities.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No activities scheduled",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF718096),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add some activities to get started!",
                                    fontSize = 14.sp,
                                    color = Color(0xFFA0AEC0),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredActivities.sortedBy { it.start_time }) { activity ->
                                ModernActivityCard(activity = activity, navController = navController)
                            }
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ModernActivityCard(activity: ItineraryDayModel,  navController: NavController) {
    val wibZone = ZoneOffset.ofHours(7)
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)

    val startTimeWIB = try {
        OffsetDateTime.parse(activity.start_time)
            .atZoneSameInstant(wibZone)
            .format(formatter)
    } catch (e: Exception) {
        activity.start_time
    }

    val endTimeWIB = try {
        OffsetDateTime.parse(activity.end_time)
            .atZoneSameInstant(wibZone)
            .format(formatter)
    } catch (e: Exception) {
        activity.end_time
    }

    val meetingTimeFormatted = try {
        LocalTime.parse(activity.meeting_time).format(formatter)
    } catch (e: Exception) {
        activity.meeting_time
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Time Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF667eea).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Time",
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$startTimeWIB - $endTimeWIB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF667eea)
                        )
                    }
                }

                if (meetingTimeFormatted.isNotEmpty() && meetingTimeFormatted != activity.meeting_time) {
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Meeting",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Meet: $meetingTimeFormatted",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            if (activity.activity_description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Divider(
                    color = Color(0xFFE2E8F0),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Description",
                        tint = Color(0xFF718096),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = activity.activity_description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF2D3748),
                        lineHeight = 24.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("editId", activity.id)
                    navController.navigate(listScreen.UpdateItineraryDayView.name)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit", color = Color.White)
            }
        }
    }
}

