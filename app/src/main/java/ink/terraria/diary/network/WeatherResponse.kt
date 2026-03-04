package ink.terraria.diary.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("code") val code: String,
    @SerialName("updateTime") val updateTime: String,
    @SerialName("fxLink") val fxLink: String,
    @SerialName("now") val now: Now,
    @SerialName("refer") val refer: Refer
)

@Serializable
data class Now(
    @SerialName("obsTime") val obsTime: String,
    @SerialName("temp") val temp: String,
    @SerialName("feelsLike") val feelsLike: String,
    @SerialName("icon") val icon: String,
    @SerialName("text") val text: String,
    @SerialName("wind360") val wind360: String,
    @SerialName("windDir") val windDir: String,
    @SerialName("windScale") val windScale: String,
    @SerialName("windSpeed") val windSpeed: String,
    @SerialName("humidity") val humidity: String,
    @SerialName("precip") val precip: String,
    @SerialName("pressure") val pressure: String,
    @SerialName("vis") val vis: String,
    @SerialName("cloud") val cloud: String,
    @SerialName("dew") val dew: String
)

@Serializable
data class Refer(
    @SerialName("sources") val sources: List<String>,
    @SerialName("license") val license: List<String>
)
