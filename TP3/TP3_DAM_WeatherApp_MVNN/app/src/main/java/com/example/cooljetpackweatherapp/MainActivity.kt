package com.example.cooljetpackweatherapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooljetpackweatherapp.ui.WeatherUI
import com.example.cooljetpackweatherapp.viewport.WeatherViewModel

class MainActivity : ComponentActivity() {

    // criamos isto fora do setContent para quando clicarmos numa localizacoa conseguir chamar o updateLat e
    //updateLong
    private lateinit var weatherViewModel: WeatherViewModel

    // Nos aqui tamos a lancar a atividade e receber o resultado de volta, Ou seja,
    //assim qe clicamos no botao do confirm results este bloco ativa a mainatvivty de volta com os resultados
    // da latitude e longitude de volta

    private val locationPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val lat = result.data?.getFloatExtra("latitude", 0f) ?: 0f
            val lon = result.data?.getFloatExtra("longitude", 0f) ?: 0f
            weatherViewModel.updateLatitude(lat.toString())
            weatherViewModel.updateLongitude(lon.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            weatherViewModel = viewModel()
            WeatherUI(
                // aqui criamos no viewModel este onLoctionPicerCliker para quando clicarmos no botao
                // do icon do coordiantes card,, lancar a atividade LocationPicker
                weatherViewModel = weatherViewModel,
                onLocationPickerClick = {
                    val intent = android.content.Intent(this, com.example.cooljetpackweatherapp.ui.LocationPickerActivity::class.java)
                    locationPickerLauncher.launch(intent)
                }
            )
        }
    }
}