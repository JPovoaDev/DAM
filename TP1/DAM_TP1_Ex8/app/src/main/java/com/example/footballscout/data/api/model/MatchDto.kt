package com.example.footballscout.data.api.model

import com.google.gson.annotations.SerializedName

data class MatchResponse(
    @SerializedName("events") val events: List<MatchDto>?,
    @SerializedName("results") val results: List<MatchDto>?
)

data class MatchDto(
    @SerializedName("idEvent") val idEvent: String,
    @SerializedName("strEvent") val strEvent: String?, // Shows "Team A vs Team B"
    @SerializedName("strLeague") val strLeague: String?,
    @SerializedName("dateEvent") val dateEvent: String?,
    @SerializedName("intHomeScore") val intHomeScore: String?,
    @SerializedName("intAwayScore") val intAwayScore: String?,
    @SerializedName("idHomeTeam") val idHomeTeam: String?,
    @SerializedName("strHomeTeam") val strHomeTeam: String?,
    @SerializedName("idAwayTeam") val idAwayTeam: String?,
    @SerializedName("strAwayTeam") val strAwayTeam: String?
)
