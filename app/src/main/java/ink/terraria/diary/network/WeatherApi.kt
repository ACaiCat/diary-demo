package ink.terraria.diary.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import ink.terraria.diary.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient

private val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-QW-Api-Key", BuildConfig.QWEATHER_API_KEY)
            .build()
        println(request.url)
        chain.proceed(request)
    }
    .build()

private val retrofit = retrofit2.Retrofit.Builder()
    .baseUrl(BuildConfig.QWEATHER_BASE_URL)
    .client(client)
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .build()

object WeatherApi {
    val weatherApiService: WeatherApiService = retrofit.create(WeatherApiService::class.java)
}
