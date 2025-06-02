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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_se.Component.rememberStatusSnackbar
import com.example.alp_se.Model.UpdateItineraryRequest
import com.example.alp_se.ViewModel.ItineraryViewModel

// Helper functions for formatting
private fun formatDateString(dateString: String): String {
    return try {
        dateString.substringBefore('T')
    } catch (e: Exception) {
        dateString
    }
}

private fun formatTimeString(datetimeString: String): String {
    return try {
        // Handle ISO datetime format like "2025-06-10T09:00:00.000Z"
        when {
            // If it's already in HH:MM format
            datetimeString.matches(Regex("^\\d{2}:\\d{2}$")) -> datetimeString
            datetimeString.contains('T') -> {
                val timePart = datetimeString.substringAfter('T')
                when {
                    timePart.contains(':') -> {
                        val timeOnly = timePart.substringBefore('.')
                            .substringBefore('Z')  // Remove timezone if present
                        if (timeOnly.length >= 5) {
                            timeOnly.substring(0, 5) // Take HH:MM part
                        } else timeOnly
                    }
                    else -> timePart
                }
            }
            // If it's just time without colon, try to format it
            datetimeString.length == 4 && datetimeString.all { it.isDigit() } -> {
                "${datetimeString.substring(0, 2)}:${datetimeString.substring(2, 4)}"
            }
            else -> datetimeString
        }
    } catch (e: Exception) {
        println("Error formatting time string: $datetimeString, Error: ${e.message}")
        datetimeString
    }
}

