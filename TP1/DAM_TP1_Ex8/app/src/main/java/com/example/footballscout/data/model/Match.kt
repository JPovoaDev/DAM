package com.example.footballscout.data.model

data class Match(
    val id: String,
    val opponentName: String,
    val competition: String,
    val date: String,
    val homeScore: String = "",
    val awayScore: String = ""
)
