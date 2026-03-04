package ink.terraria.diary.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("/v7/weather/now")
    suspend fun getCurrentWeather(
        @Query("location") location: String,
        @Query("lang") lang: String,
    ): WeatherResponse
}
