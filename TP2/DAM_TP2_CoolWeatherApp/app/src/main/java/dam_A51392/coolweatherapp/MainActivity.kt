package dam_A51392.coolweatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    // Variável que indica se é dia ou noite — usada para escolher o tema e o ícone
    private var day: Boolean = true

    companion object {
        // Código de identificação do pedido de permissão de localização
        private const val LOCATION_PERMISSION_REQUEST = 1001
    }

    // Verifica se a hora atual está entre o nascer e o pôr do sol
    private fun isDay(currentTime: String, sunrise: String, sunset: String): Boolean {
        val time = currentTime.substring(11) // extrai apenas a hora (HH:mm) da string completa
        return time >= sunrise && time <= sunset
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Aplica o tema correto (Dia/Noite, Portrait/Landscape) antes de criar a Activity
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

        // Ao arrancar, tenta obter a localização do dispositivo e buscar o tempo
        requestLocationAndFetch()

        // Quando o utilizador prime "ATUALIZAR", lê as coordenadas dos campos e busca o tempo
        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            val lat = findViewById<EditText>(R.id.tvLatitude).text.toString().replace(",", ".").toFloatOrNull()
            val lon = findViewById<EditText>(R.id.tvLongitude).text.toString().replace(",", ".").toFloatOrNull()
            if (lat != null && lon != null) {
                // Se os campos têm valores válidos, usa esses valores
                fetchWeatherData(lat, lon).start()
            } else {
                // Se os campos estão vazios ou inválidos, tenta usar o GPS
                requestLocationAndFetch()
            }
        }
    }

    // Suprime o aviso do Android Studio porque a permissão é verificada manualmente antes
    @SuppressLint("MissingPermission")
    private fun requestLocationAndFetch() {
        // Verifica se a permissão de localização foi concedida
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Se não foi concedida, pede a permissão ao utilizador
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        // Obtém o gestor de localização do sistema
        val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager

        // Tenta obter a última localização conhecida — primeiro por GPS, depois por rede
        val location = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            // Se obteve localização, preenche os campos e busca o tempo
            val lat = location.latitude.toFloat()
            val lon = location.longitude.toFloat()
            findViewById<EditText>(R.id.tvLatitude).setText(lat.toString())
            findViewById<EditText>(R.id.tvLongitude).setText(lon.toString())
            fetchWeatherData(lat, lon).start()
        } else {
            // Se não conseguiu localização, usa Lisboa como alternativa
            android.widget.Toast.makeText(this, "GPS não disponível. A usar Lisboa.", android.widget.Toast.LENGTH_SHORT).show()
            fetchWeatherData(38.7f, -9.1f).start()
        }
    }

    // Chamado pelo sistema quando o utilizador responde ao pedido de permissão
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida — tenta obter a localização
                requestLocationAndFetch()
            } else {
                // Permissão negada — usa Lisboa como alternativa
                android.widget.Toast.makeText(this, "Permissão negada. A usar Lisboa.", android.widget.Toast.LENGTH_SHORT).show()
                fetchWeatherData(38.7f, -9.1f).start()
            }
        }
    }

    // Constrói o URL e faz o pedido HTTP à API Open-Meteo, devolvendo os dados em formato WeatherData
    private fun WeatherAPI_Call(lat: Float, lon: Float): WeatherData {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true&hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m&daily=sunrise,sunset&timezone=auto"
        URL(url).openStream().use {
            // Converte a resposta JSON para o objeto WeatherData usando a biblioteca Gson
            return Gson().fromJson(InputStreamReader(it, "UTF-8"), WeatherData::class.java)
        }
    }

    // Cria uma Thread separada para fazer o pedido à API sem bloquear o interface
    private fun fetchWeatherData(lat: Float, lon: Float): Thread {
        return Thread {
            try {
                val weather = WeatherAPI_Call(lat, lon)
                updateUI(weather) // atualiza o interface com os dados recebidos
            } catch (e: Exception) {
                // Se ocorrer um erro, mostra uma mensagem ao utilizador
                runOnUiThread {
                    android.widget.Toast.makeText(this, "${e.javaClass.simpleName}: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Atualiza todos os elementos visuais do interface com os dados recebidos da API
    private fun updateUI(request: WeatherData) {
        runOnUiThread {
            // Mostra a latitude e longitude devolvidas pela API
            findViewById<EditText>(R.id.tvLatitude).setText(request.latitude)
            findViewById<EditText>(R.id.tvLongitude).setText(request.longitude)

            // Mostra a pressão ao nível do mar (índice 12 = meio-dia aproximado)
            findViewById<TextView>(R.id.tvPressure).text = "${request.hourly.pressure_msl.getOrNull(12) ?: "--"} hPa"

            // Mostra a temperatura atual
            findViewById<TextView>(R.id.tvTemp).text = "${request.current_weather.temperature} °C"

            // Mostra a velocidade do vento
            findViewById<TextView>(R.id.tvWindSpeed).text = "${request.current_weather.windspeed} km/h"

            // Mostra a direção do vento em graus
            findViewById<TextView>(R.id.tvWindDir).text = "${request.current_weather.winddirection}°"

            // Mostra a hora da observação meteorológica
            findViewById<TextView>(R.id.tvTime).text = request.current_weather.time

            // Determina se é dia ou noite com base na hora atual, nascer e pôr do sol
            day = isDay(
                request.current_weather.time,
                request.daily.sunrise[0].substring(11), // extrai hora do nascer do sol
                request.daily.sunset[0].substring(11)   // extrai hora do pôr do sol
            )

            // Muda o fundo da aplicação consoante dia/noite e orientação do ecrã
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val container = findViewById<android.view.View>(R.id.container)
            container.setBackgroundResource(
                if (day) { if (isLandscape) R.drawable.sunny_bg_land else R.drawable.sunny_bg }
                else     { if (isLandscape) R.drawable.night_bg_land else R.drawable.night_bg }
            )

            // Determina o ícone meteorológico a mostrar com base no código WMO
            val wCode = getWeatherCodeMap()[request.current_weather.weathercode]
            val wImage = if (wCode != null) {
                when (wCode) {
                    // Para céu limpo, maioritariamente limpo e parcialmente nublado,
                    // usa versão "day" ou "night" do ícone
                    WMO_WeatherCode.CLEAR_SKY,
                    WMO_WeatherCode.MAINLY_CLEAR,
                    WMO_WeatherCode.PARTLY_CLOUDY -> wCode.image + if (day) "day" else "night"
                    // Para os restantes códigos, o ícone é o mesmo para dia e noite
                    else -> wCode.image
                }
            } else {
                // Código desconhecido — usa ícone de céu limpo como alternativa
                if (day) "clear_day" else "clear_night"
            }

            // Carrega o drawable correspondente ao nome do ícone e aplica-o à ImageView
            val resID = resources.getIdentifier(wImage, "drawable", packageName)
            val weatherImage = findViewById<ImageView>(R.id.imgWeatherIcon)
            // Se o drawable existir usa-o, caso contrário usa o ícone padrão
            weatherImage.setImageResource(if (resID != 0) resID else if (day) R.drawable.clear_day else R.drawable.clear_night)
        }
    }
}