package com.example.dogviewer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogviewer.model.ImageItem
import com.example.dogviewer.repository.DogRepository
import kotlinx.coroutines.launch

/**
 * ViewModel orchestrating the UI state according to the MVVM architecture.
 * It manages logic exclusively, keeping Android UI dependencies entirely decoupled.
 */
class DogViewModel(private val repository: DogRepository) : ViewModel() {

    // Internal mutable state holding the list of images
    private val _images = MutableLiveData<List<ImageItem>>(emptyList())
    // Public immutable LiveData exposed to the UI
    val images: LiveData<List<ImageItem>> get() = _images

    // State capturing if a network request is currently active
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Event stream containing strictly error strings to bubble up gracefully
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    /**
     * Commands the repository to fetch a new image via the Dog API.
     * Handles the loading state automatically and appends the new dog to the list upon success.
     */
    fun fetchNewDogImage() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // reset string stream
            try {
                // Fetch new image on background thread (via repository)
                val newImage = repository.fetchRandomDogImage()
                
                // Read current list and append the new image
                val currentList = _images.value ?: emptyList()
                _images.value = currentList + newImage
            } catch (e: Exception) {
                // API Error Handling catching terminal repository crashes (Step 18)
                _errorMessage.value = "Network/API Error: ${e.localizedMessage ?: "Unknown Issue"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
