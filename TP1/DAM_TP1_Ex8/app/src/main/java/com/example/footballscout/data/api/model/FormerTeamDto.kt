package com.example.footballscout.data.api.model

import com.google.gson.annotations.SerializedName

data class FormerTeamResponse(
    @SerializedName("formerteams") val formerTeams: List<FormerTeamDto>?
)

data class FormerTeamDto(
    @SerializedName("id") val id: String,
    @SerializedName("idPlayer") val idPlayer: String,
    @SerializedName("idFormerTeam") val idFormerTeam: String,
    @SerializedName("strSport") val strSport: String?,
    @SerializedName("strPlayer") val strPlayer: String?,
    @SerializedName("strFormerTeam") val strFormerTeam: String?,
    @SerializedName("strMoveType") val strMoveType: String?,
    @SerializedName("strBadge") val strBadge: String?,
    @SerializedName("strJoined") val strJoined: String?,
    @SerializedName("strDeparted") val strDeparted: String?
)
