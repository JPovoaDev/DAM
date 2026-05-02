package com.example.cooljetpackweatherapp.viewport
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooljetpackweatherapp.data.WMO_WeatherCode
import com.example.cooljetpackweatherapp.data.WeatherApiClient
import com.example.cooljetpackweatherapp.data.WeatherApiClient.getWeather
import com.example.cooljetpackweatherapp.ui.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    // by mutableStateOf(""), cria uma variavel que consegue ver, quando o valor muda os composables que a usam
    //recompoem se automaticamente.

    //o privete set so o viewModel pode muda-lo. Ou seja a UI le as variaveis manda o valor para o viewmodel e o view model
    //altera-o apartir dos updates.

    //a diferenca entre esta e o _uistate é que o este é para o estado geral da app, enquanto esta é para alteracoes locais
    //alteracoes gerais é por exemplo a temperatura ...
    var latt by mutableStateOf("38.7")
        private set

    var long by mutableStateOf("-9.1")
        private set

     fun updateLatitude(inputLatt:String){
        latt=inputLatt

    }
     fun updateLongitude(inputLong:String){
        long=inputLong

    }
     fun fetchWeather(){
         //transformamos a latitude e a longitude para float de maneira a que a chamada da funcao da api seja feita corretamente println("latt: $latt, long: $long")

         val lat = latt.toFloatOrNull() ?: return
        val lon = long.toFloatOrNull() ?: return

         viewModelScope.launch {
             //chamamos para apamjar os dados da api com aquela latitude e longitude
             val result = WeatherApiClient.getWeather(lat, lon)

                 if (result != null) {
                     //damos update ao estado e returnamos os reusltados da chamada da api
                     _uiState.update { currentState ->
                         currentState.copy(
                             isDay = result.current_weather.is_day == 1,
                             temperature = result.current_weather.temperature,
                             windspeed = result.current_weather.windspeed,
                             winddirection = result.current_weather.winddirection,
                             weathercode = result.current_weather.weathercode,
                             seaLevelPressure = result.hourly.pressure_msl.firstOrNull()?.toFloat() ?: 0f,
                             time = result.current_weather.time
                         )
                     }
                 }
         }
     }

}