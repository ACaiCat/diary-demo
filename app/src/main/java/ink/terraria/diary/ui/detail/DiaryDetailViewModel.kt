package ink.terraria.diary.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ink.terraria.diary.data.Diary
import ink.terraria.diary.data.DiaryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class DiaryDetailViewModel(
    private val diaryRepository: DiaryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val diaryId: Int = checkNotNull(savedStateHandle[DiaryDetailDestination.diaryIdArg])
    var newDiary: Boolean = checkNotNull(savedStateHandle[DiaryDetailDestination.newDiaryArg])

    private val diaryDetailUiState = if (newDiary) {
        DiaryDetailUiState()
    } else {
        DiaryDetailUiState(diaryRepository.getDiary(diaryId))
    }

    var uiState by mutableStateOf(diaryDetailUiState)
        private set

    fun saveDiary(diary: Diary) {
        viewModelScope.launch {
            if (newDiary) {
                diaryRepository.insertDiary(diary)
                uiState.newDiary = false
            } else {
                diaryRepository.updateDiary(diary)
            }
            uiState.editing = false
            updateUiState(uiState.diary)
        }
    }

    fun updateUiState(diary: Diary) {
        uiState = uiState.copy(diary = diary, canSave = validateInput(diary))
    }

    fun validateInput(diary: Diary): Boolean {
        return diary.title.isNotBlank() && diary.content.isNotBlank()
    }

}

data class DiaryDetailUiState(
    val diary: Diary = Diary(0, "", "", "", "", "", Date()),
    var editing: Boolean = false,
    var newDiary: Boolean = false,
    var canSave: Boolean = false
)
