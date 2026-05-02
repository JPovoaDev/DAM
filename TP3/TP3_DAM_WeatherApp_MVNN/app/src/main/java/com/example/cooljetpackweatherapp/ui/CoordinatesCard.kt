package com.example.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color

@Composable
fun CoordinatesCard(
    latitude: String,
    longitude: String,
    locationName: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onLocationNameChange: (String) -> Unit,
    onSaveLocation: () -> Unit,
    onLocationPickerClick: () -> Unit

) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = "Coordinates")
            IconButton(onClick = onLocationPickerClick) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Location Picker"
                )
            }

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
            OutlinedTextField(
                value = locationName,
                onValueChange = onLocationNameChange,
                label = { Text("Location Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSaveLocation,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C4DFF)
                )
            ) {
                Text("Save Location")
            }
        }
    }
}