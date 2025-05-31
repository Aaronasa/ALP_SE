package com.example.alp_se.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.alp_se.Model.ItineraryDayModel
import com.example.alp_se.Route.listScreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryDayView(
    onBack: () -> Unit = {},
    navController: NavController? = null,
    itineraryId: Int = 0, // Pass the itinerary ID
    // itineraryDayViewModel: ItineraryDayViewModel = viewModel() // You'll need to create this ViewModel
) {
    // State for day activities
    var dayActivities by remember {
        mutableStateOf(
            listOf(
                ItineraryDayModel(day = "Day 1"),
                ItineraryDayModel(day = "Day 2"),
                ItineraryDayModel(day = "Day 3")
            )
        )
    }

    // Current editing activity
    var editingIndex by remember { mutableStateOf(-1) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Time picker states for editing
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showMeetingTimePicker by remember { mutableStateOf(false) }

    // Time picker state objects
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()
    val meetingTimePickerState = rememberTimePickerState()

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
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = "Daily Schedule",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = "Daily Activities",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle
                    Text(
                        text = "Manage your daily schedule",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Content Section
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
                ) {
                    // Progress indicator
                    LinearProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFF667eea),
                        trackColor = Color(0xFF667eea).copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section Header with Add Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionHeader(title = "Activities Schedule")

                        // Add Activity Button
                        IconButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color(0xFF667eea),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Activity",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Activities List
                    if (dayActivities.isEmpty()) {
                        // Empty State
                        EmptyStateCard(
                            onAddActivity = { showAddDialog = true }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(dayActivities) { index, activity ->
                                ActivityCard(
                                    activity = activity,
                                    onEdit = { editingIndex = index },
                                    onDelete = {
                                        dayActivities = dayActivities.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save All Button
                    Button(
                        onClick = {
                            // Save all activities
                            // itineraryDayViewModel.saveAllActivities(dayActivities)
                            println("Saving all activities: $dayActivities")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667eea)
                        ),
                        enabled = dayActivities.isNotEmpty()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = "Save",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save All Activities",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Add/Edit Activity Dialog
        if (showAddDialog || editingIndex >= 0) {
            AddEditActivityDialog(
                activity = if (editingIndex >= 0) dayActivities[editingIndex] else ItineraryDayModel(),
                isEditing = editingIndex >= 0,
                onSave = { updatedActivity ->
                    if (editingIndex >= 0) {
                        // Update existing activity
                        dayActivities = dayActivities.toMutableList().apply {
                            this[editingIndex] = updatedActivity
                        }
                        editingIndex = -1
                    } else {
                        // Add new activity
                        dayActivities = dayActivities + updatedActivity
                        showAddDialog = false
                    }
                },
                onDismiss = {
                    showAddDialog = false
                    editingIndex = -1
                }
            )
        }

        // Time Pickers
        if (showStartTimePicker) {
            TimePickerDialog(
                onTimeSelected = { hour, minute ->
                    val formattedTime = String.format("%02d:%02d", hour, minute)
                    // Handle start time selection
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
                    // Handle end time selection
                    showEndTimePicker = false
                },
                onDismiss = { showEndTimePicker = false },
                timePickerState = endTimePickerState
            )
        }

        if (showMeetingTimePicker) {
            TimePickerDialog(
                onTimeSelected = { hour, minute ->
                    val formattedTime = String.format("%02d:%02d", hour, minute)
                    // Handle meeting time selection
                    showMeetingTimePicker = false
                },
                onDismiss = { showMeetingTimePicker = false },
                timePickerState = meetingTimePickerState
            )
        }
    }
}

@Composable
private fun ActivityCard(
    activity: ItineraryDayModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with day and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.day,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF667eea)
                )

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color(0xFF667eea).copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color(0xFFFF4444).copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeInfoChip(
                    icon = Icons.Filled.Schedule,
                    label = "Start",
                    time = activity.start_time.ifBlank { "Not set" },
                    modifier = Modifier.weight(1f)
                )

                TimeInfoChip(
                    icon = Icons.Filled.Schedule,
                    label = "End",
                    time = activity.end_time.ifBlank { "Not set" },
                    modifier = Modifier.weight(1f)
                )
            }

            if (activity.meeting_time.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                TimeInfoChip(
                    icon = Icons.Filled.Group,
                    label = "Meeting",
                    time = activity.meeting_time,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (activity.activity_description.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = activity.activity_description,
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun TimeInfoChip(
    icon: ImageVector,
    label: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF667eea),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color(0xFF333333),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    onAddActivity: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = "No Activities",
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No activities planned yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start by adding your first daily activity",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAddActivity,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add First Activity")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditActivityDialog(
    activity: ItineraryDayModel,
    isEditing: Boolean,
    onSave: (ItineraryDayModel) -> Unit,
    onDismiss: () -> Unit
) {
    var editableActivity by remember { mutableStateOf(activity) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onSave(editableActivity) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea)),
                enabled = editableActivity.day.isNotBlank()
            ) {
                Text(if (isEditing) "Update" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text = if (isEditing) "Edit Activity" else "Add New Activity",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Day field
                ModernTextField(
                    value = editableActivity.day,
                    onValueChange = { editableActivity = editableActivity.copy(day = it) },
                    label = "Day",
                    icon = Icons.Filled.CalendarToday,
                    placeholder = "e.g., Day 1, Monday"
                )

                // Time fields
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModernTextField(
                        value = editableActivity.start_time,
                        onValueChange = { editableActivity = editableActivity.copy(start_time = it) },
                        label = "Start Time",
                        icon = Icons.Filled.Schedule,
                        placeholder = "09:00",
                        modifier = Modifier.weight(1f)
                    )

                    ModernTextField(
                        value = editableActivity.end_time,
                        onValueChange = { editableActivity = editableActivity.copy(end_time = it) },
                        label = "End Time",
                        icon = Icons.Filled.Schedule,
                        placeholder = "17:00",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Meeting time
                ModernTextField(
                    value = editableActivity.meeting_time,
                    onValueChange = { editableActivity = editableActivity.copy(meeting_time = it) },
                    label = "Meeting Time",
                    icon = Icons.Filled.Group,
                    placeholder = "08:30"
                )

                // Activity description
                OutlinedTextField(
                    value = editableActivity.activity_description,
                    onValueChange = { editableActivity = editableActivity.copy(activity_description = it) },
                    label = { Text("Activity Description") },
                    placeholder = { Text("Describe the day's activities...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF667eea),
                        focusedLabelColor = Color(0xFF667eea),
                        cursorColor = Color(0xFF667eea)
                    )
                )
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

// Reuse existing composables from CreateItineraryView
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

@Composable
private fun SectionHeader(title: String) {
    Row(
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
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF667eea),
            focusedLabelColor = Color(0xFF667eea),
            cursorColor = Color(0xFF667eea)
        ),
        keyboardOptions = keyboardOptions
    )
}