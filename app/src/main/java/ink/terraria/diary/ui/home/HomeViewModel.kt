package ink.terraria.diary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ink.terraria.diary.data.Diary
import ink.terraria.diary.data.DiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(diaryRepository: DiaryRepository) : ViewModel() {

    private val _allDiaries: StateFlow<List<Diary>> = diaryRepository.getAllDairiesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = listOf()
        )

    val searchQuery = MutableStateFlow("")

    val homeUiState: StateFlow<HomeUiState> = combine(_allDiaries, searchQuery) { diaries, query ->
        val filtered = if (query.isBlank()) diaries
        else diaries.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.content.contains(query, ignoreCase = true)
        }
        HomeUiState(diaries = filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState()
    )

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }
}

data class HomeUiState(
    val diaries: List<Diary> = listOf(),
)
