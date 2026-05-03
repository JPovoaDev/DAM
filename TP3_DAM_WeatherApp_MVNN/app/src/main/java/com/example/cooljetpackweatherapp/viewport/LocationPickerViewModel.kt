package com.example.cooljetpackweatherapp.viewport

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationPickerViewModel : ViewModel() {
    //Criamos mais um viewModel pois o locationPicker vai ser uma atividade em separado e o android estudios
    // n deixa o mesmo viewmodel dar handel de duas atividas diferentes

    //variavel para guardar a localizacao selecionada
    var selectedLocation by mutableStateOf<LatLng?>(null)
        private set

    fun updateSelectedLocation(latLng: LatLng) {
        selectedLocation = latLng
    }
}