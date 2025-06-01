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

    var day by remember { mutableStateOf("") }
    var start_Time by remember { mutableStateOf("") }
    var end_Time by remember { mutableStateOf("") }
    var meeting_Time by remember { mutableStateOf("") }
    var activity_Description by remember { mutableStateOf("") }

    val editId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("editId")
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val itineraryDays by viewModel.itineraryDayModel.collectAsState()
    var updatedActivity by remember { mutableStateOf<ItineraryDayModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllItineraryDays()
    }

    LaunchedEffect(itineraryDays) {
        updatedActivity = itineraryDays.find { it.id == editId }
    }

    if (updatedActivity == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    LaunchedEffect(updatedActivity) {
        updatedActivity?.let {
            day = formatToLocalDateOnly(it.day)
            start_Time = formatToTimeOnly(it.start_time)
            end_Time = formatToTimeOnly(it.end_time)
            meeting_Time = formatToTimeOnly(it.meeting_time)
            activity_Description = it.activity_description
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
                text = "Update Activity",
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
                        value = day,
                        onValueChange = { day = it },
                        label = "Day",
                        icon = Icons.Filled.CalendarToday,
                        placeholder = "DD-MM-YYYY"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernTextField(
                        value = start_Time,
                        onValueChange = { start_Time = it },
                        label = "Start Time",
                        icon = Icons.Filled.Schedule,
                        placeholder = "09:00"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernTextField(
                        value = end_Time,
                        onValueChange = { end_Time = it },
                        label = "End Time",
                        icon = Icons.Filled.Schedule,
                        placeholder = "17:00"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernTextField(
                        value = meeting_Time,
                        onValueChange = { meeting_Time = it },
                        label = "Meeting Time",
                        icon = Icons.Filled.Group,
                        placeholder = "08:30"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = activity_Description,
                        onValueChange = { activity_Description = it },
                        label = { Text("Activity Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            updatedActivity?.let { activity ->
                                val startISO = formatToISOString(day, start_Time) ?: ""
                                val endISO = formatToISOString(day, end_Time) ?: ""

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
                                        day = normalizeDateFormat(day),
                                        start_time = startISO,
                                        end_time = endISO,
                                        meeting_time = normalizeTimeFormat(meeting_Time),
                                        activity_description = activity_Description
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
                        Text("Update Activity")
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