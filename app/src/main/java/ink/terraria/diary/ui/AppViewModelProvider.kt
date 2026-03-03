package ink.terraria.diary.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ink.terraria.diary.DiaryBookApplication
import ink.terraria.diary.ui.home.HomeViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            HomeViewModel(DiaryBookApplication().appContainer.diaryRepository)
        }

    }
}

fun CreationExtras.DiaryBookApplication(): DiaryBookApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DiaryBookApplication
}
