package com.example.footballscout.data.api.model

import com.google.gson.annotations.SerializedName

data class PlayerResponse(
    @SerializedName("player") val players: List<PlayerDto>?
)

data class PlayerLookupResponse(
    @SerializedName("players") val players: List<PlayerDto>?
)

data class PlayerDto(
    @SerializedName("idPlayer") val idPlayer: String,
    @SerializedName("strPlayer") val strPlayer: String?,
    @SerializedName("strNationality") val strNationality: String?,
    @SerializedName("dateBorn") val dateBorn: String?,
    @SerializedName("strHeight") val strHeight: String?,
    @SerializedName("strWeight") val strWeight: String?,
    @SerializedName("strTeam") val strTeam: String?,
    @SerializedName("strThumb") val strThumb: String?,
    @SerializedName("strCutout") val strCutout: String?,
    @SerializedName("strDescriptionEN") val strDescriptionEN: String?
)
