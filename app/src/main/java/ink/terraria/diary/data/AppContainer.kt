package ink.terraria.diary.data

import android.content.Context

interface AppContainer {
    val diaryRepository: DiaryRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val diaryRepository: DiaryRepository by lazy {
        LocalDiaryRepository(DiaryBookDatabase.getDatabase(context).diaryDao())
    }
}
