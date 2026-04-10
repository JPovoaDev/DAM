package dam_A51392.coolweatherapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.widget.TextView
import android.content.res.Configuration
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL


class MainActivity : AppCompatActivity() {
    // Muda para false para testar o tema de noite
    private var day: Boolean = false

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
        // Coordenadas iniciais (exemplo: Lisboa)
        val lat = 38.7f
        val long = -9.1f

    // Chamada inicial ao arrancar a app
        fetchWeatherData(lat, long).start()

    // Botão UPDATE
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        btnUpdate.setOnClickListener {
            val latInput = findViewById<EditText>(R.id.tvLatitude).text.toString().toFloatOrNull() ?: lat
            val longInput = findViewById<EditText>(R.id.tvLongitude).text.toString().toFloatOrNull() ?: long
            fetchWeatherData(latInput, longInput).start()
        }

        // Muda o ícone consoante o dia ou noite
        val imgWeatherIcon = findViewById<ImageView>(R.id.imgWeatherIcon)
        if (day) {
            imgWeatherIcon.setImageResource(R.drawable.clear_day)
        } else {
            imgWeatherIcon.setImageResource(R.drawable.clear_night)
        }
    }
    private fun WeatherAPI_Call(lat: Float, long: Float): WeatherData {
        val reqString = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${lat}&longitude=${long}&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m")
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
            val weather = WeatherAPI_Call(lat, long)
            updateUI(weather)
        }
    }

    private fun updateUI(request: WeatherData) {
        runOnUiThread {
            // Weather Image
            val weatherImage: ImageView = findViewById(R.id.imgWeatherIcon)

            // Pressure
            val pressure: TextView = findViewById(R.id.tvPressure)
            pressure.text = request.hourly.pressure_msl.get(12).toString() + " hPa"

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

            // Latitude
            val lat: TextView = findViewById(R.id.tvLatitude)
            lat.text = request.latitude

            // Longitude
            val lon: TextView = findViewById(R.id.tvLongitude)
            lon.text = request.longitude

            // Weather Icon
            val mapt = getWeatherCodeMap()
            val wCode = mapt.get(request.current_weather.weathercode)
            val wImage = when (wCode) {
                WMO_WeatherCode.CLEAR_SKY,
                WMO_WeatherCode.MAINLY_CLEAR,
                WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode?.image + "day" else wCode?.image + "night"
                else -> wCode?.image
            }

            val res = resources
            val resID = res.getIdentifier(wImage, "drawable", packageName)
            val drawable = this.getDrawable(resID)
            if (drawable != null) {
                weatherImage.setImageDrawable(drawable)
            } else {
                // fallback se o drawable não existir
                weatherImage.setImageResource(R.drawable.fog)
            }
        }
    }
}