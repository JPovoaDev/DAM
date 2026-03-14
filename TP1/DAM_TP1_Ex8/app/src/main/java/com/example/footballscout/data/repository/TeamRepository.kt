package com.example.footballscout.data.repository

import com.example.footballscout.data.api.MockStandingsApi
import com.example.footballscout.data.api.TheSportsDbApi
import com.example.footballscout.data.api.model.MatchDto
import com.example.footballscout.data.api.model.StandingDto
import com.example.footballscout.data.api.model.TeamDto
import com.example.footballscout.data.database.dao.TeamDao
import com.example.footballscout.data.database.entity.TeamEntity
import com.example.footballscout.data.model.Match
import com.example.footballscout.data.model.Team
import com.example.footballscout.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TeamRepository(
    private val api: TheSportsDbApi,
    private val standingsApi: MockStandingsApi,
    private val dao: TeamDao
) {

    private val fetchedTeamsCache = mutableMapOf<String, Team>()
    private val nextMatchesCache = mutableMapOf<String, List<Match>>()

    fun searchTeams(query: String): Flow<Resource<List<Team>>> = flow {
        emit(Resource.Loading())
        try {
            // First check if we can filter locally from cached globally loaded teams
            if (cachedDefaultTeams != null) {
                val filtered = cachedDefaultTeams!!.filter { it.name.contains(query, ignoreCase = true) }
                if (filtered.isNotEmpty()) {
                    emit(Resource.Success(filtered))
                    return@flow
                }
            }
            
            // Fallback to API search if not found locally
            val response = api.searchTeams(query)
            if (response.isSuccessful) {
                val data = response.body()?.teams?.map { it.toDomain() } ?: emptyList()
                android.util.Log.d("TeamRepository", "Mapped ${data.size} teams from API response")
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error("Error finding teams: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    private var cachedDefaultTeams: List<Team>? = null

    fun getDefaultTeams(): Flow<Resource<List<Team>>> = flow {
        if (cachedDefaultTeams != null) {
            emit(Resource.Success(cachedDefaultTeams!!))
            return@flow
        }
        
        emit(Resource.Loading())
        try {
            val teamsList = mutableListOf<Team>()
            coroutineScope {
                val leagues = listOf("English Premier League", "Spanish La Liga", "Italian Serie A", "German Bundesliga", "French Ligue 1", "Portuguese Primeira Liga")
                val deferreds = leagues.map { league ->
                    async { api.getAllTeams(league) }
                }
                val responses = deferreds.awaitAll()
                responses.forEach { response ->
                    if (response.isSuccessful) {
                        teamsList.addAll(response.body()?.teams?.map { it.toDomain() } ?: emptyList())
                    }
                }
            }
            if (teamsList.isNotEmpty()) {
                val finalTeams = teamsList.toList()
                cachedDefaultTeams = finalTeams
                android.util.Log.d("TeamRepository", "Mapped ${finalTeams.size} default teams")
                emit(Resource.Success(finalTeams))
            } else {
                emit(Resource.Error("Error fetching default teams"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    fun getTeamById(teamId: String): Flow<Resource<Team>> = flow {
        emit(Resource.Loading())
        try {
            val localTeam = dao.getTeamById(teamId)
            if (localTeam != null) {
                emit(Resource.Success(localTeam.toDomain()))
            } else if (fetchedTeamsCache.containsKey(teamId)) {
                emit(Resource.Success(fetchedTeamsCache[teamId]!!))
            } else {
                val response = api.lookupTeam(teamId)
                if (response.isSuccessful) {
                    val teamDto = response.body()?.teams?.firstOrNull()
                    if (teamDto != null) {
                        emit(Resource.Success(teamDto.toDomain()))
                    } else {
                        emit(Resource.Error("Team not found"))
                    }
                } else {
                    emit(Resource.Error("Error finding team: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    fun getNextMatches(teamId: String): Flow<Resource<List<Match>>> = flow {
        emit(Resource.Loading())
        try {
            // Return cached result immediately if already fetched this session.
            // This is critical: the free-tier API throttles after ~15-20 rapid calls,
            // and the round-loop below can fire up to 13 requests per visit.
            val cached = nextMatchesCache[teamId]
            if (cached != null) {
                emit(Resource.Success(cached))
                return@flow
            }

            val team = fetchedTeamsCache[teamId]
            if (team != null) {
                val leagueId = getLeagueIdByName(team.league)
                val matchesList = mutableListOf<Match>()
                
                // Fetch upcoming rounds. Stop as soon as we have 5 matches or
                // hit two consecutive empty rounds (league season ended).
                var emptyRounds = 0
                for (round in 26..38) {
                    val response = api.getMatchesByRound(leagueId, round.toString(), "2025-2026")
                    if (response.isSuccessful) {
                        val events = response.body()?.events
                        if (events.isNullOrEmpty()) {
                            emptyRounds++
                            if (emptyRounds >= 2) break // Season likely over for this league
                            continue
                        }
                        emptyRounds = 0
                        val teamMatches = events.filter { 
                            (it.idHomeTeam == teamId || it.idAwayTeam == teamId) && it.intHomeScore == null 
                        }
                        matchesList.addAll(teamMatches.map { it.toDomain(teamId) })
                        if (matchesList.size >= 5) break
                    } else {
                        break // API error — stop looping to avoid wasting requests
                    }
                }
                
                val result = matchesList.take(5)
                nextMatchesCache[teamId] = result // Cache so we don't hammer the API again
                emit(Resource.Success(result))
            } else {
                emit(Resource.Success(emptyList()))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Local error: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    fun getPreviousMatches(teamId: String): Flow<Resource<List<Match>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getPreviousMatches(teamId)
            if (response.isSuccessful) {
                val data = response.body()?.results?.map { it.toDomain(teamId) } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error("Error finding matches: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    private fun getLeagueIdByName(league: String): String {
        return when {
            league.contains("Premier League", ignoreCase = true) -> "4328"
            league.contains("La Liga", ignoreCase = true) -> "4335"
            league.contains("Serie A", ignoreCase = true) -> "4332"
            league.contains("Bundesliga", ignoreCase = true) -> "4331"
            league.contains("Ligue 1", ignoreCase = true) -> "4334"
            league.contains("Primeira Liga", ignoreCase = true) || league.contains("Portugal", ignoreCase = true) -> "4344"
            else -> "4328"
        }
    }

    fun getLeagueStandings(league: String): Flow<Resource<List<StandingDto>>> = flow {
        emit(Resource.Loading())
        try {
            val leagueId = getLeagueIdByName(league)
            val response = api.getLeagueStandings(leagueId, "2025-2026")
            if (response.isSuccessful) {
                val data = response.body()?.table?.map { 
                    StandingDto(
                        position = it.intRank?.toIntOrNull() ?: 0,
                        teamName = it.strTeam ?: "Unknown",
                        points = it.intPoints?.toIntOrNull() ?: 0,
                        playedMatches = it.intPlayed?.toIntOrNull() ?: 0,
                        won = it.intWin?.toIntOrNull() ?: 0,
                        draw = it.intDraw?.toIntOrNull() ?: 0,
                        lost = it.intLoss?.toIntOrNull() ?: 0
                    )
                } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error("Error finding standings: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error loading standings: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    fun getFavoriteTeams(): Flow<List<Team>> {
        return dao.getFavoriteTeams().map { list -> list.map { it.toDomain() } }.flowOn(Dispatchers.IO)
    }
    
    fun isTeamFavorite(teamId: String): Flow<Boolean> = dao.isTeamFavorite(teamId).flowOn(Dispatchers.IO)

    suspend fun addTeamToFavorites(team: Team) {
        dao.insertTeam(team.toEntity())
    }

    suspend fun removeTeamFromFavorites(team: Team) {
        dao.deleteTeam(team.toEntity())
    }

    // Mappers
    private fun TeamDto.toDomain(): Team {
        val desc = strDescription ?: ""
        // Titles extraction: simple Regex looking for won sentences
        val titlesRegex = Regex("(?i)(?:They )?have won .*?(?:titles|cups|trophies|championships|league).*?\\.")
        val match = titlesRegex.find(desc)
        val titlesText = match?.value ?: "No title information available."

        val team = Team(
            id = idTeam,
            name = strTeam ?: "",
            logoUrl = strBadge ?: "",
            league = strLeague ?: "",
            stadium = strStadium ?: "",
            description = desc,
            titles = titlesText
        )
        fetchedTeamsCache[team.id] = team
        return team
    }

    private fun TeamEntity.toDomain(): Team {
        val team = Team(
            id = id,
            name = name,
            logoUrl = logoUrl ?: "",
            league = league ?: "",
            stadium = stadium ?: "",
            description = description ?: "",
            titles = titles ?: "",
            isFavorite = true
        )
        // Ensure teams loaded from DB are also in the in-memory cache so
        // getNextMatches() can resolve their league when needed.
        fetchedTeamsCache[team.id] = team
        return team
    }

    private fun Team.toEntity(): TeamEntity {
        return TeamEntity(
            id = id,
            name = name,
            logoUrl = logoUrl,
            league = league,
            stadium = stadium,
            description = description,
            titles = titles
        )
    }

    private fun MatchDto.toDomain(requestTeamId: String): Match {
        // Determine opponent
        val opponent = if (idHomeTeam == requestTeamId) strAwayTeam else strHomeTeam
        return Match(
            id = idEvent,
            opponentName = opponent ?: strEvent ?: "Unknown",
            competition = strLeague ?: "",
            date = dateEvent ?: "",
            homeScore = intHomeScore ?: "",
            awayScore = intAwayScore ?: ""
        )
    }
}
