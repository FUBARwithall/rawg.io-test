package com.cancreative.rawggames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.cancreative.rawggames.data.local.GameDatabase
import com.cancreative.rawggames.data.remote.RawgApiService
import com.cancreative.rawggames.data.repository.GameRepository
import com.cancreative.rawggames.ui.theme.RawgGamesTheme
import com.cancreative.rawggames.ui.view.MainScreen
import com.cancreative.rawggames.ui.viewmodel.GameViewModel
import com.cancreative.rawggames.ui.viewmodel.GameViewModelFactory

// Start of MainActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Local DB, Network API, and Repository
        val database = GameDatabase.getDatabase(applicationContext)
        val apiService = RawgApiService.create()
        val repository = GameRepository(apiService, database.favoriteGameDao())

        // Initialize ViewModel using the Factory
        val viewModel = ViewModelProvider(
            this,
            GameViewModelFactory(repository)
        )[GameViewModel::class.java]

        setContent {
            RawgGamesTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

// End of MainActivity
