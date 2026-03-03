package ink.terraria.diary

import android.app.Application
import ink.terraria.diary.data.AppContainer
import ink.terraria.diary.data.AppDataContainer

class DiaryBookApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
    }
}
