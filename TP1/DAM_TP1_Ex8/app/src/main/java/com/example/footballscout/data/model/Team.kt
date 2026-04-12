package com.example.footballscout.data.model

data class Team(
    val id: String,
    val name: String,
    val logoUrl: String = "",
    val league: String = "",
    val stadium: String = "",
    val description: String = "",
    val titles: String = "",
    val isFavorite: Boolean = false
)
