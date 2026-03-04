package ink.terraria.diary.data

import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    suspend fun insertDiary(diary: Diary): Long

    suspend fun updateDiary(diary: Diary)

    suspend fun deleteDiary(diary: Diary)

    suspend fun getDiary(id: Int): Diary

    fun getAllDairiesStream(): Flow<List<Diary>>
}
