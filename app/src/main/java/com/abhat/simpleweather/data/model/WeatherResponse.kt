package com.abhat.simpleweather.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("lat") val lat: Float,
    @SerializedName("lon") val lon: Float,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("timezone_offset") val timezoneOffset: Int,
    @SerializedName("current") val currentDayWeather: WeatherData,
    @SerializedName("daily") val dailyWeatherData: List<DailyWeatherData>,
    @SerializedName("hourly") val hourlyWeatherData: List<WeatherData>
)