package dam_A51392.coolweatherapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.content.res.Configuration
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var day: Boolean = true
    private var firstLoad: Boolean = true

    private fun isDay(currentTime: String, sunrise: String, sunset: String): Boolean {
        val time = currentTime.substring(11)
        return time >= sunrise && time <= sunset
    }

    override fun onCreate(savedInstanceState: Bundle?) {



        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                if (day) setTheme(R.style.Theme_Day)
                else setTheme(R.style.Theme_Night)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (day) setTheme(R.style.Theme_Day_Land)
                else setTheme(R.style.Theme_Night_Land)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lat = 38.7f
        val long = -9.1f

        // Chamada inicial
        fetchWeatherData(lat, long).start()

        // Botão UPDATE
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        btnUpdate.setOnClickListener {
            val latInput = findViewById<EditText>(R.id.tvLatitude)
                .text.toString()
                .replace(",", ".")
                .toFloatOrNull() ?: lat

            val longInput = findViewById<EditText>(R.id.tvLongitude)
                .text.toString()
                .replace(",", ".")
                .toFloatOrNull() ?: long

            fetchWeatherData(latInput, longInput).start()
        }
    }

    private fun WeatherAPI_Call(lat: Float, long: Float): WeatherData {
        val reqString = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${lat}&longitude=${long}&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m&")
            append("daily=sunrise,sunset&")
            append("timezone=auto")
        }

        val url = URL(reqString)
        url.openStream().use {
            val request = Gson().fromJson(
                InputStreamReader(it, "UTF-8"),
                WeatherData::class.java
            )
            return request
        }
    }

    private fun fetchWeatherData(lat: Float, long: Float): Thread {
        return Thread {
            try {
                val weather = WeatherAPI_Call(lat, long)
                updateUI(weather)
            } catch (e: Exception) {
                runOnUiThread {
                    android.widget.Toast.makeText(
                        this@MainActivity,
                        e.javaClass.simpleName + ": " + e.message,
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updateUI(request: WeatherData) {
        runOnUiThread {
            val weatherImage: ImageView = findViewById(R.id.imgWeatherIcon)

            // Pressure
            val pressure: TextView = findViewById(R.id.tvPressure)
            val pressureVal = request.hourly.pressure_msl.getOrNull(12)
                ?: request.hourly.pressure_msl.firstOrNull()
            pressure.text = "${pressureVal ?: "--"} hPa"

            // Temperature
            val temp: TextView = findViewById(R.id.tvTemp)
            temp.text = request.current_weather.temperature.toString() + " °C"

            // Wind Speed
            val windSpeed: TextView = findViewById(R.id.tvWindSpeed)
            windSpeed.text = request.current_weather.windspeed.toString() + " km/h"

            // Wind Direction
            val windDir: TextView = findViewById(R.id.tvWindDir)
            windDir.text = request.current_weather.winddirection.toString() + "°"

            // Time
            val time: TextView = findViewById(R.id.tvTime)
            time.text = request.current_weather.time

            // Atualiza dia/noite
            day = isDay(
                request.current_weather.time,
                request.daily.sunrise[0].substring(11),
                request.daily.sunset[0].substring(11)
            )

            // Background
            val container = findViewById<android.view.View>(R.id.container)
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            if (day) {
                container.setBackgroundResource(if (isLandscape) R.drawable.sunny_bg_land else R.drawable.sunny_bg)
            } else {
                container.setBackgroundResource(if (isLandscape) R.drawable.night_bg_land else R.drawable.night_bg)
            }

            // Weather Icon
            val mapt = getWeatherCodeMap()
            val wCode = mapt[request.current_weather.weathercode]

            val wImage = if (wCode != null) {
                when (wCode) {
                    WMO_WeatherCode.CLEAR_SKY,
                    WMO_WeatherCode.MAINLY_CLEAR,
                    WMO_WeatherCode.PARTLY_CLOUDY -> wCode.image + if (day) "day" else "night"
                    else -> wCode.image
                }
            } else {
                if (day) "clear_day" else "clear_night"
            }

            // Aplica o icon
            val resID = resources.getIdentifier(wImage, "drawable", packageName)
            if (resID != 0) {
                weatherImage.setImageDrawable(this.getDrawable(resID))
            } else {
                if (day) weatherImage.setImageResource(R.drawable.clear_day)
                else weatherImage.setImageResource(R.drawable.clear_night)
            }
        }
    }
}