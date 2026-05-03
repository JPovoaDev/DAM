package com.example.dogviewer.core.repository

import com.example.dogviewer.core.model.ImageItem
import com.example.dogviewer.core.network.DogApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

/**
 * Repository serving as the Single Source of Truth for dog images.
 */
class DogRepository(private val apiService: DogApiService) {

    // In-memory FIFO queue capping at 5 items
    private val _favorites = mutableListOf<ImageItem>()

    // In-memory Cache queue capping at 50 items exclusively for active fetching
    private val _cache = mutableListOf<ImageItem>()

    /**
     * Fetches a single random dog image from the API and converts it into the domain model.
     * Guaranteed to execute on the IO thread to avoid blocking the main UI.
     */
    suspend fun fetchRandomDogImage(): ImageItem {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Call API service
                val response = apiService.getRandomDogImage()
                val url = response.message

                // 2. Extract title (breed) from the URL
                // Example: https://images.dog.ceo/breeds/poodle-toy/n02113204_1.jpg -> Poodle-toy
                val segments = url.split("/")
                val breedsIndex = segments.indexOf("breeds")
                
                var title = "Unknown Breed"
                var breed = "Unknown"
                var subBreed: String? = null

                if (breedsIndex != -1 && breedsIndex + 1 < segments.size) {
                    val breedSegment = segments[breedsIndex + 1]
                    title = breedSegment.replaceFirstChar { it.uppercase() }
                    
                    val parts = breedSegment.split("-")
                    breed = parts[0].replaceFirstChar { it.uppercase() }
                    if (parts.size > 1) {
                        subBreed = parts[1].replaceFirstChar { it.uppercase() }
                    }
                }
                
                // 3. Convert response into ImageItem
                val newItem = ImageItem(
                    url = url, 
                    title = title,
                    breed = breed,
                    subBreed = subBreed
                )
 
                // 3. Keep cache below max 50 items (Thread-safe access)
                synchronized(this@DogRepository) {
                    _cache.add(newItem)
                    if (_cache.size > 50) {
                        _cache.removeAt(0)
                    }
                }
 
                newItem
            } catch (e: Exception) {
                // Protect coroutine structured concurrency
                if (e is CancellationException) throw e
                
                // Offline Mode Fallback: Safe read-only access to lists
                synchronized(this@DogRepository) {
                    when {
                        _cache.isNotEmpty() -> _cache.random()
                        _favorites.isNotEmpty() -> _favorites.random()
                        else -> throw e
                    }
                }
            }
        }
    }
 
    /**
     * Adds an item to favorites. Follows strict FIFO with a maximum of 5 items.
     */
    fun addFavorite(item: ImageItem) {
        synchronized(this) {
            if (_favorites.none { it.url == item.url }) {
                _favorites.add(item)
                if (_favorites.size > 5) {
                    _favorites.removeAt(0)
                }
            }
        }
    }
 
    /**
     * Removes a specific item from the favorites list.
     */
    fun removeFavorite(item: ImageItem) {
        synchronized(this) {
            _favorites.removeAll { it.url == item.url }
        }
    }
 
    /**
     * Returns a thread-safe immutable copy of the current favorites list.
     */
    fun getFavorites(): List<ImageItem> = synchronized(this) { 
        _favorites.toList() 
    }
}
