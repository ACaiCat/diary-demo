package ink.terraria.diary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Diary::class], version = 3, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class DiaryBookDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao

    companion object {
        @Volatile
        private var Instance: DiaryBookDatabase? = null
        fun getDatabase(context: Context): DiaryBookDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context, DiaryBookDatabase::class.java, "diary_book_database"
                ).fallbackToDestructiveMigration(true).build().also { Instance = it }
            }
        }
    }
}
