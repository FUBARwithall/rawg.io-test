package com.cancreative.rawggames.data.repository

import com.cancreative.rawggames.data.local.FavoriteGameDao
import com.cancreative.rawggames.data.local.FavoriteGameEntity
import com.cancreative.rawggames.data.remote.GameDetailResponse
import com.cancreative.rawggames.data.remote.GamesResponse
import com.cancreative.rawggames.data.remote.RawgApiService
import kotlinx.coroutines.flow.Flow

// Start of GameRepository

class GameRepository(
    private val apiService: RawgApiService,
    private val favoriteGameDao: FavoriteGameDao
) {

    // Remote API Operations
    suspend fun getGames(page: Int, pageSize: Int, apiKey: String): GamesResponse {
        return apiService.getGames(page, pageSize, apiKey)
    }

    suspend fun searchGames(query: String, apiKey: String): GamesResponse {
        return apiService.searchGames(query, apiKey)
    }

    suspend fun getGameDetail(id: Int, apiKey: String): GameDetailResponse {
        return apiService.getGameDetail(id, apiKey)
    }

    // Local DB Operations
    fun getAllFavorites(): Flow<List<FavoriteGameEntity>> {
        return favoriteGameDao.getAllFavorites()
    }

    fun isFavorite(id: Int): Flow<Boolean> {
        return favoriteGameDao.isFavorite(id)
    }

    suspend fun addFavorite(game: FavoriteGameEntity) {
        favoriteGameDao.insertFavorite(game)
    }

    suspend fun removeFavorite(game: FavoriteGameEntity) {
        favoriteGameDao.deleteFavorite(game)
    }

    suspend fun removeFavoriteById(id: Int) {
        favoriteGameDao.deleteFavoriteById(id)
    }
}

// End of GameRepository