@Composable
fun UpdateItineraryView(
    itineraryViewModel: ItineraryViewModel = viewModel(factory = ItineraryViewModel.Factory),
    navController: NavController? = null,
    itineraryId: Int = 0  // Add this parameter
) {
    val itineraries by itineraryViewModel.itineraryModel.collectAsState()
    val statusMessage by itineraryViewModel.statusMessage.collectAsState()

    // Remember snackbar host state for status messages
    val snackbarHostState = rememberStatusSnackbar(
        statusMessage = statusMessage,
        onStatusMessageCleared = { itineraryViewModel.clearStatusMessage() }
    )

    println("UpdateItineraryView - Received itineraryId: $itineraryId") // Debug log

    // Find the itinerary to update
    val currentItinerary = itineraries.find { it.id == itineraryId }

    // Load data when composable is first created
    LaunchedEffect(Unit) {
        println("UpdateItineraryView - Loading all itineraries...")
        itineraryViewModel.getAllItineraries()
    }

    // Pre-fill the form with current itinerary data when itineraries are loaded
    LaunchedEffect(itineraries, itineraryId) {
        if (itineraries.isNotEmpty() && itineraryId != 0) {
            currentItinerary?.let { itinerary ->
                itineraryViewModel.updateTitle(itinerary.title)
                itineraryViewModel.updateStartDate(formatDateString(itinerary.start_date))
                itineraryViewModel.updateEndDate(formatDateString(itinerary.end_date))
                val formattedStartTime = formatTimeString(itinerary.estimate_Start)
                val formattedEndTime = formatTimeString(itinerary.estimate_End)
                itineraryViewModel.updateEstimateStart(formattedStartTime)
                itineraryViewModel.updateEstimateEnd(formattedEndTime)
                itineraryViewModel.updateTotalPerson(itinerary.total_person.toString())
                itineraryViewModel.updateCountry(itinerary.country)
                itineraryViewModel.updateLocation(itinerary.location)
                println("UpdateItineraryView - Pre-filled form with itinerary: ${itinerary.title}")
            } ?: run {
                println("UpdateItineraryView - No itinerary found with ID: $itineraryId")
                println("UpdateItineraryView - Available itineraries: ${itineraries.map { "${it.id}: ${it.title}" }}")
            }
        } else {
            println("UpdateItineraryView - Not pre-filling: itineraries.isEmpty()=${itineraries.isEmpty()}, itineraryId=$itineraryId")
        }
    }

    // Navigate back after successful update
    LaunchedEffect(statusMessage) {
        if (statusMessage?.contains("updated successfully") == true) {
            kotlinx.coroutines.delay(1500)
            navController?.navigateUp()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    androidx.compose.material3.Snackbar(
                        snackbarData = snackbarData,
                        containerColor = if (statusMessage?.contains("successfully") == true) {
                            Color(0xFF4CAF50) // Green for success
                        } else {
                            Color(0xFFF44336) // Red for error
                        },
                        contentColor = Color.White,
                        actionColor = Color.White
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back button
                        IconButton(
                            onClick = { navController?.navigateUp() },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Update Itinerary",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Modify your travel plans",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Update icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Update",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Form Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Title Field
                        CustomTextField(
                            value = itineraryViewModel.title,
                            onValueChange = itineraryViewModel::updateTitle,
                            label = "Judul Trip",
                            icon = Icons.Filled.Title,
                            placeholder = "Masukkan judul trip anda"
                        )

                        // Date Fields Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomTextField(
                                value = itineraryViewModel.start_date,
                                onValueChange = itineraryViewModel::updateStartDate,
                                label = "Tanggal Mulai",
                                icon = Icons.Filled.CalendarToday,
                                placeholder = "YYYY-MM-DD",
                                modifier = Modifier.weight(1f)
                            )

                            CustomTextField(
                                value = itineraryViewModel.end_date,
                                onValueChange = itineraryViewModel::updateEndDate,
                                label = "Tanggal Berakhir",
                                icon = Icons.Filled.CalendarToday,
                                placeholder = "YYYY-MM-DD",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Estimate Time Fields Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomTextField(
                                value = itineraryViewModel.estimate_start,
                                onValueChange = itineraryViewModel::updateEstimateStart,
                                label = "Perkiraan Waktu Mulai",
                                icon = Icons.Filled.CalendarToday,
                                placeholder = "HH:MM",
                                modifier = Modifier.weight(1f)
                            )

                            CustomTextField(
                                value = itineraryViewModel.estimate_end,
                                onValueChange = itineraryViewModel::updateEstimateEnd,
                                label = "Perkiraan Waktu Selesai",
                                icon = Icons.Filled.CalendarToday,
                                placeholder = "HH:MM",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Total Person Field
                        CustomTextField(
                            value = itineraryViewModel.total_person,
                            onValueChange = itineraryViewModel::updateTotalPerson,
                            label = "Jumlah Partisipan",
                            icon = Icons.Filled.Person,
                            placeholder = "Masukkan Jumlah Partisipan"
                        )

                        // Country Field
                        CustomTextField(
                            value = itineraryViewModel.country,
                            onValueChange = itineraryViewModel::updateCountry,
                            label = "Negara",
                            icon = Icons.Filled.LocationOn,
                            placeholder = "Masukkan Negara Tujuan"
                        )

                        // Location Field
                        CustomTextField(
                            value = itineraryViewModel.location,
                            onValueChange = itineraryViewModel::updateLocation,
                            label = "Lokasi",
                            icon = Icons.Filled.LocationOn,
                            placeholder = "Masukkan Lokasi Trip"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Update Button
                        Button(
                            onClick = {
                                if (itineraryId != 0) {
                                    val startDate = "${itineraryViewModel.start_date}T00:00:00.000Z"
                                    val endDate = "${itineraryViewModel.end_date}T00:00:00.000Z"
                                    val startTime = "${itineraryViewModel.start_date}T${itineraryViewModel.estimate_start}:00.000Z"
                                    val endTime = "${itineraryViewModel.end_date}T${itineraryViewModel.estimate_end}:00.000Z"

                                    val updateRequest = UpdateItineraryRequest(
                                        title = itineraryViewModel.title,
                                        start_date = startDate,
                                        end_date = endDate,
                                        estimate_start = startTime,
                                        estimate_end = endTime,
                                        total_person = itineraryViewModel.total_person.toIntOrNull() ?: 1,
                                        country = itineraryViewModel.country,
                                        location = itineraryViewModel.location,
                                        id = itineraryId
                                    )
                                    itineraryViewModel.updateItinerary(itineraryId, updateRequest)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667eea)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = "Save",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Simpan Itinerary",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = Color(0xFF333333),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF667eea),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )
    }
}