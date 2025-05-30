package com.example.alp_se.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_se.Model.CreateItineraryRequest
import com.example.alp_se.Route.listScreen
import com.example.alp_se.ViewModel.ItineraryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItineraryView(
    onBack: () -> Unit = {},
    navController: NavController? = null,
    itineraryViewModel: ItineraryViewModel = viewModel()
) {
    // Use ViewModel state directly instead of local state
    val title = itineraryViewModel.title
    val startDate = itineraryViewModel.start_date
    val endDate = itineraryViewModel.end_date
    val location = itineraryViewModel.location
    val totalPerson = itineraryViewModel.total_person
    val country = itineraryViewModel.country
    val estimateStart = itineraryViewModel.estimate_start
    val estimateEnd = itineraryViewModel.estimate_end

    // Date picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Date picker state objects
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()

    // Listen to status messages
    val statusMessage by itineraryViewModel.statusMessage.collectAsState()

    // Show status messages
    LaunchedEffect(statusMessage) {
        statusMessage?.let { message ->
            // You can show a Toast or Snackbar here if needed
            println("Status: $message") // For debugging
        }
    }

    // Helper function to combine date and time into ISO string
    fun combineDateTime(dateStr: String, timeStr: String): String {
        return if (dateStr.isNotBlank() && timeStr.isNotBlank()) {
            "${dateStr}T${timeStr}:00.000Z"
        } else if (dateStr.isNotBlank()) {
            "${dateStr}T00:00:00.000Z"
        } else {
            ""
        }
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
                // Back Button
                IconButton(
                    onClick = {
                        navController?.navigate(listScreen.ListItineraryView.name)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icon with background
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
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Create",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = "Create Itinerary",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle
                    Text(
                        text = "Plan your next adventure",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Form Section
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    ),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progress indicator
                    LinearProgressIndicator(
                        progress = { 0.0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFF667eea),
                        trackColor = Color(0xFF667eea).copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Basic Information Section
                    SectionHeader(title = "Basic Information")

                    ModernTextField(
                        value = title,
                        onValueChange = { itineraryViewModel.updateTitle(it) },
                        label = "Trip Title",
                        icon = Icons.Filled.Title,
                        placeholder = "e.g., Bali Adventure 2025"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DatePickerTextField(
                            value = startDate,
                            label = "Start Date",
                            placeholder = "Select start date",
                            modifier = Modifier.weight(1f),
                            onClick = { showStartDatePicker = true }
                        )

                        DatePickerTextField(
                            value = endDate,
                            label = "End Date",
                            placeholder = "Select end date",
                            modifier = Modifier.weight(1f),
                            onClick = { showEndDatePicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Location Information Section
                    SectionHeader(title = "Location Details")

                    ModernTextField(
                        value = country,
                        onValueChange = { itineraryViewModel.updateCountry(it) },
                        label = "Country",
                        icon = Icons.Filled.Public,
                        placeholder = "e.g., Indonesia"
                    )

                    ModernTextField(
                        value = location,
                        onValueChange = { itineraryViewModel.updateLocation(it) },
                        label = "Specific Location",
                        icon = Icons.Filled.LocationOn,
                        placeholder = "e.g., Denpasar, Bali"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Trip Details Section
                    SectionHeader(title = "Trip Details")

                    ModernTextField(
                        value = totalPerson,
                        onValueChange = { itineraryViewModel.updateTotalPerson(it) },
                        label = "Number of People",
                        icon = Icons.Filled.Group,
                        placeholder = "e.g., 4",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TimePickerTextField(
                            value = estimateStart,
                            label = "Start Time",
                            placeholder = "Select time",
                            modifier = Modifier.weight(1f),
                            onClick = { showStartTimePicker = true }
                        )

                        TimePickerTextField(
                            value = estimateEnd,
                            label = "End Time",
                            placeholder = "Select time",
                            modifier = Modifier.weight(1f),
                            onClick = { showEndTimePicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            println("Button clicked!") // Debug log
                            println("Title: $title")
                            println("Start Date: $startDate")
                            println("End Date: $endDate")
                            println("Location: $location")
                            println("Country: $country")
                            println("Total Person: $totalPerson")
                            println("Estimate Start: $estimateStart")
                            println("Estimate End: $estimateEnd")

                            // Create proper datetime strings for estimate_start and estimate_end
                            val estimateStartDateTime = combineDateTime(startDate, estimateStart)
                            val estimateEndDateTime = combineDateTime(endDate, estimateEnd)

                            println("Estimate Start DateTime: $estimateStartDateTime")
                            println("Estimate End DateTime: $estimateEndDateTime")

                            val request = CreateItineraryRequest(
                                title = title,
                                start_date = startDate,
                                end_date = endDate,
                                estimate_start = estimateStartDateTime, // Use combined datetime
                                estimate_end = estimateEndDateTime,     // Use combined datetime
                                total_person = totalPerson.toIntOrNull() ?: 0,
                                country = country,
                                location = location,
                                userId = 1 // TODO: Get actual user ID
                            )

                            println("Request: $request") // Debug log
                            itineraryViewModel.createItinerary(request)
                            navController?.navigate(listScreen.ListItineraryView.name)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667eea)
                        ),
                        enabled = title.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()
                    ) {
                        Text(
                            text = "Create Itinerary",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Date Pickers
        if (showStartDatePicker) {
            DatePickerDialog(
                onDateSelected = { dateInMillis ->
                    dateInMillis?.let {
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formattedDate = formatter.format(Date(it))
                        itineraryViewModel.updateStartDate(formattedDate)
                        println("Start date selected: $formattedDate") // Debug log
                    }
                    showStartDatePicker = false
                },
                onDismiss = { showStartDatePicker = false },
                datePickerState = startDatePickerState
            )
        }

        if (showEndDatePicker) {
            DatePickerDialog(
                onDateSelected = { dateInMillis ->
                    dateInMillis?.let {
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formattedDate = formatter.format(Date(it))
                        itineraryViewModel.updateEndDate(formattedDate)
                        println("End date selected: $formattedDate") // Debug log
                    }
                    showEndDatePicker = false
                },
                onDismiss = { showEndDatePicker = false },
                datePickerState = endDatePickerState
            )
        }

        // Time Pickers
        if (showStartTimePicker) {
            TimePickerDialog(
                onTimeSelected = { hour, minute ->
                    val formattedTime = String.format("%02d:%02d", hour, minute)
                    itineraryViewModel.updateEstimateStart(formattedTime)
                    println("Start time selected: $formattedTime") // Debug log
                    showStartTimePicker = false
                },
                onDismiss = { showStartTimePicker = false },
                timePickerState = startTimePickerState
            )
        }

        if (showEndTimePicker) {
            TimePickerDialog(
                onTimeSelected = { hour, minute ->
                    val formattedTime = String.format("%02d:%02d", hour, minute)
                    itineraryViewModel.updateEstimateEnd(formattedTime)
                    println("End time selected: $formattedTime") // Debug log
                    showEndTimePicker = false
                },
                onDismiss = { showEndTimePicker = false },
                timePickerState = endTimePickerState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    datePickerState: DatePickerState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    timePickerState: TimePickerState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerTextField(
    value: String,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = label,
                tint = Color(0xFF667eea)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF667eea),
            focusedLabelColor = Color(0xFF667eea),
            cursorColor = Color(0xFF667eea),
            disabledBorderColor = Color(0xFF667eea).copy(alpha = 0.5f),
            disabledLabelColor = Color(0xFF667eea).copy(alpha = 0.7f),
            disabledTextColor = Color.Black
        ),
        readOnly = true,
        enabled = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerTextField(
    value: String,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = label,
                tint = Color(0xFF667eea)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF667eea),
            focusedLabelColor = Color(0xFF667eea),
            cursorColor = Color(0xFF667eea),
            disabledBorderColor = Color(0xFF667eea).copy(alpha = 0.5f),
            disabledLabelColor = Color(0xFF667eea).copy(alpha = 0.7f),
            disabledTextColor = Color.Black
        ),
        readOnly = true,
        enabled = false
    )
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .background(
                    color = Color(0xFF667eea),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF667eea)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF667eea),
            focusedLabelColor = Color(0xFF667eea),
            cursorColor = Color(0xFF667eea)
        ),
        keyboardOptions = keyboardOptions
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateItineraryViewPreview() {
    // Preview without NavController and using default ViewModel
    CreateItineraryView(
        onBack = {},            // No-op for back
        navController = null    // No navigation in preview
    )
}

