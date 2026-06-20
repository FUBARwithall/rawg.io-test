package com.cancreative.rawggames.ui.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cancreative.rawggames.data.remote.GameResponse
import com.cancreative.rawggames.ui.theme.RawgGamesTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Start of GameCardTest

class GameCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gameCard_displaysCorrectNameAndRating() {
        val game = GameResponse(
            id = 123,
            name = "Test Super Game",
            released = "2024-01-01",
            backgroundImage = null,
            rating = 4.8,
            metacritic = 95,
            playtime = 12
        )

        var clicked = false

        composeTestRule.setContent {
            RawgGamesTheme {
                GameCard(
                    game = game,
                    isFavorite = false,
                    onClick = { clicked = true },
                    onFavoriteClick = {}
                )
            }
        }

        // Verify name is displayed
        composeTestRule.onNodeWithText("Test Super Game").assertExists()
        // Verify rating is displayed
        composeTestRule.onNodeWithText("4.8").assertExists()

        // Verify click action triggers callback
        composeTestRule.onNodeWithText("Test Super Game").performClick()
        assertTrue(clicked)
    }
}

// End of GameCardTest
