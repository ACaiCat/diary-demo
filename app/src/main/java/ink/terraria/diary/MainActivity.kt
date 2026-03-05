package ink.terraria.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import ink.terraria.diary.ui.navigation.DiaryBookNavHost
import ink.terraria.diary.ui.theme.DiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiaryTheme {
                val navController = rememberNavController()
                DiaryBookNavHost(
                    navController = navController,
                )
            }
        }
    }
}

