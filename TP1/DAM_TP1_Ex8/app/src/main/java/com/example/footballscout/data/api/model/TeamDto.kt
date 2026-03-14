package com.example.footballscout.data.api.model

import com.google.gson.annotations.SerializedName

data class TeamResponse(
    @SerializedName("teams") val teams: List<TeamDto>?
)

data class TeamDto(
    @SerializedName("idTeam") val idTeam: String,
    @SerializedName("strTeam") val strTeam: String?,
    @SerializedName("strBadge") val strBadge: String?,
    @SerializedName("strLeague") val strLeague: String?,
    @SerializedName("strStadium") val strStadium: String?,
    @SerializedName("strDescriptionEN") val strDescription: String?
)
