package com.example.footballscout.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.footballscout.data.database.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM favorite_players")
    fun getFavoritePlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM favorite_players WHERE id = :playerId")
    suspend fun getPlayerById(playerId: String): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_players WHERE id = :playerId)")
    fun isPlayerFavorite(playerId: String): Flow<Boolean>
}
