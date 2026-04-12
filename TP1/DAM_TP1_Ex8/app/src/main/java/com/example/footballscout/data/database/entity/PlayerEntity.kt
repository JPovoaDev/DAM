package com.example.footballscout.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nationality: String?,
    val dateOfBirth: String?,
    val height: String?,
    val weight: String?,
    val team: String?,
    val photoUrl: String?,
    val careerDescription: String?,
    val totalGoals: String?
)
