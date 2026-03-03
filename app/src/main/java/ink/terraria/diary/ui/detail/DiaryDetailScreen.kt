package ink.terraria.diary.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ink.terraria.diary.R
import ink.terraria.diary.data.AppContainer
import ink.terraria.diary.data.Diary
import ink.terraria.diary.ui.AppViewModelProvider
import ink.terraria.diary.ui.navigation.NavigationDestination


object DiaryDetailDestination : NavigationDestination {
    override val route: String = "diary_detail"
    override val titleRes: Int = R.string.diary
    const val diaryIdArg = "diaryId"
    const val newDiaryArg = "newDiary"
    val routeWithArgs = "$route/{$diaryIdArg}/{$newDiaryArg}"

}

@Composable
fun DiaryDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryDetailViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState = viewModel.uiState
    Scaffold(
        modifier = modifier,
        topBar = {

        }) { paddingValues ->
        DiaryDetailBody(
            diaryDetailUiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onDiaryChange = viewModel::updateUiState
        )
    }
}

@Composable
fun DiaryDetailBody(
    diaryDetailUiState: DiaryDetailUiState,
    onDiaryChange: (Diary) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        BasicTextField(
            value = diaryDetailUiState.diary.title,
            onValueChange = {
                onDiaryChange(diaryDetailUiState.diary.copy(title = it))
            },
            singleLine = true,
            minLines = 1,
            maxLines = 1,
            textStyle = MaterialTheme.typography.headlineMedium,
            decorationBox = { innerTextField ->
                Box {
                    if (diaryDetailUiState.diary.title.isEmpty()) {
                        Text(
                            text = stringResource(R.string.title),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    innerTextField()
                }
            }

        )

        val formattedDate = java.text.SimpleDateFormat(
            stringResource(R.string.local_datetime),
            java.util.Locale.CHINA
        ).format(diaryDetailUiState.diary.date)

        Text(
            text = formattedDate,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        BasicTextField(
            value = diaryDetailUiState.diary.content,
            onValueChange = {
                onDiaryChange(diaryDetailUiState.diary.copy(content = it))
            },
            singleLine = false,
            minLines = 1,
            maxLines = Int.MAX_VALUE,
            textStyle = MaterialTheme.typography.bodyLarge,
            decorationBox = { innerTextField ->
                Box {
                    if (diaryDetailUiState.diary.content.isEmpty()) {
                        Text(
                            text = stringResource(R.string.content_tip),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
