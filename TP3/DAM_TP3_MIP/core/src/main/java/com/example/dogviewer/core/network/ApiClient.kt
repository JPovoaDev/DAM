package com.example.dogviewer.core.network

import com.example.dogviewer.core.repository.DogRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple Singleton client maintaining the Retrofit instance AND the shared Repository.
 */
object ApiClient {
    private const val BASE_URL = "https://dog.ceo/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val dogApiService: DogApiService by lazy {
        retrofit.create(DogApiService::class.java)
    }

    // Shared repository instance to ensure Favorites and Cache state are globally synced
    val dogRepository: DogRepository by lazy {
        DogRepository(dogApiService)
    }
}
