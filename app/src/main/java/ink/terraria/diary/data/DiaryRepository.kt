package ink.terraria.diary.data

import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    suspend fun insertDiary(diary: Diary)

    suspend fun updateDiary(diary: Diary)

    suspend fun deleteDiary(diary: Diary)

    fun getDiary(id: Int): Diary

    fun getAllDairiesStream(): Flow<List<Diary>>
}
