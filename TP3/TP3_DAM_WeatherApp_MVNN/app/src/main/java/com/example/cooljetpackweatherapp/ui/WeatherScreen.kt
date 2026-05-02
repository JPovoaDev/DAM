package com.example.cooljetpackweatherapp.ui
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooljetpackweatherapp.data.WMO_WeatherCode
import com.example.cooljetpackweatherapp.viewport.WeatherViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherUI(
    weatherViewModel: WeatherViewModel = viewModel()
)
{
    val weatherUIState by weatherViewModel.uiState.collectAsState()
    val latitude = weatherViewModel.latt
    val longitude = weatherViewModel.long
    val temperature = weatherUIState.temperature
    val windSpeed = weatherUIState.windspeed
    val windDirection = weatherUIState.winddirection
    val weathercode = weatherUIState.weathercode
    val seaLevelPressure = weatherUIState.seaLevelPressure
    val time = weatherUIState.time

    val configuration = LocalConfiguration.current
    val day = weatherUIState.isDay

    val mapt = getWeatherCodeMap()
    val wCode = mapt.get(weathercode)

    val wImage = when (wCode) {
        WMO_WeatherCode.CLEAR_SKY,
        WMO_WeatherCode.MAINLY_CLEAR,
        WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode?.image + "_day" else wCode?.image + "_night"
        else -> wCode?.image
    }

    val context = LocalContext.current
    val wIcon = context.resources.getIdentifier(wImage, "drawable", context.packageName)

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        LandscapeWeatherUI(
            wIcon,
            latitude,
            longitude,
            temperature,
            windSpeed,
            windDirection,
            weathercode,
            seaLevelPressure,
            time,
            onLatitudeChange = {weatherViewModel.updateLatitude(it)},
            onLongitudeChange = { weatherViewModel.updateLongitude(it)},
            onUpdateButtonClick = { weatherViewModel.fetchWeather()}
        )
    } else {
        PortraitWeatherUI(
            wIcon,
            latitude,
            longitude,
            temperature,
            windSpeed,
            windDirection,
            weathercode,
            seaLevelPressure,
            time,
            onLatitudeChange = {weatherViewModel.updateLatitude(it)},
            onLongitudeChange = {weatherViewModel.updateLongitude(it)},
            onUpdateButtonClick = {weatherViewModel.fetchWeather()}
        )
    }
}

@Composable
fun PortraitWeatherUI(
    wIcon: Int,
    latitude: String,
    longitude: String,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weathercode: Int,
    seaLevelPressure: Float,
    time: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icone do tempo
        if (wIcon != 0) {
            Icon(
                painter = painterResource(id = wIcon),
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(120.dp)
                    .padding(16.dp),
                tint = Color.Unspecified
            )
        }

        CoordinatesCard(
            latitude = latitude,
            longitude = longitude,
            onLatitudeChange = onLatitudeChange,
            onLongitudeChange = onLongitudeChange
        )

        WeatherCard(
            seaLevelPressure = seaLevelPressure,
            windDirection = windDirection,
            windSpeed = windSpeed,
            temperature = temperature,
            time = time
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onUpdateButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C4DFF)
            )
        ) {
            Text(text = "Update Weather")
        }
    }
}

@Composable
fun LandscapeWeatherUI(
    wIcon: Int,
    latitude: String,
    longitude: String,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weathercode: Int,
    seaLevelPressure: Float,
    time: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icone do tempo à esquerda
            if (wIcon != 0) {
                Icon(
                    painter = painterResource(id = wIcon),
                    contentDescription = "Weather Icon",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp)
                        .align(Alignment.CenterVertically),
                    tint = Color.Unspecified
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                CoordinatesCard(
                    latitude = latitude,
                    longitude = longitude,
                    onLatitudeChange = onLatitudeChange,
                    onLongitudeChange = onLongitudeChange
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                WeatherCard(
                    seaLevelPressure = seaLevelPressure,
                    windDirection = windDirection,
                    windSpeed = windSpeed,
                    temperature = temperature,
                    time = time
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onUpdateButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C4DFF)
            )
        ) {
            Text(text = "Update Weather")
        }
    }
}

fun getWeatherCodeMap():Map<Int, WMO_WeatherCode>{
    return WMO_WeatherCode.entries.associateBy{it.code}
}