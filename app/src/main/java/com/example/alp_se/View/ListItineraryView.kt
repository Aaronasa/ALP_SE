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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alp_se.Model.ItineraryModel

@Composable
fun ListItineraryView() {
    // Sample data untuk preview
    val sampleItineraries = listOf(

    ItineraryModel(
            title = "Liburan Bali",
            start_date = "20 Dec",
            end_date = "22 Dec",
            location = "Denpasar, Bali",
            total_person = 5
        ),
    ItineraryModel(
            title = "Jakarta Business Trip",
        start_date = "15 Jan",
        end_date = "18 Jan",
            location = "Jakarta, Indonesia",
        total_person = 3
        ),
    ItineraryModel(
            title = "Yogyakarta Cultural Tour",
        start_date = "05 Feb",
        end_date = "07 Feb",
            location = "Yogyakarta, Indonesia",
        total_person = 8
        ),
    ItineraryModel(
            title = "Bandung Adventure",
        start_date = "12 Mar",
        end_date = "14 Mar",
            location = "Bandung, Jawa Barat",
        total_person = 4
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sampleItineraries) { itinerary ->
            ItineraryCard(
                title = itinerary.title,
                startDate = itinerary.start_date,
                endDate = itinerary.end_date,
                location = itinerary.location,
                participantCount = itinerary.total_person
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ListItineraryViewPreview() {
    MaterialTheme {
        ListItineraryView()
    }
}