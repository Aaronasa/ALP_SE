package com.example.alp_se.View

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.example.alp_se.Model.ItineraryDayModel
import com.example.alp_se.Model.UpdateItineraryDayRequest
import com.example.alp_se.Route.listScreen
import com.example.alp_se.ViewModel.ItineraryDayViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateItineraryDayView(
    navController: NavController,
    viewModel: ItineraryDayViewModel = viewModel(factory = ItineraryDayViewModel.Factory)
) {


    val editId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("editId")
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val itineraryDays by viewModel.itineraryDayModel.collectAsState()
    val updatedActivity = itineraryDays.find { it.id == editId }


    LaunchedEffect(Unit) {
        viewModel.getAllItineraryDays()
    }



    if (updatedActivity == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    LaunchedEffect(updatedActivity?.id) {
        updatedActivity?.let {
            viewModel.updateDay(formatToLocalDateOnly(it.day))
            viewModel.updateStartTime(formatToTimeOnly(it.start_time))
            viewModel.updateEndTime(formatToTimeOnly(it.end_time))
            viewModel.updateMeetingTime(formatToTimeOnly(it.meeting_time))
            viewModel.updateActivityDescription(it.activity_description)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF667eea), Color(0xFFF5F5F5))
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
                text = "Ubah Aktivitas",
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
                        value = viewModel.day,
                        onValueChange = { viewModel.day = it },
                        label = "Hari",
                        icon = Icons.Filled.CalendarToday,
                        placeholder = "DD-MM-YYYY"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernTextField(
                        value = viewModel.start_time,
                        onValueChange = { viewModel.start_time = it },
                        label = "Waktu Mulai",
                        icon = Icons.Filled.Schedule,
                        placeholder = "09:00"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernTextField(
                        value = viewModel.end_time,
                        onValueChange = { viewModel.end_time = it },
                        label = "Waktu Selesai",
                        icon = Icons.Filled.Schedule,
                        placeholder = "17:00"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernTextField(
                        value = viewModel.meeting_time,
                        onValueChange = { viewModel.meeting_time = it },
                        label = "Waktu Bertemu",
                        icon = Icons.Filled.Group,
                        placeholder = "08:30"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.activity_description,
                        onValueChange = { viewModel.activity_description = it },
                        label = { Text("Deskripsi Aktivitas") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            updatedActivity?.let { activity ->
                                val startISO = formatToISOString(viewModel.day, viewModel.start_time) ?: ""
                                val endISO = formatToISOString(viewModel.day, viewModel.end_time) ?: ""

                                if (endISO < startISO) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("End time cannot be earlier than start time")
                                    }
                                    return@Button
                                }

                                viewModel.updateItineraryDay(
                                    activity.id,
                                    UpdateItineraryDayRequest(
                                        id = activity.id,
                                        day = normalizeDateFormat(viewModel.day),
                                        start_time = startISO,
                                        end_time = endISO,
                                        meeting_time = normalizeTimeFormat(viewModel.meeting_time),
                                        activity_description = viewModel.activity_description
                                    )
                                )
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea))
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = "Update")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ubah")
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

@RequiresApi(Build.VERSION_CODES.O)
fun formatToLocalDateOnly(dateStr: String): String {
    return try {
        val parsed = OffsetDateTime.parse(dateStr)
            .withOffsetSameInstant(java.time.ZoneOffset.ofHours(7)) // Konversi ke GMT+7
        parsed.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    } catch (e: Exception) {
        ""
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatToTimeOnly(timeStr: String): String {
    return try {
        if (timeStr.contains("T")) {
            val parsed = OffsetDateTime.parse(timeStr)
                .withOffsetSameInstant(java.time.ZoneOffset.ofHours(7)) // Konversi ke WIB
            parsed.toLocalTime().format(DateTimeFormatter.ofPattern("HH.mm"))
        } else {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            val outputFormat = SimpleDateFormat("HH.mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            val date = inputFormat.parse(timeStr)
            outputFormat.format(date!!)
        }
    } catch (e: Exception) {
        Log.e("TIME_PARSE_ERROR", "Failed to parse meeting time: ${e.message}")
        ""
    }
}