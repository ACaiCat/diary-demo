package ink.terraria.diary.ui.detail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import ink.terraria.diary.R
import ink.terraria.diary.data.Diary
import ink.terraria.diary.network.WeatherApi
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
    val context = LocalContext.current
    if (uiState.newDiary) {
        TryGetWeather(context, viewModel)
    }

    Scaffold(
        modifier = modifier, topBar = {
            DetailTopBar(
                uiState = uiState,
                onSaveClick = { viewModel.saveDiary(uiState.diary) },
                onEditClick = { viewModel.updateEditing(true) },
                onSelectDateClick = { viewModel.showDatePicker(true) },
                onInsertPhotoClick = { viewModel.showPhotoPicker(true) },
                onPickWeatherClick = { viewModel.showWeatherPicker(true) },
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
            onWeatherEditDismiss = { viewModel.showWeatherPicker(false) },
            onWeatherEditConfirm = {
                viewModel.showWeatherPicker(false)
                viewModel.updateUiState(uiState.diary.copy(weather = it))
            },
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                )
                .padding(horizontal = 24.dp)
        )


    }
}

@Composable
fun TryGetWeather(context: Context, viewModel: DiaryDetailViewModel) {
    val weatherLangCode = stringResource(R.string.weather_lang_code)
    val hasLocationPermission = remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            hasLocationPermission.value = true
        }
    }
    LaunchedEffect(hasLocationPermission.value) {
        if (!hasLocationPermission.value) {
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            return@LaunchedEffect
        }
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@LaunchedEffect
        }
        try {
            val location =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (location != null) {
                val weatherResponse = WeatherApi.weatherApiService.getCurrentWeather(
                    "${"%.2f".format(location.longitude)},${"%.2f".format(location.latitude)}",
                    weatherLangCode
                )
                val weatherText = weatherResponse.now.text
                viewModel.updateUiState(viewModel.uiState.diary.copy(weather = weatherText))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
    onPickWeatherClick: () -> Unit,
    navigationBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    TopAppBar(modifier = modifier, title = {}, navigationIcon = {
        TooltipBox(
            TooltipDefaults.rememberTooltipPositionProvider(
                TooltipAnchorPosition.Above
            ), tooltip = {
                PlainTooltip { Text(stringResource(R.string.back)) }
            }, state = rememberTooltipState()
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
    }, actions = {
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
            IconButton(onClick = onPickWeatherClick) {
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
    })

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
    onWeatherEditDismiss: () -> Unit,
    onWeatherEditConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (uiState.showDatePicker) {
            DiaryDatePicker(
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

        if (uiState.showWeatherPicker) {
            WeatherPicker(
                onWeatherEditDismiss = onWeatherEditDismiss,
                onWeatherEditConfirm = onWeatherEditConfirm
            )
        }
        DiaryEditField(
            uiState = uiState,
            onDiaryChange = onDiaryChange,
        )
    }
}


@Composable
fun DiaryEditField(
    uiState: DiaryDetailUiState, onDiaryChange: (Diary) -> Unit, modifier: Modifier = Modifier
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

            textStyle = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface),
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
            })

        Row {
            Text(
                text = uiState.diary.date.toLocalString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (uiState.diary.weather.isNotEmpty()) {
                Text(
                    text = " | ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = uiState.diary.weather,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

        }


        BasicTextField(
            value = uiState.diary.content,
            onValueChange = {
                onDiaryChange(uiState.diary.copy(content = it))
            },
            singleLine = false,
            minLines = 1,
            maxLines = Int.MAX_VALUE,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
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
            })

        DiaryPhoto(
            uiState = uiState,
            onDiaryChange = onDiaryChange,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.padding(vertical = 16.dp))
    }
}

@Composable
fun DiaryPhoto(
    uiState: DiaryDetailUiState, onDiaryChange: (Diary) -> Unit, modifier: Modifier = Modifier
) {
    val showPhotoDeleteAlert = remember(uiState.diary.id) { mutableStateOf(false) }

    if (!uiState.diary.imageUrl.isEmpty() || !uiState.diary.imagePath.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth()
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
                    }, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer, shape = CircleShape
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
            })
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
        onPickWeatherClick = {})
}
