package com.cancreative.rawggames.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cancreative.rawggames.data.remote.GameDetailResponse
import com.cancreative.rawggames.ui.theme.DarkBackground
import com.cancreative.rawggames.ui.theme.DarkSurface
import com.cancreative.rawggames.ui.theme.PrimaryPurple
import com.cancreative.rawggames.ui.theme.TextPrimary
import com.cancreative.rawggames.ui.theme.TextSecondary
import com.cancreative.rawggames.ui.viewmodel.GameDetailState
import com.cancreative.rawggames.ui.viewmodel.GameViewModel

// Start of DetailGameScreen

@Composable
fun DetailGameScreen(
    gameId: Int,
    viewModel: GameViewModel,
    onBackClick: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()
    val favoriteGames by viewModel.favoriteGames.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.fetchGameDetail(gameId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        when (val state = detailState) {
            is GameDetailState.Idle, is GameDetailState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            }
            is GameDetailState.Error -> {
                ErrorView(message = state.message) {
                    viewModel.fetchGameDetail(gameId)
                }
            }
            is GameDetailState.Success -> {
                val isFavorite = favoriteGames.any { it.id == state.game.id }
                GameDetailContent(
                    game = state.game,
                    isFavorite = isFavorite,
                    onBackClick = onBackClick,
                    onFavoriteToggle = { viewModel.toggleFavoriteDetail(state.game) }
                )
            }
        }
    }
}

@Composable
fun GameDetailContent(
    game: GameDetailResponse,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Cover Image header
        Box {
            AsyncImage(
                model = game.backgroundImage,
                contentDescription = game.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
            )

            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Favorite button
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }

        // Details content
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = game.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rating & Metacritic
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${game.rating} / 5",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (game.metacritic != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = getMetacriticColor(game.metacritic),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Metascore: ${game.metacritic}",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Additional details info row
            DetailRow(label = "Released", value = game.released ?: "Unknown")
            DetailRow(label = "Playtime", value = if (game.playtime != null && game.playtime > 0) "${game.playtime} hrs" else "Unknown")
            DetailRow(label = "Developers", value = game.developers?.joinToString { it.name } ?: "Unknown")
            DetailRow(label = "Publishers", value = game.publishers?.joinToString { it.name } ?: "Unknown")

            Spacer(modifier = Modifier.height(24.dp))

            // Description
            Text(
                text = "About",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.descriptionRaw ?: game.description ?: "No description available.",
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

fun getMetacriticColor(score: Int): Color {
    return when {
        score >= 75 -> Color(0xFF66CC33) // Green
        score >= 50 -> Color(0xFFFFCC33) // Yellow
        else -> Color(0xFFFF3333)       // Red
    }
}

// End of DetailGameScreen
