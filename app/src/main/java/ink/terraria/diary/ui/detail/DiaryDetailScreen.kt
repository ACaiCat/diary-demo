package ink.terraria.diary.ui.detail

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import ink.terraria.diary.R
import ink.terraria.diary.data.Diary
import ink.terraria.diary.isHttpUrl
import ink.terraria.diary.toLocalString
import ink.terraria.diary.ui.AppViewModelProvider
import ink.terraria.diary.ui.navigation.NavigationDestination
import java.util.Date


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
    navigationBack: () -> Unit,
    viewModel: DiaryDetailViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState = viewModel.uiState
    Scaffold(
        modifier = modifier,
        topBar = {
            DetailTopBar(
                uiState = uiState,
                onSaveClick = { viewModel.saveDiary(uiState.diary) },
                onEditClick = { viewModel.updateEditing(true) },
                onSelectDateClick = { viewModel.showDatePicker(true) },
                onInsertPhotoClick = { viewModel.showPhotoPicker(true) },
                navigationBack = navigationBack
            )

        }) { paddingValues ->
        DiaryDetailBody(
            uiState = uiState,
            onDiaryChange = viewModel::updateUiState,
            onDateEditConfirm = {
                viewModel.updateUiState(uiState.diary.copy(date = it))
                viewModel.showDatePicker(false)
            },
            onDateEditDismiss = { viewModel.showDatePicker(false) },
            onPhotoEditDismiss = {
                viewModel.showPhotoPicker(false)
                viewModel.showNetworkPhotoPicker(false)
            },
            onLocalPhotoConfirm = {
                viewModel.updateUiState(uiState.diary.copy(imagePath = it, imageUrl = ""))
                viewModel.showPhotoPicker(false)
            },
            onNetworkPhotoConfirm = {
                viewModel.updateUiState(uiState.diary.copy(imageUrl = it, imagePath = ""))
                viewModel.showNetworkPhotoPicker(false)
            },
            onNetworkPhotoRequest = {
                viewModel.showPhotoPicker(false)
                viewModel.showNetworkPhotoPicker(true)
            },
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    uiState: DiaryDetailUiState,
    onSaveClick: () -> Unit,
    onEditClick: () -> Unit,
    onSelectDateClick: () -> Unit,
    onInsertPhotoClick: () -> Unit,
    navigationBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    TopAppBar(
        modifier = modifier,
        title = {},
        navigationIcon = {
            TooltipBox(
                TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above
                ),
                tooltip = {
                    PlainTooltip { Text(stringResource(R.string.back)) }
                },
                state = rememberTooltipState()
            ) {
                IconButton(
                    onClick = navigationBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        actions = {
            if (uiState.editing) {
                IconButton(onClick = onInsertPhotoClick) {
                    Icon(
                        imageVector = Icons.Default.InsertPhoto,
                        contentDescription = stringResource(R.string.insert_photo)
                    )
                }
                IconButton(onClick = onSelectDateClick) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.select_date)
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = stringResource(R.string.weather)
                    )
                }

                IconButton(onClick = onSaveClick, enabled = uiState.canSave) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            } else {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )

}

@Composable
fun DiaryDetailBody(
    uiState: DiaryDetailUiState,
    onDateEditDismiss: () -> Unit,
    onDateEditConfirm: (Date) -> Unit,
    onDiaryChange: (Diary) -> Unit,
    onPhotoEditDismiss: () -> Unit,
    onLocalPhotoConfirm: (String) -> Unit,
    onNetworkPhotoRequest: () -> Unit,
    onNetworkPhotoConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (uiState.showDatePicker) {
            DiaryDateSelector(
                uiState = uiState,
                onDateEditDismiss = onDateEditDismiss,
                onDateEditConfirm = onDateEditConfirm
            )
        }

        if (uiState.showPhotoPicker) {
            PhotoPicker(
                uiState = uiState,
                onPhotoEditDismiss = onPhotoEditDismiss,
                onPhotoEditConfirm = onLocalPhotoConfirm,
                onNetworkPhotoRequest = onNetworkPhotoRequest,
            )
        }

        if (uiState.showNetworkPhotoPicker) {
            NetWorkPhotoPicker(
                uiState = uiState,
                onPhotoEditDismiss = onPhotoEditDismiss,
                onPhotoEditConfirm = onNetworkPhotoConfirm
            )
        }

        DiaryEditField(
            uiState = uiState,
            onDiaryChange = onDiaryChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPicker(
    uiState: DiaryDetailUiState,
    onPhotoEditDismiss: () -> Unit,
    onPhotoEditConfirm: (String) -> Unit,
    onNetworkPhotoRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            onPhotoEditConfirm(it.toString())
        }
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onPhotoEditDismiss,
        title = {
            if (uiState.diary.imageUrl.isEmpty() && uiState.diary.imagePath.isEmpty()) {
                Text(stringResource(R.string.insert_photo))
            } else {
                Text(stringResource(R.string.replace_photo))
            }
        },
        text = { Text(stringResource(R.string.pick_photo_tip)) },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = {
                    launcher.launch("image/*")
                }) {
                    Text(stringResource(R.string.local_photo))
                }
                TextButton(onClick = {
                    onNetworkPhotoRequest()
                }) {
                    Text(stringResource(R.string.network_photo))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onPhotoEditDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun NetWorkPhotoPicker(
    uiState: DiaryDetailUiState,
    onPhotoEditDismiss: () -> Unit,
    onPhotoEditConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var imgUrl by remember { mutableStateOf(uiState.diary.imageUrl) }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onPhotoEditDismiss,
        title = {
            Text(stringResource(R.string.network_photo))
        },
        text = {
            TextField(
                value = imgUrl,
                onValueChange = {
                    imgUrl = it
                },
                placeholder = { Text(stringResource(R.string.network_photo_tip)) },
                label = { Text(stringResource(R.string.network_photo_url)) },
                isError = !isHttpUrl(imgUrl),
                singleLine = true,
                maxLines = 1
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onPhotoEditConfirm(imgUrl)
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onPhotoEditDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun DiaryDateSelector(
    uiState: DiaryDetailUiState,
    onDateEditDismiss: () -> Unit,
    onDateEditConfirm: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val timePickerState = rememberDatePickerState()
    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDateEditDismiss,
        dismissButton = {
            TextButton(onClick = onDateEditDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDateEditConfirm(
                    timePickerState.selectedDateMillis?.let { Date(it) }
                        ?: uiState.diary.date
                )
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
    ) {
        DatePicker(
            state = timePickerState,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun DiaryEditField(
    uiState: DiaryDetailUiState,
    onDiaryChange: (Diary) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        BasicTextField(
            value = uiState.diary.title,
            onValueChange = {
                onDiaryChange(uiState.diary.copy(title = it))
            },
            singleLine = true,
            minLines = 1,
            maxLines = 1,
            textStyle = MaterialTheme.typography.headlineMedium,
            enabled = uiState.editing,
            decorationBox = { innerTextField ->
                Box {
                    if (uiState.diary.title.isEmpty()) {
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

        Text(
            text = uiState.diary.date.toLocalString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        BasicTextField(
            value = uiState.diary.content,
            onValueChange = {
                onDiaryChange(uiState.diary.copy(content = it))
            },
            singleLine = false,
            minLines = 1,
            maxLines = Int.MAX_VALUE,
            textStyle = MaterialTheme.typography.bodyLarge,
            enabled = uiState.editing,
            decorationBox = { innerTextField ->
                Box {
                    if (uiState.diary.content.isEmpty()) {
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

        DiaryPhoto(
            uiState = uiState,
            onDiaryChange = onDiaryChange,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun DiaryPhoto(
    uiState: DiaryDetailUiState,
    onDiaryChange: (Diary) -> Unit,
    modifier: Modifier = Modifier
) {
    val showPhotoDeleteAlert = remember { mutableStateOf(false) }

    if (!uiState.diary.imageUrl.isEmpty() || !uiState.diary.imagePath.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = uiState.diary.imageUrl.ifEmpty { uiState.diary.imagePath },
                contentDescription = stringResource(R.string.diary_photo),
                error = painterResource(R.drawable.img_fallback),
                fallback = painterResource(R.drawable.img_fallback),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = MaterialTheme.shapes.medium)
            )

            if (uiState.editing) {
                IconButton(
                    onClick = {
                        showPhotoDeleteAlert.value = true
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.delete_photo),
                        tint = MaterialTheme.colorScheme.error
                    )

                }
            }
        }
    }

    if (showPhotoDeleteAlert.value) {
        AlertDialog(
            onDismissRequest = {
                showPhotoDeleteAlert.value = false
            },
            title = { Text(stringResource(R.string.delete_photo)) },
            text = { Text(stringResource(R.string.delete_photo_tip)) },
            confirmButton = {
                TextButton(onClick = {
                    onDiaryChange(uiState.diary.copy(imagePath = "", imageUrl = ""))
                    showPhotoDeleteAlert.value = false
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoDeleteAlert.value = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Preview
@Composable
fun DetailTopBarPreview() {
    DetailTopBar(
        uiState = DiaryDetailUiState(editing = true),
        onSaveClick = {},
        onEditClick = {},
        onSelectDateClick = {},
        onInsertPhotoClick = {},
        navigationBack = {},
    )
}
