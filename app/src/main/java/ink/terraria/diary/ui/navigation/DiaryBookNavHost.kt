package ink.terraria.diary.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        composable(
            route = HomeDestination.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            HomeScreen(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 8.dp),
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
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }) {
            DiaryDetailScreen(
                modifier = Modifier.fillMaxSize(),
                navigationBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
