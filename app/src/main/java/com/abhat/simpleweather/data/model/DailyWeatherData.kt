package com.abhat.simpleweather.data.model

import com.google.gson.annotations.SerializedName

data class DailyWeatherData(
    @SerializedName("date") val date: Long,
    @SerializedName("sunrise") val sunriseTime: Long,
    @SerializedName("sunset") val sunsetTime: Long,
    @SerializedName("moonrise") val moonRiseTime: Long,
    @SerializedName("moonset") val moonSetTime: Long,
    @SerializedName("moon_phase") val moonPhase: Float,
    @SerializedName("temp") val temp: Temp,
    @SerializedName("feels_like") val feelsLike: FeelsLike,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("dew_point") val dewPoint: Float,
    @SerializedName("uvi") val uvi: Float,
    @SerializedName("clouds") val clouds: Int,
    @SerializedName("wind_speed") val windSpeed: Float,
    @SerializedName("wind_deg") val windDegree: Int,
    @SerializedName("weather") val weatherMeta: WeatherMeta,

)

data class Temp(
    @SerializedName("day") val day: Float,
    @SerializedName("min") val min: Float,
    @SerializedName("max") val max: Float,
    @SerializedName("night") val night: Float,
    @SerializedName("eve") val evening: Float,
    @SerializedName("morn") val morning: Float
)

data class FeelsLike(
    @SerializedName("day") val day: Float,
    @SerializedName("night") val night: Float,
    @SerializedName("eve") val evening: Float,
    @SerializedName("morn") val morning: Float
)
