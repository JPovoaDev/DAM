package com.example.footballscout.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_teams")
data class TeamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val logoUrl: String?,
    val league: String?,
    val stadium: String?,
    val description: String?,
    val titles: String?
)
