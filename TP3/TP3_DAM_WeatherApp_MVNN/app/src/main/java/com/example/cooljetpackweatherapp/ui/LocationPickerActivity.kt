package com.example.cooljetpackweatherapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooljetpackweatherapp.viewport.LocationPickerViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class LocationPickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val locationPickerViewModel: LocationPickerViewModel = viewModel()
            val selectedLocation = locationPickerViewModel.selectedLocation

            Box(modifier = Modifier.fillMaxSize()) {
                //uma vez que a posicao da camara n pode comecar null, a google maps recomenda
                //fazermos assim para gerir a camara
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(38.7, -9.1), 5f)
                }
                //é aqui que vai aparecer o mapa
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    //ao clicarmos no mapa vai chamar a funcao do viewmodel para dar update as
                    //latitudes e longitudes
                    onMapClick = { latLng ->
                        locationPickerViewModel.updateSelectedLocation(latLng)
                    }
                ) {
                    //se selecionarmos alguma coisa aparece um titulo a dizer
                    selectedLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Localização selecionada"
                        )
                    }
                }
                // uma vez que selecionamos uma localizacao aparece o botao
                selectedLocation?.let {
                    Button(
                        onClick = {
                            val resultIntent = Intent()
                            resultIntent.putExtra("latitude", it.latitude.toFloat())
                            resultIntent.putExtra("longitude", it.longitude.toFloat())
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C4DFF)
                        )
                    ) {
                        Text("Confirm Location")
                    }
                }
            }
        }
    }
}