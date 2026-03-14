package com.example.footballscout.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.footballscout.data.database.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM favorite_teams")
    fun getFavoriteTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM favorite_teams WHERE id = :teamId")
    suspend fun getTeamById(teamId: String): TeamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity)

    @Delete
    suspend fun deleteTeam(team: TeamEntity)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_teams WHERE id = :teamId)")
    fun isTeamFavorite(teamId: String): Flow<Boolean>
}
