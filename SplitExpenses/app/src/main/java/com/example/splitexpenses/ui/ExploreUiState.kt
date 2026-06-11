package com.example.splitexpenses.ui

data class Place(
    val id: String,
    val name: String,
    val location: String,
    val rating: Double,
    val tags: List<String>
)

data class ExploreUiState(
    val places: List<Place> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "Tudo",
    val searchQuery: String = "",
    val isLoading: Boolean = false
)
