package ink.terraria.diary.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ink.terraria.diary.ui.home.HomeDestination
import ink.terraria.diary.ui.home.HomeScreen

@Composable
fun DiaryBookNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost (
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
