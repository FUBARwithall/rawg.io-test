package com.cancreative.rawggames.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Start of FavoriteGameDao

@Dao
interface FavoriteGameDao {

    @Query("SELECT * FROM favorite_games")
    fun getAllFavorites(): Flow<List<FavoriteGameEntity>>

    @Query("SELECT EXISTS(SELECT * FROM favorite_games WHERE id = :id)")
    fun isFavorite(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(game: FavoriteGameEntity)

    @Delete
    suspend fun deleteFavorite(game: FavoriteGameEntity)

    @Query("DELETE FROM favorite_games WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)
}

// End of FavoriteGameDao
