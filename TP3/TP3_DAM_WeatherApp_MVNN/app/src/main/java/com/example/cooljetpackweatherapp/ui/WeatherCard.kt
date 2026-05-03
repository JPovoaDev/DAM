package com.example.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeatherCard(
    seaLevelPressure: Float,
    windDirection: Int,
    windSpeed: Float,
    temperature: Float,
    time: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            WeatherRow("Sea Level Pressure", "$seaLevelPressure hPa")
            WeatherRow("Wind Direction", "$windDirection°")
            WeatherRow("Wind Speed", "$windSpeed km/h")
            WeatherRow("Temperature", "$temperature °C")
            WeatherRow("Time", time)
        }
    }
}