package ink.terraria.diary.data

import kotlinx.coroutines.flow.Flow

class LocalDiaryRepository(private val diaryDao: DiaryDao): DiaryRepository {
    override suspend fun insertDiary(diary: Diary) = diaryDao.insert(diary)

    override suspend fun updateDiary(diary: Diary) = diaryDao.update(diary)

    override suspend fun deleteDiary(diary: Diary) = diaryDao.delete(diary)

    override suspend fun getDiary(id: Int): Diary = diaryDao.getDiary(id)

    override fun getAllDairiesStream(): Flow<List<Diary>> = diaryDao.getAllDairies()
}
