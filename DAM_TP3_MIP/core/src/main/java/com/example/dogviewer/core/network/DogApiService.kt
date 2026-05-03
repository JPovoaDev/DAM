package com.example.dogviewer.core.network

import retrofit2.http.GET

/**
 * Retrofit interface exclusively defining the authorized Dog API requests.
 */
interface DogApiService {
    
    /**
     * Executes a GET request to the `/breeds/image/random` endpoint.
     * Returns a [DogApiResponse] containing the raw dog image URL.
     */
    @GET("api/breeds/image/random")
    suspend fun getRandomDogImage(): DogApiResponse
}
