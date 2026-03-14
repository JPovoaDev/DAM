package com.example.footballscout.data.model

data class Player(
    val id: String,
    val name: String,
    val nationality: String = "",
    val dateOfBirth: String = "",
    val height: String = "",
    val weight: String = "",
    val team: String = "",
    val photoUrl: String = "",
    val careerDescription: String = "",
    val totalGoals: String = "",
    val formerTeams: List<com.example.footballscout.data.api.model.FormerTeamDto> = emptyList(),
    val isFavorite: Boolean = false
)
