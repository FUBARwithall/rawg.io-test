package com.cancreative.rawggames.ui.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.unit.dp
import com.cancreative.rawggames.ui.theme.DarkSurface
import com.cancreative.rawggames.ui.theme.PrimaryPurple
import com.cancreative.rawggames.ui.theme.TextSecondary
import com.cancreative.rawggames.ui.viewmodel.GameViewModel

// Start of MainScreen

sealed class Screen(val route: String, val title: String) {
    object GamesList : Screen("games_list", "Home")
    object Favorites : Screen("favorites", "Favorites")
    object Detail : Screen("detail/{gameId}", "Detail") {
        fun createRoute(gameId: Int) = "detail/$gameId"
    }
}

@Composable
fun MainScreen(viewModel: GameViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute == Screen.GamesList.route || currentRoute == Screen.Favorites.route) {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.GamesList.route,
                        onClick = {
                            if (currentRoute != Screen.GamesList.route) {
                                navController.navigate(Screen.GamesList.route) {
                                    popUpTo(Screen.GamesList.route) { inclusive = true }
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = TextSecondary,
                            selectedTextColor = PrimaryPurple,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PrimaryPurple.copy(alpha = 0.4f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Favorites.route,
                        onClick = {
                            if (currentRoute != Screen.Favorites.route) {
                                navController.navigate(Screen.Favorites.route) {
                                    popUpTo(Screen.GamesList.route)
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                        label = { Text("Favorites") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = TextSecondary,
                            selectedTextColor = PrimaryPurple,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PrimaryPurple.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.GamesList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.GamesList.route) {
                GamesListScreen(
                    viewModel = viewModel,
                    onGameClick = { gameId ->
                        navController.navigate(Screen.Detail.createRoute(gameId))
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onGameClick = { gameId ->
                        navController.navigate(Screen.Detail.createRoute(gameId))
                    }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("gameId") { type = NavType.IntType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getInt("gameId") ?: 0
                DetailGameScreen(
                    gameId = gameId,
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

// End of MainScreen
