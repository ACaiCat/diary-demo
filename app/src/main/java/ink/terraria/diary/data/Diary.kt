package ink.terraria.diary.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "diaries")
data class Diary(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val content: String,
    val localPath: String,
    val imageUrl: String,
    val weather: String,
    val date: Date
)
