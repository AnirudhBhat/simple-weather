package com.abhat.simpleweather.data.model

import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("date") val date: Long,
    @SerializedName("sunrise") val sunriseTime: Long,
    @SerializedName("sunset") val sunsetTime: Long,
    @SerializedName("temp") val temp: Float,
    @SerializedName("feels_like") val feelsLike: Float,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("dew_point") val dewPoint: Float,
    @SerializedName("uvi") val uvi: Float,
    @SerializedName("clouds") val clouds: Int,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("wind_speed") val windSpeed: Float,
    @SerializedName("wind_deg") val windDegree: Int,
    @SerializedName("weather") val weatherMeta: WeatherMeta
)
