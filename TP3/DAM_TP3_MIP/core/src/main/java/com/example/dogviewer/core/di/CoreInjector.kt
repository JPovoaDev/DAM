package com.example.dogviewer.core.di

import com.example.dogviewer.core.network.ApiClient
import com.example.dogviewer.core.viewmodel.DogViewModelFactory

/**
 * Service Locator for simple Dependency Injection.
 * Provides the necessary dependencies for the UI modules without exposing the underlying implementations.
 */
object CoreInjector {
    
    /**
     * Provides a factory to instantiate the DogViewModel.
     * Ensures that the ViewModel always receives the correct singleton repository.
     */
    fun provideDogViewModelFactory(): DogViewModelFactory {
        return DogViewModelFactory(ApiClient.dogRepository)
    }
}
