package com.example.footballscout.data.api.model

import com.google.gson.annotations.SerializedName

data class StandingResponse(
    @SerializedName("table") val table: List<StandingApiDto>?
)

data class StandingApiDto(
    @SerializedName("intRank") val intRank: String?,
    @SerializedName("strTeam") val strTeam: String?,
    @SerializedName("intPoints") val intPoints: String?,
    @SerializedName("intPlayed") val intPlayed: String?,
    @SerializedName("intWin") val intWin: String?,
    @SerializedName("intDraw") val intDraw: String?,
    @SerializedName("intLoss") val intLoss: String?
)

data class StandingDto(
    val position: Int,
    val teamName: String,
    val points: Int,
    val playedMatches: Int,
    val won: Int,
    val draw: Int,
    val lost: Int
)
