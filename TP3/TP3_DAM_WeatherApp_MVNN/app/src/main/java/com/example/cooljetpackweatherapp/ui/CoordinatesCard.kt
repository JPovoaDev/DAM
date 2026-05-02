package com.example.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CoordinatesCard(
    latitude: String,
    longitude: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = "Coordinates")

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = latitude,
                onValueChange = onLatitudeChange,
                label = { Text("Latitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = longitude,
                onValueChange = onLongitudeChange,
                label = { Text("Longitude") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}