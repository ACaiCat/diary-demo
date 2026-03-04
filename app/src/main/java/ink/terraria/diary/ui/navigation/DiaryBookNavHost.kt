package ink.terraria.diary.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ink.terraria.diary.ui.detail.DiaryDetailBody
import ink.terraria.diary.ui.detail.DiaryDetailDestination
import ink.terraria.diary.ui.detail.DiaryDetailScreen
import ink.terraria.diary.ui.home.HomeDestination
import ink.terraria.diary.ui.home.HomeScreen

@Composable
fun DiaryBookNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
                navigateToNewDiary = {
                    navController.navigate("${DiaryDetailDestination.route}/0/true")
                },
                navigateToDiaryDetail = {
                    navController.navigate("${DiaryDetailDestination.route}/$it/false")
                }
            )
        }
        composable(
            route = DiaryDetailDestination.routeWithArgs,
            arguments = listOf(
                navArgument(DiaryDetailDestination.diaryIdArg) {
                    type = NavType.IntType
                },
                navArgument(DiaryDetailDestination.newDiaryArg) {
                    type = NavType.BoolType
                }
            )) {
            DiaryDetailScreen(
                modifier = Modifier.fillMaxSize(),
                navigationBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
