package com.example.footballscout.data.repository

import com.example.footballscout.data.api.TheSportsDbApi
import com.example.footballscout.data.api.model.PlayerDto
import com.example.footballscout.data.database.dao.PlayerDao
import com.example.footballscout.data.database.entity.PlayerEntity
import com.example.footballscout.data.model.Player
import com.example.footballscout.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PlayerRepository(
    private val api: TheSportsDbApi,
    private val dao: PlayerDao
) {

    fun searchPlayers(query: String): Flow<Resource<List<Player>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.searchPlayers(query)
            if (response.isSuccessful) {
                val data = response.body()?.players?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error("Error finding players: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    fun getPlayerById(playerId: String): Flow<Resource<Player>> = flow {
        emit(Resource.Loading())
        try {
            // First check local DB
            val localPlayer = dao.getPlayerById(playerId)
            
            // Fetch former teams dynamically
            var formerTeamsList = emptyList<com.example.footballscout.data.api.model.FormerTeamDto>()
            try {
                val formerTeamsRes = api.lookupFormerTeams(playerId)
                if (formerTeamsRes.isSuccessful) {
                    formerTeamsList = formerTeamsRes.body()?.formerTeams ?: emptyList()
                }
            } catch (ignored: Exception) {}

            if (localPlayer != null) {
                emit(Resource.Success(localPlayer.toDomain().copy(formerTeams = formerTeamsList)))
            } else {
                // Fetch from API
                val response = api.lookupPlayer(playerId)
                if (response.isSuccessful) {
                    val playerDto = response.body()?.players?.firstOrNull()
                    if (playerDto != null) {
                        emit(Resource.Success(playerDto.toDomain(formerTeamsList)))
                    } else {
                        emit(Resource.Error("Player not found"))
                    }
                } else {
                    emit(Resource.Error("Error finding player: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    fun getFavoritePlayers(): Flow<List<Player>> {
        return dao.getFavoritePlayers().map { list -> list.map { it.toDomain() } }.flowOn(Dispatchers.IO)
    }
    
    fun isPlayerFavorite(playerId: String): Flow<Boolean> = dao.isPlayerFavorite(playerId).flowOn(Dispatchers.IO)

    suspend fun addPlayerToFavorites(player: Player) {
        dao.insertPlayer(player.toEntity())
    }

    suspend fun removePlayerFromFavorites(player: Player) {
        dao.deletePlayer(player.toEntity())
    }

    // Mappers
    private fun PlayerDto.toDomain(formerTeamsList: List<com.example.footballscout.data.api.model.FormerTeamDto> = emptyList()): Player {
        val desc = strDescriptionEN ?: ""
        
        var goalsText = ""
        // Fix for TheSportsDB API Free Tier always returning Mario Balotelli's description for every player lookup
        val isBalotelliDescription = desc.contains("Balotelli", ignoreCase = true)
        val isNotBalotelli = (strPlayer?.contains("Balotelli", ignoreCase = true) == false)
        
        if (isBalotelliDescription && isNotBalotelli) {
            goalsText = "Career goals restricted by SportsDB API Free Tier."
        } else {
            // Goals extraction: Regex looking for goals scored
            val goalsRegex = Regex("(?i)scored \\d+ (?:career )?goals.*?\\.")
            var goalsMatch = goalsRegex.find(desc)
            if (goalsMatch == null) {
                 val secondaryGoalsRegex = Regex("(?i)\\d+ goals.*?in.*?appearances")
                 goalsMatch = secondaryGoalsRegex.find(desc)
            }
            goalsText = goalsMatch?.value ?: "Career goals information not explicitly stated."
        }

        return Player(
            id = idPlayer,
            name = strPlayer ?: "",
            nationality = strNationality ?: "",
            dateOfBirth = dateBorn ?: "",
            height = strHeight ?: "",
            weight = strWeight ?: "",
            team = strTeam ?: "",
            photoUrl = strCutout ?: strThumb ?: "",
            careerDescription = desc,
            totalGoals = goalsText,
            formerTeams = formerTeamsList.sortedByDescending { it.strJoined?.toIntOrNull() ?: 0 }
        )
    }

    private fun PlayerEntity.toDomain(): Player {
        return Player(
            id = id,
            name = name,
            nationality = nationality ?: "",
            dateOfBirth = dateOfBirth ?: "",
            height = height ?: "",
            weight = weight ?: "",
            team = team ?: "",
            photoUrl = photoUrl ?: "",
            careerDescription = careerDescription ?: "",
            totalGoals = totalGoals ?: "Career goals information not explicitly stated.",
            isFavorite = true
        )
    }

    private fun Player.toEntity(): PlayerEntity {
        return PlayerEntity(
            id = id,
            name = name,
            nationality = nationality,
            dateOfBirth = dateOfBirth,
            height = height,
            weight = weight,
            team = team,
            photoUrl = photoUrl,
            careerDescription = careerDescription,
            totalGoals = totalGoals
        )
    }
}
