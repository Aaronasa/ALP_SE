package com.example.alp_se.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_se.Model.CreateItineraryDayRequest
import com.example.alp_se.Model.ItineraryDayModel
import com.example.alp_se.Route.listScreen
import com.example.alp_se.ViewModel.ItineraryDayViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import kotlinx.coroutines.launch

@Composable
fun CreateItineraryDayView(
    navController: NavController? = null,
    viewModel: ItineraryDayViewModel = viewModel(factory = ItineraryDayViewModel.Factory)
) {
    var itineraryId by remember { mutableStateOf(0) }
    var startDate by remember { mutableStateOf<String?>(null) }
    var endDate by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        navController?.previousBackStackEntry?.savedStateHandle?.let { handle ->
            itineraryId = handle.get<Int>("itineraryId") ?: 0
            startDate = handle.get<String>("startDate")
            endDate = handle.get<String>("endDate")
            println("✅ itineraryId loaded: $itineraryId")
        }
    }

    if (itineraryId <= 0) {
        Text(
            text = "Loading itinerary data...",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(24.dp)
        )
        return
    }

    var activity by remember { mutableStateOf(ItineraryDayModel()) }

    val formatter = remember { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) }
    val startDateObj = remember(startDate) { startDate?.let { formatter.parse(it) } }
    val endDateObj = remember(endDate) { endDate?.let { formatter.parse(it) } }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Activity",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ModernTextField(
                        value = activity.day,
                        onValueChange = { activity = activity.copy(day = it) },
                        label = "Day",
                        icon = Icons.Filled.CalendarToday,
                        placeholder = "DD-MM-YYYY"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernTextField(
                        value = activity.start_time,
                        onValueChange = { activity = activity.copy(start_time = it) },
                        label = "Start Time",
                        icon = Icons.Filled.Schedule,
                        placeholder = "09.00"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernTextField(
                        value = activity.end_time,
                        onValueChange = { activity = activity.copy(end_time = it) },
                        label = "End Time",
                        icon = Icons.Filled.Schedule,
                        placeholder = "17.00"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernTextField(
                        value = activity.meeting_time,
                        onValueChange = { activity = activity.copy(meeting_time = it) },
                        label = "Meeting Time",
                        icon = Icons.Filled.Group,
                        placeholder = "08.30"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = activity.activity_description,
                        onValueChange = { activity = activity.copy(activity_description = it) },
                        label = { Text("Activity Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            val startDateFormatted = formatToISOString(activity.day, activity.start_time)
                            val endDateFormatted = formatToISOString(activity.day, activity.end_time)
                            val meetingTimeFormatted = normalizeTimeFormat(activity.meeting_time)
                            val normalizedDay = normalizeDateFormat(activity.day)

                            val inputDay = try {
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(normalizedDay)
                            } catch (e: Exception) {
                                null
                            }

                            if (startDateFormatted != null && endDateFormatted != null && meetingTimeFormatted != null && inputDay != null) {

                                // ⛔ Validasi: End time tidak boleh lebih awal dari start time
                                if (endDateFormatted < startDateFormatted) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("❗ End time cannot be earlier than start time.")
                                    }
                                    return@Button
                                }

                                // ⛔ Validasi: Tanggal harus dalam rentang itinerary
                                if (startDateObj != null && endDateObj != null) {
                                    if (inputDay.before(startDateObj) || inputDay.after(endDateObj)) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("❗ Date must be within itinerary duration.")
                                        }
                                        return@Button
                                    }
                                }

                                // ✅ Buat aktivitas
                                if (itineraryId > 0) {
                                    viewModel.createItineraryDay(
                                        CreateItineraryDayRequest(
                                            day = normalizedDay,
                                            start_time = startDateFormatted,
                                            end_time = endDateFormatted,
                                            activity_description = activity.activity_description,
                                            meeting_time = meetingTimeFormatted,
                                            itineraryId = itineraryId
                                        )
                                    )
                                    navController?.navigate(listScreen.ListItineraryDayView.name) {
                                        popUpTo(listScreen.ListItineraryDayView.name) { inclusive = true }
                                    }
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("❗ Invalid itinerary ID.")
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("❗ Invalid date or time format.")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea))
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Activity")
                    }

                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

fun formatToISOString(date: String, time: String): String? {
    return try {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH.mm", Locale("id", "ID"))
        inputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val combined = "$date $time"
        val parsedDate = inputFormat.parse(combined)
        parsedDate?.let { outputFormat.format(it) }
    } catch (e: Exception) {
        Log.e("DATE_ERROR", "Failed to parse date: ${e.message}")
        null
    }
}


fun normalizeTimeFormat(input: String): String {
    return if (input.contains('.')) {
        val parts = input.split(".")
        val hour = parts[0].padStart(2, '0')
        val minute = parts.getOrNull(1)?.padEnd(2, '0') ?: "00"
        "$hour:$minute:00"
    } else if (input.contains(':')) {
        // Sudah benar tapi belum ada detik
        val colonParts = input.split(":")
        when (colonParts.size) {
            2 -> "${colonParts[0].padStart(2, '0')}:${colonParts[1].padStart(2, '0')}:00"
            3 -> input // sudah HH:mm:ss
            else -> "00:00:00"
        }
    } else {
        "00:00:00"
    }
}

fun normalizeDateFormat(input: String): String {
    return try {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale("id", "ID"))
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(input)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        Log.e("DATE_ERROR", "Failed to normalize day: ${e.message}")
        "1970-01-01"
    }
}