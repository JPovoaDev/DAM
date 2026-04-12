package com.example.footballscout.data.api

import com.example.footballscout.data.api.model.MatchResponse
import com.example.footballscout.data.api.model.PlayerLookupResponse
import com.example.footballscout.data.api.model.PlayerResponse
import com.example.footballscout.data.api.model.TeamResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TheSportsDbApi {

    @GET("searchplayers.php")
    suspend fun searchPlayers(@Query("p") player: String): Response<PlayerResponse>

    @GET("lookupplayer.php")
    suspend fun lookupPlayer(@Query("id") id: String): Response<PlayerLookupResponse>

    @GET("lookupformerteams.php")
    suspend fun lookupFormerTeams(@Query("id") id: String): Response<com.example.footballscout.data.api.model.FormerTeamResponse>

    @GET("searchteams.php")
    suspend fun searchTeams(@Query("t") team: String): Response<TeamResponse>

    @GET("lookupteam.php")
    suspend fun lookupTeam(@Query("id") id: String): Response<TeamResponse>

    @GET("search_all_teams.php")
    suspend fun getAllTeams(@Query("l") league: String): Response<TeamResponse>

    @GET("eventsnext.php")
    suspend fun getNextMatches(@Query("id") teamId: String): Response<MatchResponse>

    @GET("eventslast.php")
    suspend fun getPreviousMatches(@Query("id") teamId: String): Response<MatchResponse>

    @GET("eventsround.php")
    suspend fun getMatchesByRound(@Query("id") leagueId: String, @Query("r") round: String, @Query("s") season: String): Response<MatchResponse>

    @GET("lookuptable.php")
    suspend fun getLeagueStandings(@Query("l") leagueId: String, @Query("s") season: String): Response<com.example.footballscout.data.api.model.StandingResponse>
}
