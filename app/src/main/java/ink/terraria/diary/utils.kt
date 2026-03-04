package ink.terraria.diary

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Date.toLocalString(): String {
    return SimpleDateFormat(
        stringResource(R.string.local_datetime),
        Locale.getDefault()
    ).format(this)
}
