package ink.terraria.diary.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ink.terraria.diary.data.Diary
import ink.terraria.diary.data.DiaryRepository
import kotlinx.coroutines.launch
import java.util.Date

class DiaryDetailViewModel(
    private val diaryRepository: DiaryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val diaryId: Int = checkNotNull(savedStateHandle[DiaryDetailDestination.diaryIdArg])
    var newDiary: Boolean = checkNotNull(savedStateHandle[DiaryDetailDestination.newDiaryArg])

    var uiState by mutableStateOf(
        if (newDiary) DiaryDetailUiState(editing = true, newDiary = true) else DiaryDetailUiState()
    )
        private set

    init {
        if (!newDiary) {
            viewModelScope.launch {
                val diary = diaryRepository.getDiary(diaryId)
                uiState = uiState.copy(diary = diary, canSave = validateInput(diary))
            }
        }
    }

    fun saveDiary(diary: Diary) {
        viewModelScope.launch {
            if (newDiary) {
                diaryRepository.insertDiary(diary)
                uiState.newDiary = false
            } else {
                diaryRepository.updateDiary(diary)
            }
            updateUiState(uiState.diary)
            updateEditing(false)
        }
    }

    fun updateUiState(diary: Diary) {
        uiState = uiState.copy(diary = diary, canSave = validateInput(diary))
    }

    fun updateEditing(editing: Boolean) {
        uiState = uiState.copy(editing = editing)
    }

    fun validateInput(diary: Diary): Boolean {
        return diary.title.isNotBlank() && diary.content.isNotBlank()
    }

    fun showDatePicker(show: Boolean) {
        uiState = uiState.copy(showDatePicker = show)
    }

    fun showPhotoPicker(show: Boolean) {
        uiState = uiState.copy(showPhotoPicker = show)
    }

    fun showNetworkPhotoPicker(show: Boolean) {
        uiState = uiState.copy(showNetworkPhotoPicker = show)
    }

    fun showWeatherPicker(show: Boolean) {
        uiState = uiState.copy(showWeatherPicker = show)
    }

    fun showDeleteAlert(show: Boolean) {
        uiState = uiState.copy(showDeleteAlert = show)
    }

    fun deleteDiary() {
        viewModelScope.launch {
            diaryRepository.deleteDiary(uiState.diary)
        }
    }

}

data class DiaryDetailUiState(
    val diary: Diary = Diary(0, "", "", "", "", "", Date()),
    var editing: Boolean = false,
    var newDiary: Boolean = false,
    var canSave: Boolean = false,
    var showDatePicker: Boolean = false,
    var showPhotoPicker: Boolean = false,
    var showNetworkPhotoPicker: Boolean = false,
    var showWeatherPicker: Boolean = false,
    var showDeleteAlert: Boolean = false
)
