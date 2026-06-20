package com.cancreative.rawggames.ui.viewmodel

import com.cancreative.rawggames.data.remote.GameDetailResponse
import com.cancreative.rawggames.data.remote.GameResponse
import com.cancreative.rawggames.data.remote.GamesResponse
import com.cancreative.rawggames.data.repository.GameRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// Start of GameViewModelTest

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: GameRepository
    private lateinit var viewModel: GameViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        
        // Mock default favorites flow
        every { repository.getAllFavorites() } returns flowOf(emptyList())
        
        viewModel = GameViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchGames_updatesStateToSuccess_whenApiSucceeds() {
        val dummyGames = listOf(
            GameResponse(id = 1, name = "Game 1", released = "2024", backgroundImage = null, rating = 4.5, metacritic = 90, playtime = 10)
        )
        val dummyResponse = GamesResponse(count = 1, next = null, previous = null, results = dummyGames)
        
        coEvery { repository.getGames(any(), any(), any()) } returns dummyResponse

        viewModel.fetchGames(reset = true)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.gamesState.value
        assert(state is GamesListState.Success)
        assertEquals(dummyGames, (state as GamesListState.Success).games)
    }

    @Test
    fun fetchGameDetail_updatesDetailStateToSuccess_whenApiSucceeds() {
        val dummyDetail = GameDetailResponse(
            id = 1,
            name = "Game 1",
            descriptionRaw = "Cool game",
            description = null,
            released = "2024",
            backgroundImage = null,
            backgroundImageAdditional = null,
            rating = 4.5,
            metacritic = 90,
            playtime = 10,
            developers = null,
            publishers = null
        )
        
        coEvery { repository.getGameDetail(1, any()) } returns dummyDetail

        viewModel.fetchGameDetail(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.detailState.value
        assert(state is GameDetailState.Success)
        assertEquals(dummyDetail, (state as GameDetailState.Success).game)
    }
}

// End of GameViewModelTest
