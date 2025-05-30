package com.example.alp_se.View

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernItineraryDayCard(
    itineraryDay: ItineraryDayModel,
    dayNumber: Int? = null,
    onClick: () -> Unit = {},
    gradientColors: List<Color> = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2)
    )
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Format time display
    val timeDisplay = if (itineraryDay.start_time.isNotEmpty() && itineraryDay.end_time.isNotEmpty()) {
        "${itineraryDay.start_time} - ${itineraryDay.end_time}"
    } else if (itineraryDay.meeting_time.isNotEmpty()) {
        itineraryDay.meeting_time
    } else {
        "Time TBD"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clickable {
                isExpanded = !isExpanded
                onClick()
            }
            .animateContentSize(
                animationSpec = tween(300)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isExpanded) 200.dp else 140.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = gradientColors.map { it.copy(alpha = 0.1f) }
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Day indicator
                    if (dayNumber != null) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.radialGradient(gradientColors),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    // Title and status
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = itineraryDay.day.ifEmpty { "Day ${dayNumber ?: ""}" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748),
                            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Expand arrow
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowRight,
                        contentDescription = "Expand",
                        tint = gradientColors.first(),
                        modifier = Modifier
                            .size(24.dp)
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time and meeting time info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoChip(
                        icon = Icons.Filled.Schedule,
                        text = timeDisplay,
                        color = gradientColors.first()
                    )

                    if (itineraryDay.meeting_time.isNotEmpty() &&
                        itineraryDay.meeting_time != itineraryDay.start_time) {
                        InfoChip(
                            icon = Icons.Filled.AccessTime,
                            text = "Meet: ${itineraryDay.meeting_time}",
                            color = gradientColors.last()
                        )
                    }
                }

                // Description
                if (isExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        color = Color.Gray.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = "Activity Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = itineraryDay.activity_description.ifEmpty { "No description available" },
                        fontSize = 14.sp,
                        color = Color(0xFF718096),
                        lineHeight = 20.sp
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = itineraryDay.activity_description.ifEmpty { "No description available" },
                        fontSize = 14.sp,
                        color = Color(0xFF718096),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFAFAFA)
@Composable
fun ModernItineraryDayCardPreview() {
    MaterialTheme {
        ModernItineraryDayCard(
            itineraryDay = ItineraryDayModel(
                id = 1,
                day = "Day 1 - Arrival",
                start_time = "09:00",
                end_time = "18:00",
                activity_description = "Airport pickup, hotel check-in, welcome dinner at beachfront restaurant with traditional Balinese cuisine and cultural performance.",
                meeting_time = "09:00",
                itineraryId = 1
            ),
            dayNumber = 1
        )
    }
}