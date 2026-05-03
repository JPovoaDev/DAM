package com.example.cooljetpackweatherapp.viewport

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooljetpackweatherapp.data.FavoriteLocation
import com.example.cooljetpackweatherapp.data.WeatherApiClient
import com.example.cooljetpackweatherapp.ui.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    var latt by mutableStateOf("")
        private set

    var long by mutableStateOf("")
        private set

    var locationName by mutableStateOf("")
        private set

    var favoriteLocations by mutableStateOf(listOf<FavoriteLocation>())
        private set

    fun updateLatitude(inputLatt: String) {
        latt = inputLatt
    }

    fun updateLongitude(inputLong: String) {
        long = inputLong
    }

    fun updateLocationName(name: String) {
        locationName = name
    }

    fun addFavoriteLocation() {
        val newLocation = FavoriteLocation(
            name = locationName,
            latitude = latt,
            longitude = long
        )
        favoriteLocations = favoriteLocations + newLocation
        locationName = ""
    }

    fun selectFavoriteLocation(location: FavoriteLocation) {
        updateLatitude(location.latitude)
        updateLongitude(location.longitude)
        fetchWeather()
    }

    fun fetchWeather() {
        val lat = latt.toFloatOrNull() ?: return
        val lon = long.toFloatOrNull() ?: return
        viewModelScope.launch {
            val result = WeatherApiClient.getWeather(lat, lon)
            if (result != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        temperature = result.current_weather.temperature,
                        windspeed = result.current_weather.windspeed,
                        winddirection = result.current_weather.winddirection,
                        weathercode = result.current_weather.weathercode,
                        seaLevelPressure = result.hourly.pressure_msl.firstOrNull()?.toFloat() ?: 0f,
                        time = result.current_weather.time,
                        isDay = result.current_weather.is_day == 1
                    )
                }
            }
        }
    }
}