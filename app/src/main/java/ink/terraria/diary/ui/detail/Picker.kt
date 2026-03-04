package ink.terraria.diary.ui.detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import ink.terraria.diary.R
import ink.terraria.diary.isHttpUrl
import ink.terraria.diary.network.Now
import ink.terraria.diary.network.WeatherApi
import java.util.Date


private sealed class WeatherState {
    data object NoPermission : WeatherState()
    data object GettingLocation : WeatherState()
    data object LoadingWeather : WeatherState()
    data object LocationFailed : WeatherState()
    data object WeatherFailed : WeatherState()
    data class Success(val weather: Now) : WeatherState()
}

@Composable
fun WeatherPicker(
    onWeatherEditDismiss: () -> Unit,
    onWeatherEditConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasLocationPermission = remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val gettingLocation = remember {
        mutableStateOf(false)
    }
    val loadingWeather = remember {
        mutableStateOf(false)
    }
    val weather = remember {
        mutableStateOf<Now?>(null)
    }
    val noPermissionText = stringResource(R.string.no_location_permission)
    val weatherLangCode = stringResource(R.string.weather_lang_code)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            hasLocationPermission.value = true
        } else {
            Toast.makeText(context, noPermissionText, Toast.LENGTH_SHORT).show()
        }

    }

    val location = remember {
        mutableStateOf<android.location.Location?>(null)
    }
    LaunchedEffect(hasLocationPermission.value) {
        weather.value = null
        location.value = null

        if (!hasLocationPermission.value) {
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            return@LaunchedEffect
        }
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@LaunchedEffect
        }

        gettingLocation.value = true
        try {
            location.value =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

            if (location.value != null) {
                loadingWeather.value = true
                val weatherResponse =
                    WeatherApi.weatherApiService.getCurrentWeather(
                        "${"%.2f".format(location.value?.longitude)},${"%.2f".format(location.value?.latitude)}",
                        weatherLangCode
                    )
                weather.value = weatherResponse.now
                loadingWeather.value = false
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {
            gettingLocation.value = false
        }
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onWeatherEditDismiss,
        title = { Text(stringResource(R.string.weather)) },
        text = {
            val weatherState: WeatherState = when {
                !hasLocationPermission.value -> WeatherState.NoPermission
                gettingLocation.value -> WeatherState.GettingLocation
                location.value == null -> WeatherState.LocationFailed
                loadingWeather.value -> WeatherState.LoadingWeather
                weather.value == null -> WeatherState.WeatherFailed
                else -> WeatherState.Success(weather.value!!)
            }
            when (weatherState) {
                is WeatherState.NoPermission -> Text(
                    stringResource(R.string.no_location_permission),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )

                is WeatherState.GettingLocation -> Text(
                    stringResource(R.string.getting_location),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                is WeatherState.LoadingWeather -> Text(
                    stringResource(R.string.loading_weather),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                is WeatherState.LocationFailed -> Text(
                    stringResource(R.string.failed_to_get_location),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )

                is WeatherState.WeatherFailed -> Text(
                    stringResource(R.string.failed_to_get_weather),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )

                is WeatherState.Success -> Text(
                    text = weatherState.weather.text,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onWeatherEditConfirm(weather.value!!.text)
                },
                enabled = weather.value != null
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onWeatherEditDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
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

    var imgUrl by remember(uiState.diary.id) { mutableStateOf(uiState.diary.imageUrl) }
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
            TextButton(
                enabled = isHttpUrl(imgUrl),
                onClick = {
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
fun DiaryDatePicker(
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
