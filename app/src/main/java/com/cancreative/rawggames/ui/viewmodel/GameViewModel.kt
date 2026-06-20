package com.cancreative.rawggames.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cancreative.rawggames.BuildConfig
import com.cancreative.rawggames.data.local.FavoriteGameEntity
import com.cancreative.rawggames.data.remote.GameDetailResponse
import com.cancreative.rawggames.data.remote.GameResponse
import com.cancreative.rawggames.data.repository.GameRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Start of State Definitions

sealed interface GamesListState {
    object Idle : GamesListState
    object Loading : GamesListState
    data class Success(val games: List<GameResponse>, val isPageLoading: Boolean = false) : GamesListState
    data class Error(val message: String) : GamesListState
}

sealed interface GameDetailState {
    object Idle : GameDetailState
    object Loading : GameDetailState
    data class Success(val game: GameDetailResponse) : GameDetailState
    data class Error(val message: String) : GameDetailState
}

// End of State Definitions

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    private val apiKey = BuildConfig.API_KEY

    // Games List Pagination state variables
    private var currentPage = 1
    private val pageSize = 20
    private var isLastPage = false
    private val loadedGames = mutableListOf<GameResponse>()

    private val _gamesState = MutableStateFlow<GamesListState>(GamesListState.Idle)
    val gamesState: StateFlow<GamesListState> = _gamesState.asStateFlow()

    // Search state variables
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchState = MutableStateFlow<GamesListState>(GamesListState.Idle)
    val searchState: StateFlow<GamesListState> = _searchState.asStateFlow()

    // Game Details state variables
    private val _detailState = MutableStateFlow<GameDetailState>(GameDetailState.Idle)
    val detailState: StateFlow<GameDetailState> = _detailState.asStateFlow()

    // Favorite Games state flow
    val favoriteGames: StateFlow<List<FavoriteGameEntity>> = repository.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchGames()
        observeSearch()
    }

    // Fetch initial list of games
    fun fetchGames(reset: Boolean = false) {
        if (reset) {
            currentPage = 1
            isLastPage = false
            loadedGames.clear()
            _gamesState.value = GamesListState.Loading
        } else if (_gamesState.value is GamesListState.Loading) {
            return
        }

        val currentState = _gamesState.value
        if (currentState is GamesListState.Success) {
            _gamesState.value = currentState.copy(isPageLoading = true)
        } else {
            _gamesState.value = GamesListState.Loading
        }

        viewModelScope.launch {
            try {
                val response = repository.getGames(currentPage, pageSize, apiKey)
                if (response.results.isEmpty()) {
                    isLastPage = true
                } else {
                    loadedGames.addAll(response.results)
                    currentPage++
                }
                _gamesState.value = GamesListState.Success(
                    games = loadedGames.toList(),
                    isPageLoading = false
                )
            } catch (e: Exception) {
                _gamesState.value = GamesListState.Error(e.localizedMessage ?: "Failed to load games")
            }
        }
    }

    // Live search query changes
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(GamesListState.Idle)
                    } else {
                        flowOf(GamesListState.Loading).apply {
                            performSearch(query)
                        }
                    }
                }
                .collect { state ->
                    if (state is GamesListState.Idle) {
                        _searchState.value = state
                    }
                }
        }
    }

    private fun performSearch(query: String) {
        _searchState.value = GamesListState.Loading
        viewModelScope.launch {
            try {
                val response = repository.searchGames(query, apiKey)
                _searchState.value = GamesListState.Success(response.results)
            } catch (e: Exception) {
                _searchState.value = GamesListState.Error(e.localizedMessage ?: "Search failed")
            }
        }
    }

    // Fetch Details for a single game
    fun fetchGameDetail(id: Int) {
        _detailState.value = GameDetailState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getGameDetail(id, apiKey)
                _detailState.value = GameDetailState.Success(response)
            } catch (e: Exception) {
                _detailState.value = GameDetailState.Error(e.localizedMessage ?: "Failed to load game details")
            }
        }
    }

    // Favorites DB operations
    fun toggleFavorite(game: GameResponse) {
        viewModelScope.launch {
            val isFav = favoriteGames.value.any { it.id == game.id }
            if (isFav) {
                repository.removeFavoriteById(game.id)
            } else {
                repository.addFavorite(
                    FavoriteGameEntity(
                        id = game.id,
                        name = game.name,
                        backgroundImage = game.backgroundImage,
                        rating = game.rating,
                        released = game.released
                    )
                )
            }
        }
    }

    fun toggleFavoriteDetail(game: GameDetailResponse) {
        viewModelScope.launch {
            val isFav = favoriteGames.value.any { it.id == game.id }
            if (isFav) {
                repository.removeFavoriteById(game.id)
            } else {
                repository.addFavorite(
                    FavoriteGameEntity(
                        id = game.id,
                        name = game.name,
                        backgroundImage = game.backgroundImage,
                        rating = game.rating,
                        released = game.released
                    )
                )
            }
        }
    }

    fun isGameFavorite(id: Int): Boolean {
        return favoriteGames.value.any { it.id == id }
    }
}

// Factory for GameViewModel injection
class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
