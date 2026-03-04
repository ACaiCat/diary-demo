package ink.terraria.diary.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ink.terraria.diary.DiaryBookAppBar
import ink.terraria.diary.R
import ink.terraria.diary.data.Diary
import ink.terraria.diary.data.diaries
import ink.terraria.diary.toLocalString
import ink.terraria.diary.ui.AppViewModelProvider
import ink.terraria.diary.ui.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route: String = "home"
    override val titleRes: Int = R.string.app_name
}

@Composable
fun HomeScreen(
    navigateToDiaryDetail: (diaryId: Int) -> Unit,
    navigateToNewDiary: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState by viewModel.homeUiState.collectAsState()
    Scaffold(
        topBar = {
            DiaryBookAppBar(stringResource(HomeDestination.titleRes))
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = navigateToNewDiary,
                modifier = Modifier.padding(bottom = 32.dp, end = 8.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_diary))
                Text(
                    text = stringResource(R.string.add_diary),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = Modifier
    ) { paddingValues ->
        HomeBody(
            diaries = uiState.diaries,
            onDiaryClicked = navigateToDiaryDetail,
            isLoading = uiState.loading,
            modifier = modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                )
                .fillMaxWidth()
        )
    }
}

@Composable
fun HomeBody(
    diaries: List<Diary>,
    isLoading: Boolean,
    onDiaryClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (diaries.isEmpty() && !isLoading) {
            Spacer(Modifier.padding(top = 32.dp))
            Text(
                text =
                    stringResource(R.string.empty_diary_book),
                style = MaterialTheme.typography.displayMedium
            )

        } else {
            DiaryList(
                diaries = diaries,
                onDiaryClicked = onDiaryClicked
            )
        }

        Spacer(Modifier.padding(vertical = 16.dp))
    }


}

@Composable
fun DiaryList(
    diaries: List<Diary>,
    onDiaryClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(diaries) { diary ->
            Diary(
                diary = diary,
                onDiaryClicked = onDiaryClicked,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

}

@Composable
fun Diary(
    diary: Diary,
    onDiaryClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                onClick = { onDiaryClicked(diary.id) }
            )) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            var contentPreview = diary.content
            if (diary.content.length > 50) {
                contentPreview = diary.content.take(50) + "..."
            }


            Text(
                text = diary.title,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = diary.date.toLocalString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = contentPreview,
                style = MaterialTheme.typography.bodyMedium
            )


        }
    }

}

@Preview
@Composable
fun DiaryBodyReview() {
    HomeBody(diaries = diaries, isLoading = false, onDiaryClicked = {})

}
