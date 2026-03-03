package ink.terraria.diary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(diary: Diary)

    @Update
    suspend fun update(diary: Diary)

    @Delete
    suspend fun delete(diary: Diary)

    @Query("SELECT * FROM diaries WHERE id = :id")
    fun getDiary(id: Int): Flow<Diary>

    @Query("SELECT * FROM diaries ORDER BY date DESC")
    fun getAllDairies(): Flow<List<Diary>>
}
