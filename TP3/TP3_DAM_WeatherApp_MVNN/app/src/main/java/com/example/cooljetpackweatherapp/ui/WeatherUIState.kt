package com.example.cooljetpackweatherapp.ui

data class WeatherUIState (
    val latitude: String = "",
    val longitude: String = "",
    val temperature: Float = 0f,
    val windspeed: Float = 0f,
    val winddirection: Int = 0,
    val seaLevelPressure: Float = 0f,
    val weathercode: Int = 0,
    val time: String = "",
    val isDay: Boolean = true
)