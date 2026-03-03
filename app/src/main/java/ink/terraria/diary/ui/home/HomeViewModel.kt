package ink.terraria.diary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ink.terraria.diary.data.Diary
import ink.terraria.diary.data.DiaryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val diaryRepository: DiaryRepository) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> = diaryRepository.getAllDairiesStream()
        .map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeUiState()
        )

}

data class HomeUiState(
    val diaries: List<Diary> = listOf()
)
