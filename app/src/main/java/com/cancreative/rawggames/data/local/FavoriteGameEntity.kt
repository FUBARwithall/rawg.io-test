package com.cancreative.rawggames.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Start of FavoriteGameEntity

@Entity(tableName = "favorite_games")
data class FavoriteGameEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val backgroundImage: String?,
    val rating: Double,
    val released: String?
)

// End of FavoriteGameEntity
